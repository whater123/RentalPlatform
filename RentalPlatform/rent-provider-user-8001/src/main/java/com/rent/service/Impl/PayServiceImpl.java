package com.rent.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.*;
import com.rent.pojo.base.Trade;
import com.rent.pojo.base.OrderPay;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.User;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.service.GoodsService;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.PayService;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author w
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    GoodsEntityMapper goodsEntityMapper;
    @Autowired
    OrderPayMapper orderPayMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * 获取支付id
     * @param userId 用户id
     * @return 半随机支付id
     */
    private String getRandomPayId(int userId){
        StringBuilder stringBuilder = new StringBuilder();
        Integer integer = orderPayMapper.selectCount(null);
        stringBuilder.append("RPP");
        stringBuilder.append(integer+1);
        stringBuilder.append(UUID.randomUUID().toString().toUpperCase(), 0, 5);
        stringBuilder.append(userId);
        return String.valueOf(stringBuilder);
    }

    @Override
    public PayNeedMsg getPayNeedMsg(Trade trade) throws Exception {
        if (trade.getOrderRentWay()!=1 && trade.getOrderRentWay()!=2){
            return new PayNeedMsg("rantWay参数错误");
        }
        User user = loginAndRegisterService.getUser(new User(trade.getUserId()));
        EnterpriseGoodsEntity goodsEntityInformation = goodsService.getGoodsEntityInformation(trade.getGoodsEntityId());
        if (user==null||goodsEntityInformation==null){
            return new PayNeedMsg("id参数错误");
        }
        String subDeposit = MoneyUtil.fractionMultiply(MoneyUtil.fractionDivide(user.getUserCreditScore() +".0", "240.0"), goodsEntityInformation.getGoodsDeposit());
        String needDeposit = MoneyUtil.fractionSubtract(goodsEntityInformation.getGoodsDeposit(),subDeposit);
        subDeposit = MoneyUtil.fractionMultiply(subDeposit, trade.getOrderGoodsCount()+".0");
        needDeposit = MoneyUtil.fractionMultiply(needDeposit, trade.getOrderGoodsCount()+".0");
        if (trade.getOrderRentWay()==2){
            //活期支付总额就是押金金额
            return new PayNeedMsg(user.getUserCreditScore(),needDeposit,needDeposit,subDeposit);
        } else if (trade.getOrderRentWay()==1){
            //定期时间单位对应价格获取，如果不存在就返回null
            String[] split = goodsEntityInformation.getGoodsRegularUnit().split("/");
            String[] split1 = goodsEntityInformation.getGoodsRegularPrice().split("/");
            if (split.length!=split1.length){
                return new PayNeedMsg("数据库存储错误");
            }
            boolean flag = false;
            String rentPrice = null;
            for (int i = 0; i < split.length; i++) {
                if (split[i].equals(trade.getOrderRentUnit())){
                    rentPrice = split1[i];
                    flag = true;
                    break;
                }
            }
            if (!flag||rentPrice==null){
                return new PayNeedMsg("orderRentUnit参数错误，不存在此单位");
            }
            //单位价格*几个单位
            String firstPay = MoneyUtil.fractionMultiply(rentPrice+".0", trade.getOrderRentTime() +".0");
            firstPay = MoneyUtil.fractionMultiply(firstPay, trade.getOrderGoodsCount()+".0");
            String goodsOriginPrice = MoneyUtil.fractionMultiply(goodsEntityInformation.getGoodsPrice(), trade.getOrderGoodsCount() +".0");
            //如果第一次支付金额大于原价则返回参数错误
            if (MoneyUtil.compare(firstPay,goodsOriginPrice)==1){
                return new PayNeedMsg("定期支付金额大于商品原价,定期支付金额:"+firstPay+";商品原价"+goodsEntityInformation.getGoodsPrice());
            }
            //需要支付总价=押金+定期价格
            String firstPayAdd = MoneyUtil.fractionAdd(needDeposit, firstPay);
            return new PayNeedMsg(user.getUserCreditScore(),needDeposit,firstPayAdd,subDeposit);
        }else {
            return null;
        }
    }

    @Override
    public Map<String, String> payMsgCheck(OrderPay orderPay) throws Exception {
        Map<String,String> map = new HashMap<>();
        EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(orderPay.getGoodsEntityId());
        if (enterpriseGoodsEntity==null || enterpriseGoodsEntity.getGoodsRentState()<2){
            map.put("code","1");
            map.put("msg","商品个体不存在或不为租赁状态");
            return map;
        }
        Trade trade = tradeMapper.mySelectById(orderPay.getOrderId());
        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderPay.getOrderId());
        List<OrderPay> orderPays = orderPayMapper.selectList(queryWrapper);
        if (orderPays.size()>=1){
            for (OrderPay o: orderPays
                 ) {
                if (o.getPayType()==0&&orderPay.getPayType()==0){
                    map.put("code", "309");
                    map.put("msg", "订单已初付款");
                    return map;
                }
            }
        }
        if (trade ==null) {
            map.put("code", "309");
            map.put("msg", "订单不存在");
            return map;
        }else if (trade.getUserId()!=orderPay.getUserId()){
            map.put("code", "309");
            map.put("msg", "订单提交者付款者不一致");
            return map;
        }else if (trade.getOrderState()!=1&&trade.getOrderState()!=0){
            map.put("code", "309");
            map.put("msg", "订单状态不为待付款，无法支付");
            return map;
        }
        User user = loginAndRegisterService.getUser(new User(trade.getUserId()));
        if (!MoneyUtil.isRuleString(orderPay.getPayAmount())){
            orderPay.setPayAmount(MoneyUtil.addTail(orderPay.getPayAmount()));
        }
        if (!MoneyUtil.isRuleString(user.getUserAccountMoney())){
            user.setUserAccountMoney(MoneyUtil.addTail(user.getUserAccountMoney()));
        }
        if (trade.getLogisticsId()!=0){
            map.put("code","309");
            map.put("msg","订单异常，已有物流信息");
            return map;
        } else if (trade.getOrderStopState()!=0){
            map.put("code","309");
            map.put("msg","订单异常，订单已结束");
            return map;
        }else if (orderPay.getPayType()>2){
            map.put("code","1");
            map.put("msg","payType参数错误");
            return map;
        }else if (MoneyUtil.compare(orderPay.getPayAmount(),user.getUserAccountMoney())==1){
            map.put("code","308");
            map.put("msg","用户余额不足");
            return map;
        } else {
            map.put("code","0");
            return map;
        }
    }

    @Override
    public boolean insertPayAndUpdate(OrderPay orderPay) throws Exception {
        String randomPayId = getRandomPayId(orderPay.getUserId());
        orderPay.setPayId(randomPayId);
        orderPay.setPayTime(MyUtil.getNowTime());
        double addScore = Double.parseDouble(MoneyUtil.fractionMultiply(orderPay.getPayAmount(),"0.09"));
        orderPay.setPayScore(addScore);
        int insert = orderPayMapper.insert(orderPay);
        if (insert!=1){
            return false;
        }
        Trade trade = tradeMapper.mySelectById(orderPay.getOrderId());
        trade.setOrderState(2);
        int i1 = tradeMapper.updateById(trade);
        if (i1!=1){
            orderPayMapper.deleteById(orderPay.getPayId());
            return false;
        }
        User user = userMapper.selectById(orderPay.getUserId());
        if (!MoneyUtil.isRuleString(orderPay.getPayAmount())){
            orderPay.setPayAmount(MoneyUtil.addTail(orderPay.getPayAmount()));
        }
        if (!MoneyUtil.isRuleString(user.getUserAccountMoney())){
            user.setUserAccountMoney(MoneyUtil.addTail(user.getUserAccountMoney()));
        }
        String newAccount = MoneyUtil.fractionSubtract(user.getUserAccountMoney(), orderPay.getPayAmount());
        if (newAccount.contains("-")){
            orderPayMapper.deleteById(orderPay.getPayId());
            return false;
        }
        user.setUserAccountMoney(newAccount);
        user.setUserCreditScore(user.getUserCreditScore()+(int)addScore);
        int i = userMapper.updateById(user);
        if (i!=1){
            orderPayMapper.deleteById(orderPay.getPayId());
            return false;
        }else {
            return true;
        }
    }

    @Override
    public List<OrderPay> getUserAllPays(int userId) {
        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<OrderPay> orderPays = orderPayMapper.selectList(queryWrapper);
        Collections.reverse(orderPays);
        return orderPays;
    }
}
