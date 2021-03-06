package com.rent.controller.platform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ShiroUtil;
import com.rent.constant.SystemConstant;
import com.rent.dao.AppealMapper;
import com.rent.dao.EnterpriseMapper;
import com.rent.dao.TradeMapper;
import com.rent.dao.UserMapper;
import com.rent.pojo.base.Appeal;
import com.rent.pojo.base.Trade;
import com.rent.pojo.view.ReturnDoubleData;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseService;
import com.rent.service.EntpOrderService;
import com.rent.service.UtilsService;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/manager/platform")
public class PlatAppealController {
    @Autowired
    AppealMapper appealMapper;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    UtilsService utilsService;
    @Autowired
    EntpOrderService entpOrderService;

    @RequestMapping(value = "/getAppeal", produces = "application/json;charset=UTF-8")
    public ReturnDoubleData getAppeal(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnDoubleData("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("platform_manager")){
            return new ReturnDoubleData("302",true,"尚未授权");
        }
        JSONObject jsonObject = JSON.parseObject(json);
        QueryWrapper<Appeal> queryWrapper = new QueryWrapper<Appeal>();
        switch (jsonObject.getInteger("time")){
            case 0:
                break;
            //三个月内
            case 1:
                queryWrapper.ge("appeal_time", MyUtil.getPastDate(Calendar.MONTH,-3));
                break;
            //三个月外
            case 2:
                queryWrapper.lt("appeal_time",MyUtil.getPastDate(Calendar.MONTH,-3));
                break;
            default:
                return new ReturnDoubleData("402", true, "time参数不合法");
        }

        if(jsonObject.getInteger("state") != 0){
            if(jsonObject.getInteger("state") <= 3 && jsonObject.getInteger("state") >= 1 ){
                queryWrapper.eq("appeal_state",jsonObject.getString("state"));
            } else {
                return new ReturnDoubleData("402", true, "state参数不合法");
            }
        }

        List<Appeal> appeals = appealMapper.selectList(queryWrapper);
        List<Appeal> myAppeals = new ArrayList<Appeal>();
        for (Appeal appeal :
                appeals) {
            try{
                Trade trade = entpOrderService.getThoseTrade("order_id",appeal.getAppealGoalId()).get(0);
                appeal.setUser(userMapper.selectById(trade.getUserId()));
                appeal.setEnterprise(enterpriseMapper.selectById(trade.getEntpId()));
                myAppeals.add(appeal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //按页数给消息
        ArrayList<Integer> pageList = new ArrayList<Integer>();
        int page = jsonObject.getInteger("page");
        //获取总页数和当前页数放入pagelist数组
        if(myAppeals.size() == 0){
            pageList.add(0);
            pageList.add(1);
            return new ReturnDoubleData("200",false,"所查找的内容不存在",
                    null,pageList);
        }

        if(myAppeals.size() % SystemConstant.GET_APPEALS_PER_PAGE == 0){
            pageList.add(myAppeals.size() / SystemConstant.GET_APPEALS_PER_PAGE);
        }else {
            pageList.add((myAppeals.size() / SystemConstant.GET_APPEALS_PER_PAGE) + 1);
        }
        pageList.add(page);
        //获取当前页数下的appeal数组
        if(pageList.get(0) < page){
            return new ReturnDoubleData("400",true,"超过总页数");
        }else {
            ArrayList<Appeal> appealsInPage = new ArrayList<Appeal>();
            try{
                for (int i = (page - 1)*SystemConstant.GET_APPEALS_PER_PAGE;
                     i < (page - 1)*SystemConstant.GET_APPEALS_PER_PAGE
                             + SystemConstant.GET_APPEALS_PER_PAGE; i++) {
                    appealsInPage.add(myAppeals.get(i));
                }
            }catch (Exception ignored){}
            return new ReturnDoubleData("200",false,"获取成功",
                    appealsInPage,pageList);
        }
    }

    @RequestMapping(value = "/handleAppeal", produces = "application/json;charset=UTF-8")
    public ReturnMsg handleAppeal(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("platform_manager")){
            return new ReturnMsg("302",true,"尚未授权");
        }
        if(MyUtil.jsonHasVoid(json,"appealId","appealState")){
            return new ReturnMsg("401",true,"传参不齐");
        }
        JSONObject jsonObject = JSON.parseObject(json);
        if(jsonObject.getInteger("appealState") > 3 || jsonObject.getInteger("appealState") < 2){
            return new ReturnMsg("402",true,"appealState参数错误");
        }
        QueryWrapper<Appeal> queryWrapper = new QueryWrapper<Appeal>();
        queryWrapper.eq("appeal_id",jsonObject.getString("appealId"));
        List<Appeal> appeals = appealMapper.selectList(queryWrapper);
        if(appeals.size() == 0){
            return new ReturnMsg("403",true,"该申诉不存在");
        }
        Appeal appeal = appeals.get(0);
        if(appeal.getAppealState() != 1){
            return new ReturnMsg("403",true,"该申诉未处于待处理状态");
        }
        appeal.setAppealState(jsonObject.getInteger("appealState"));
        appealMapper.update(appeal,queryWrapper);

        if(appeal.getAppealState() == 2){
            QueryWrapper<Trade> tradeQueryWrapper = new QueryWrapper<>();
            tradeQueryWrapper.eq("order_id", appeal.getAppealGoalId());
            List<Trade> orders = tradeMapper.selectList(tradeQueryWrapper);
            if(orders.size() == 0){
                return new ReturnMsg("403",true,"该申诉的订单不存在");
            }
            Trade trade = orders.get(0);
            trade.setOrderState(8);
            tradeMapper.update(trade,tradeQueryWrapper);
        }

        return new ReturnMsg("200",false,"处理申诉成功");
    }

}
