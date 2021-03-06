package com.rent.controller.authEnterprise;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ShiroUtil;
import com.rent.constant.SystemConstant;
import com.rent.dao.*;
import com.rent.pojo.base.*;
import com.rent.pojo.view.ReturnDoubleData;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.*;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/manager/authEnterprise")
public class AuthOrderController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EntpGoodsService entpGoodsService;
    @Autowired
    UtilsService utilsService;
    @Autowired
    EnterpriseCategoryService enterpriseCategoryService;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    OrderPayMapper orderPayMapper;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderLogisticsMapper orderLogisticsMapper;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    EnterpriseGoodsEntityMapper enterpriseGoodsEntityMapper;

    @RequestMapping(value = "/getOrder", produces = "application/json;charset=UTF-8")
    public ReturnDoubleData getOrder(@RequestBody String json) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnDoubleData("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnDoubleData("302", true, "尚未授权");
        }
        if (MyUtil.jsonHasVoid(json,"state","page","time")){
            return new ReturnDoubleData("401", true, "传参不齐");
        }

        JSONObject jsonObject = JSON.parseObject(json);
        QueryWrapper<Trade> tradeQW = new QueryWrapper<Trade>();
        tradeQW.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        switch (jsonObject.getInteger("time")){
            case 0:
                break;
            //三个月内
            case 1:
                tradeQW.ge("order_create_time",MyUtil.getPastDate(Calendar.MONTH,-3));
                break;
            //三个月外
            case 2:
                tradeQW.lt("order_create_time",MyUtil.getPastDate(Calendar.MONTH,-3));
                break;
            default:
                return new ReturnDoubleData("402", true, "time参数不合法");
        }
        if(jsonObject.getInteger("state") != 0){
            if(jsonObject.getInteger("state") <= 8 && jsonObject.getInteger("state") >= 1 ){
                tradeQW.eq("order_state",jsonObject.getString("state"));
            } else {
                return new ReturnDoubleData("402", true, "state参数不合法");
            }
        }
        List<Trade> trades = tradeMapper.selectList(tradeQW);
        for (Trade trade :
                trades) {
            trade.setContact(contactMapper.selectById(trade.getContactId()));
            trade.setUser(userMapper.selectById(trade.getUserId()));
            trade.setEnterpriseGoodsEntity(enterpriseGoodsEntityMapper.selectById(trade.getGoodsEntityId()));
            try{
                trade.setEnterpriseGoods(entpGoodsService.getThoseGoods("goods_id",
                        String.valueOf(entpGoodsService.getThoseGoodsEntity("goods_entity_id",
                                String.valueOf(trade.getGoodsEntityId())).get(0).getGoodsId())).get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //按页数给消息
        ArrayList<Integer> pageList = new ArrayList<Integer>();
        int page = jsonObject.getInteger("page");
        //获取总页数和当前页数放入pagelist数组
        if(trades.size() == 0){
            pageList.add(0);
            pageList.add(1);
            return new ReturnDoubleData("200",false,"所查找的内容不存在",
                    null,pageList);
        }

        if(trades.size() % SystemConstant.GET_ORDERS_PER_PAGE == 0){
            pageList.add(trades.size() / SystemConstant.GET_ORDERS_PER_PAGE);
        }else {
            pageList.add((trades.size() / SystemConstant.GET_ORDERS_PER_PAGE) + 1);
        }
        pageList.add(page);
        //获取当前页数下的orders数组
        if(pageList.get(0) < page){
            return new ReturnDoubleData("400",true,"超过总页数");
        }else {
            ArrayList<Trade> tradesInPage = new ArrayList<Trade>();
            try{
                for (int i = (page - 1)*SystemConstant.GET_ORDERS_PER_PAGE;
                     i < (page - 1)*SystemConstant.GET_ORDERS_PER_PAGE
                             + SystemConstant.GET_ORDERS_PER_PAGE; i++) {
                    tradesInPage.add(trades.get(i));
                }
            }catch (Exception ignored){}
            return new ReturnDoubleData("200",false,"获取成功",
                    tradesInPage,pageList);
        }
    }

    @RequestMapping(value = "/deliverGoods", produces = "application/json;charset=UTF-8")
    public ReturnMsg deliverGoods(@RequestBody OrderLogistics orderLogistics){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("30101", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("30201", true, "尚未授权，尚未认证");
        }
        if(MyUtil.strHasVoid(
                orderLogistics.getEntpToUserNumber(),
                orderLogistics.getOrderId())
        ){
            return new ReturnMsg("401", true, "传参不齐");
        }
        Trade trade;
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
        try {
            queryWrapper.eq("order_id",String.valueOf(orderLogistics.getOrderId()));
            trade = tradeMapper.selectList(queryWrapper).get(0);
            if (trade.getOrderState() != 2){
                return new ReturnMsg("40301", true, "该订单未处于已支付状态");
            }
            if (!utilsService.isNowEntpId(trade.getEntpId())) {
                return new ReturnMsg("30202", true, "尚未授权，目标订单无权修改");
            }
        } catch (Exception e) {
            return new ReturnMsg("403", true, "该订单不存在");
        }
        orderLogisticsMapper.insert(orderLogistics);
        trade.setLogisticsId(orderLogistics.getLogisticsId());
        trade.setOrderState(3);
        tradeMapper.update(trade,queryWrapper);
        return new ReturnMsg("200",false,"送货信息提交成功");
    }

    @RequestMapping(value = "/moneyRecord", produces = "application/json;charset=UTF-8")
    public ReturnMsg moneyRecord(){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权，尚未认证");
        }
        List<Trade> trades = orderService.getThoseTrade("entp_id",
                String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                        String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        ArrayList<String> list = new ArrayList<>();
        for (Trade trade :
                trades) {
            list.add(trade.getOrderId());
        }
        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_id",list);
        return new ReturnMsg("200", false, enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpAccountMoney(),
                orderPayMapper.selectList(queryWrapper));
    }

    @RequestMapping(value = "/refundOfDeposit", produces = "application/json;charset=UTF-8")
    public ReturnMsg refundOfDeposit(@RequestBody Trade trade) throws Exception {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权，尚未认证");
        }
        if (MyUtil.strHasVoid(trade.getOrderId())) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        try {
            trade = orderService.getThoseTrade("order_id", trade.getOrderId()).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("403", true, "该订单不存在");
        }
        if (!utilsService.isNowEntpId(trade.getEntpId())) {
            return new ReturnMsg("302", true, "尚未授权，该订单无权修改");
        }
        if (trade.getOrderState() != 5) {
            return new ReturnMsg("403", true, "该订单尚未处于正在退款状态");
        }
        QueryWrapper<Trade> tradeWrapper = new QueryWrapper<Trade>();
        tradeWrapper.eq("order_id", trade.getOrderId());
        QueryWrapper<Enterprise> enterpriseWrapper = new QueryWrapper<Enterprise>();
        enterpriseWrapper.eq("entp_id", trade.getEntpId());
        QueryWrapper<OrderPay> orderPayWrapper = new QueryWrapper<>();
        orderPayWrapper.eq("order_id", trade.getOrderId());
        OrderPay orderPay;
        try {
            orderPay = orderPayMapper.selectList(orderPayWrapper).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("403", true, "该订单还尚未付款");
        }
        Enterprise enterprise;
        try {
            enterprise = enterpriseMapper.selectList(enterpriseWrapper).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("403", true, "该企业不存在");
        }
        String restMoney = MoneyUtil.fractionSubtract(
                MoneyUtil.addTail(enterprise.getEntpAccountMoney()),
                MoneyUtil.addTail(trade.getOrderDeposit()));
        if (MoneyUtil.compare(restMoney, "0.00") == -1) {
            return new ReturnMsg("403", true, "企业余额不足，请充值");
        } else {
            try {
                enterprise.setEntpAccountMoney(restMoney);
                trade.setOrderState(6);
                orderPay.setPayAmount("-" + MoneyUtil.addTail(trade.getOrderDeposit()));
                orderPay.setPayType(4);
                orderPay.setPayPlatform("租赁宝网页版");
                orderPay.setPayTime(MyUtil.getNowDateTime());
                orderPay.setPayScore(0);
                orderPay.setPayId(utilsService.getRandomPayId(enterpriseService.getThoseEnterprises("entp_account",
                        String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
                orderPayMapper.insert(orderPay);
                enterpriseMapper.update(enterprise, enterpriseWrapper);
                tradeMapper.update(trade, tradeWrapper);
                return new ReturnMsg("200", false, "修改成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new ReturnMsg("500", true, "服务器错误", e.getMessage());
            }

        }

    }
}
