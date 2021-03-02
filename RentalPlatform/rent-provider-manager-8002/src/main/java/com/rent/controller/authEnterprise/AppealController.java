package com.rent.controller.authEnterprise;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ShiroUtil;
import com.rent.dao.*;
import com.rent.pojo.base.Appeal;
import com.rent.pojo.base.Trade;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.*;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/manager/authEnterprise")
public class AppealController {
    @Autowired
    AppealMapper appealMapper;
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

    @RequestMapping(value = "/appealOrder", produces = "application/json;charset=UTF-8")
    public ReturnMsg appealOrder(@RequestBody Appeal appeal){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权，尚未认证");
        }
        if (MyUtil.strHasVoid(appeal.getAppealCategory(),
                appeal.getAppealContext(),
                appeal.getAppealGoalId(),
                appeal.getAppealPictureId())){
            return new ReturnMsg("401", true, "传参不齐");
        }
        if (appeal.getAppealCategory().length() > 60){
            return new ReturnMsg("40201", true, "申诉类别过长");
        }
        if (appeal.getAppealCategory().length() < 2){
            return new ReturnMsg("40202", true, "申诉类别过短");
        }
        if(utilsService.getThosePictures("picture_id",appeal.getAppealPictureId()).size() == 0){
            return new ReturnMsg("40303",true,"该申诉图组不存在");
        }
        try{
            if (!utilsService.isNowEntpId(orderService.getThoseTrade("order_id",
                    appeal.getAppealGoalId()).get(0).getEntpId())){
                return new ReturnMsg("302", true, "尚未授权，您无权申诉该订单");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("403", true, "该订单不存在");
        }
        try {
            appeal.setAppealType(2);
            appeal.setAppealState(1);
            appeal.setAppealTime(MyUtil.getNowDateTime());
            appealMapper.insert(appeal);
            return new ReturnMsg("200", false, "申诉成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误");
        }
    }
    @RequestMapping(value = "/getAppeal", produces = "application/json;charset=UTF-8")
    public ReturnMsg getAppeal(){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权，尚未认证");
        }
        try{
            List<Trade> trades = orderService.getThoseTrade("entp_id", String.valueOf(enterpriseService.
                    getThoseEnterprises("entp_account",
                            String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
            List<String> orderIdList = new ArrayList<>();
            for (Trade trade :
                    trades) {
                orderIdList.add(trade.getOrderId());
            }
            QueryWrapper<Appeal> queryWrapper = new QueryWrapper<Appeal>();
            queryWrapper.in("appeal_goal_id",orderIdList);
            return new ReturnMsg("200", false, "获取成功",appealMapper.selectList(queryWrapper));
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误");
        }
    }
}
