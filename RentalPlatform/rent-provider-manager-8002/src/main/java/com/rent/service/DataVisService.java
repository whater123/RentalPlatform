package com.rent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.ContactMapper;
import com.rent.dao.EnterpriseGoodsMapper;
import com.rent.dao.TradeMapper;
import com.rent.dao.UserMapper;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.Trade;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * @author obuivy
 */
@Service
public class DataVisService {
    @Autowired
    EnterpriseGoodsMapper enterpriseGoodsMapper;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    UserMapper userMapper;

    public List<Trade> getPendingOrder(){
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
        queryWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
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
        wrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        List<Trade> trades = tradeMapper.selectList(wrapper);
        String profit = "0.00";
        for (Trade trade :
                trades) {
            profit =  MoneyUtil.fractionAdd(profit,MoneyUtil.fractionSubtract(
                    MoneyUtil.addTail(trade.getOrderTotalMoney()),
                    MoneyUtil.addTail(trade.getOrderDeposit())));
        }

        QueryWrapper<Trade> monthWrapper = new QueryWrapper<Trade>();
        monthWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
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
        weekWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
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
        todayWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
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
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
        queryWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        int goodsCount = enterpriseGoodsMapper.selectCount(queryWrapper);
        int goodsEntityCount = 0;
        int goodsEntityExisting = 0;
        for (EnterpriseGoods goods :
                enterpriseGoodsMapper.selectList(queryWrapper)) {
            goodsEntityCount += goods.getGoodsCount();
            goodsEntityExisting += goods.getGoodsExisting();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("goodsCount",goodsCount);
        jsonObject.put("goodsEntityCount",goodsEntityCount);
        jsonObject.put("goodsEntityExisting",goodsEntityExisting);
        return jsonObject;
    }

    public JSONObject getOrderInfoJSONObject(){
        QueryWrapper<Trade> wrapper = new QueryWrapper<Trade>();
        wrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        int orderCount = tradeMapper.selectCount(wrapper);

        QueryWrapper<Trade> monthWrapper = new QueryWrapper<Trade>();
        monthWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        monthWrapper.between("order_create_time",
                MyUtil.getThisMonthOfFirstDate(0),MyUtil.getThisMonthOfFirstDate(1));
        int orderMonthCount = tradeMapper.selectCount(monthWrapper);

        QueryWrapper<Trade> weekWrapper = new QueryWrapper<Trade>();
        weekWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
        weekWrapper.between("order_create_time",
                MyUtil.getThisWeekOfFirstDate(0),MyUtil.getThisWeekOfFirstDate(1));
        int orderWeekCount = tradeMapper.selectCount(weekWrapper);

        QueryWrapper<Trade> todayWrapper = new QueryWrapper<Trade>();
        todayWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
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

    public JSONArray getOrderCurveMonthlyJSONArray(String json) throws Exception {
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
            queryWrapper.eq("entp_id",String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId()));
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
}
