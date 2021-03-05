package com.rent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.*;
import com.rent.pojo.base.*;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author obuivy
 */
@Service
public class DataVisService {
    @Autowired
    EnterpriseAuthenticationMapper enterpriseAuthenticationMapper;
    @Autowired
    EnterpriseGoodsEntityMapper enterpriseGoodsEntityMapper;
    @Autowired
    EnterpriseGoodsMapper enterpriseGoodsMapper;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    UserMapper userMapper;

    public List<Trade> getPendingOrder(){
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            queryWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        queryWrapper.in("order_state",2,5);
        List<Trade> trades = tradeMapper.selectList(queryWrapper);
        for (Trade trade :
                trades) {
            trade.setContact(contactMapper.selectById(trade.getContactId()));
            trade.setUser(userMapper.selectById(trade.getUserId()));
        }
        return trades;
    }

    public JSONObject getProfitInfoJSONObject() throws Exception {
        QueryWrapper<Trade> wrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            wrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        List<Trade> trades = tradeMapper.selectList(wrapper);
        String profit = "0.00";
        for (Trade trade :
                trades) {
            profit =  MoneyUtil.fractionAdd(profit,MoneyUtil.fractionSubtract(
                    MoneyUtil.addTail(trade.getOrderTotalMoney()),
                    MoneyUtil.addTail(trade.getOrderDeposit())));
        }

        QueryWrapper<Trade> monthWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            monthWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        monthWrapper.between("order_create_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        trades = tradeMapper.selectList(monthWrapper);
        String profitMonth = "0.00";
        for (Trade trade :
                trades) {
            profitMonth =  MoneyUtil.fractionAdd(profitMonth,MoneyUtil.fractionSubtract(
                    MoneyUtil.addTail(trade.getOrderTotalMoney()),
                    MoneyUtil.addTail(trade.getOrderDeposit())));
        }

        QueryWrapper<Trade> weekWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            weekWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        weekWrapper.between("order_create_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        trades = tradeMapper.selectList(weekWrapper);
        String profitWeek = "0.00";
        for (Trade trade :
                trades) {
            profitWeek =  MoneyUtil.fractionAdd(profitWeek,MoneyUtil.fractionSubtract(
                    MoneyUtil.addTail(trade.getOrderTotalMoney()),
                    MoneyUtil.addTail(trade.getOrderDeposit())));
        }

        QueryWrapper<Trade> todayWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            todayWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        todayWrapper.between("order_create_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        trades = tradeMapper.selectList(todayWrapper);
        String profitToday = "0.00";
        for (Trade trade :
                trades) {
            profitToday = MoneyUtil.fractionAdd(profitToday,MoneyUtil.fractionSubtract(
                    MoneyUtil.addTail(trade.getOrderTotalMoney()),
                    MoneyUtil.addTail(trade.getOrderDeposit())));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("profit",profit);
        jsonObject.put("profitToday",profitToday);
        jsonObject.put("profitWeek",profitWeek);
        jsonObject.put("profitMonth",profitMonth);
        return jsonObject;
    }

    public JSONObject getGoodsInfoJSONObejct() {
        int goodsCount;
        int goodsEntityCount = 0;
        int goodsEntityExisting = 0;
        int goodsEntityRegular = 0;
        int goodsEntityCurrent = 0;
        int goodsEntityFix = 0;
        int goodsEntitySell = 0;
        int goodsDay = 0;
        int goodsWeek = 0;
        int goodsMonth = 0;
        int goodsEntityDay = 0;
        int goodsEntityWeek = 0;
        int goodsEntityMonth = 0;


        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            queryWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        goodsCount = enterpriseGoodsMapper.selectCount(queryWrapper);

        for (EnterpriseGoods goods :
                enterpriseGoodsMapper.selectList(queryWrapper)) {
            goodsEntityCount += goods.getGoodsCount();
            goodsEntityExisting += goods.getGoodsExisting();
        }

        queryWrapper.between("goods_create_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        goodsMonth = enterpriseGoodsMapper.selectCount(queryWrapper);
        queryWrapper.between("goods_create_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        goodsWeek = enterpriseGoodsMapper.selectCount(queryWrapper);
        queryWrapper.between("goods_create_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        goodsDay = enterpriseGoodsMapper.selectCount(queryWrapper);

        QueryWrapper<EnterpriseGoodsEntity> queryWrapper1 = new QueryWrapper<EnterpriseGoodsEntity>();
        QueryWrapper<EnterpriseGoodsEntity> queryWrapper2 = new QueryWrapper<EnterpriseGoodsEntity>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            List<EnterpriseGoods> enterpriseGoods = enterpriseGoodsMapper.selectList(queryWrapper);
            List<Integer> list = new ArrayList<Integer>();
            for (EnterpriseGoods goods :
                    enterpriseGoods) {
                list.add(goods.getGoodsId());
            }
            queryWrapper1.in("goods_id",list);
            queryWrapper2.in("goods_id",list);
        }
        queryWrapper1.in("goods_rent_state",2,3,4,5);
        queryWrapper1.select("goods_rent_state","goods_id");
        for (EnterpriseGoodsEntity goodsEntity :
                enterpriseGoodsEntityMapper.selectList(queryWrapper1)) {
            if(goodsEntity.getGoodsRentState() == 2){
                goodsEntityRegular++;
            }
            if(goodsEntity.getGoodsRentState() == 3){
                goodsEntityCurrent++;
            }
            if(goodsEntity.getGoodsRentState() == 4){
                goodsEntityFix++;
            }
            if(goodsEntity.getGoodsRentState() == 5){
                goodsEntitySell++;
            }
        }

        queryWrapper2.between("goods_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        goodsEntityMonth = enterpriseGoodsEntityMapper.selectCount(queryWrapper2);
        queryWrapper2.between("goods_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        goodsEntityWeek = enterpriseGoodsEntityMapper.selectCount(queryWrapper2);
        queryWrapper2.between("goods_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        goodsEntityDay = enterpriseGoodsEntityMapper.selectCount(queryWrapper2);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goodsCount",goodsCount);
        jsonObject.put("goodsEntityCount",goodsEntityCount);
        jsonObject.put("goodsEntityExisting",goodsEntityExisting);
        jsonObject.put("goodsEntityRegular",goodsEntityRegular);
        jsonObject.put("goodsEntityCurrent",goodsEntityCurrent);
        jsonObject.put("goodsEntityFix",goodsEntityFix);
        jsonObject.put("goodsEntitySell",goodsEntitySell);
        jsonObject.put("goodsDay",goodsDay);
        jsonObject.put("goodsWeek",goodsWeek);
        jsonObject.put("goodsMonth",goodsMonth);
        jsonObject.put("goodsEntityDay",goodsEntityDay);
        jsonObject.put("goodsEntityWeek",goodsEntityWeek);
        jsonObject.put("goodsEntityMonth",goodsEntityMonth);
        return jsonObject;
    }

    public JSONObject getOrderInfoJSONObject(){
        QueryWrapper<Trade> wrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            wrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        int orderCount = tradeMapper.selectCount(wrapper);

        QueryWrapper<Trade> monthWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            monthWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        monthWrapper.between("order_create_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        int orderMonthCount = tradeMapper.selectCount(monthWrapper);

        QueryWrapper<Trade> weekWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            weekWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        weekWrapper.between("order_create_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        int orderWeekCount = tradeMapper.selectCount(weekWrapper);

        QueryWrapper<Trade> todayWrapper = new QueryWrapper<Trade>();
        if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
            todayWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        }
        todayWrapper.between("order_create_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        int orderTodayCount = tradeMapper.selectCount(todayWrapper);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderCount",orderCount);
        jsonObject.put("orderMonthCount",orderMonthCount);
        jsonObject.put("orderWeekCount",orderWeekCount);
        jsonObject.put("orderTodayCount",orderTodayCount);
        return jsonObject;
    }

    public JSONArray getOrderCurveMonthlyJsonArray(String json) throws Exception {
        JSONArray jsonArray = new JSONArray();
        List<String> dateList = MyUtil.getDateList(MyUtil.getThisMonthOfFirstDate(
                JSON.parseObject(json).getInteger("month")));
        String date1 = null,date2 = null;
        for (String date:
                dateList) {
            if(date1 == null){
                date1 = date;
                continue;
            }
            if(date2 != null){
                date1 = date2;
                date2 = date;
            }
            if(date2 == null){
                date2 = date;
            }
            QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
            if(!"10086".equals(String.valueOf(SecurityUtils.getSubject().getPrincipal()))){
                queryWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                        String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
            }
            queryWrapper.between("order_create_time", date1, date2);
            List<Trade> trades = tradeMapper.selectList(queryWrapper);
            String profit = "0.00";
            for (Trade trade :
                    trades) {
                profit = MoneyUtil.fractionAdd(profit,MoneyUtil.fractionSubtract(
                        MoneyUtil.addTail(trade.getOrderTotalMoney()),
                        MoneyUtil.addTail(trade.getOrderDeposit())));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date",date1);
            jsonObject.put("orderNumber",trades.size());
            jsonObject.put("profit",profit);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public JSONObject getUserRegisterInfoJSONObject(){
        int userRegisterDay = 0;
        int userRegisterWeek = 0;
        int userRegisterMonth = 0;
        int userRegister = 0;

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();

        queryWrapper.between("user_register_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        userRegisterMonth = userMapper.selectCount(queryWrapper);
        queryWrapper.between("user_register_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        userRegisterWeek = userMapper.selectCount(queryWrapper);
        queryWrapper.between("user_register_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        userRegisterDay = userMapper.selectCount(queryWrapper);

        userRegister = userMapper.selectCount(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userRegister",userRegister);
        jsonObject.put("userRegisterDay",userRegisterDay);
        jsonObject.put("userRegisterWeek",userRegisterWeek);
        jsonObject.put("userRegisterMonth",userRegisterMonth);
        return jsonObject;
    }



    public JSONObject getEnterpriseRegisterInfoJSONObject(){
        int enterpriseRegisterDay = 0;
        int enterpriseRegisterWeek = 0;
        int enterpriseRegisterMonth = 0;
        int enterpriseRegister = 0;

        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();

        queryWrapper.between("entp_register_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        enterpriseRegisterMonth = enterpriseMapper.selectCount(queryWrapper);
        queryWrapper.between("entp_register_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        enterpriseRegisterWeek = enterpriseMapper.selectCount(queryWrapper);
        queryWrapper.between("entp_register_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        enterpriseRegisterDay = enterpriseMapper.selectCount(queryWrapper);

        enterpriseRegister = enterpriseMapper.selectCount(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseRegister",enterpriseRegister);
        jsonObject.put("enterpriseRegisterDay",enterpriseRegisterDay);
        jsonObject.put("enterpriseRegisterWeek",enterpriseRegisterWeek);
        jsonObject.put("enterpriseRegisterMonth",enterpriseRegisterMonth);
        return jsonObject;
    }

    public JSONObject getAuthenticationsInfoJSONObject(){
        int authenticationPending = 0;
        int authenticationDay = 0;
        int authenticationWeek = 0;
        int authenticationMonth = 0;
        int authentication = 0;

        QueryWrapper<EnterpriseAuthentication> queryWrapper = new QueryWrapper<EnterpriseAuthentication>();

        queryWrapper.between("auth_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        authenticationMonth = enterpriseAuthenticationMapper.selectCount(queryWrapper);
        queryWrapper.between("auth_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        authenticationWeek = enterpriseAuthenticationMapper.selectCount(queryWrapper);
        queryWrapper.between("auth_time",
                MyUtil.getNowDate(),MyUtil.getPastDate(Calendar.DATE,1));
        authenticationDay = enterpriseAuthenticationMapper.selectCount(queryWrapper);

        authentication = enterpriseAuthenticationMapper.selectCount(null);

        QueryWrapper<EnterpriseAuthentication> queryWrapper1 = new QueryWrapper<EnterpriseAuthentication>();
        queryWrapper1.eq("auth_state",0);
        authenticationPending = enterpriseAuthenticationMapper.selectCount(queryWrapper1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("authenticationDay",authenticationDay);
        jsonObject.put("authenticationWeek",authenticationWeek);
        jsonObject.put("authenticationMonth",authenticationMonth);
        jsonObject.put("authentication",authentication);
        jsonObject.put("authenticationPending",authenticationPending);
        return jsonObject;
    }
}

