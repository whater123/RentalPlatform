package com.rent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.OrderLogisticsMapper;
import com.rent.dao.OrderPayMapper;
import com.rent.dao.TradeMapper;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.OrderLogistics;
import com.rent.pojo.base.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    OrderLogisticsMapper orderLogisticsMapper;

    public List<Trade> getThoseTrade(String column, String value){
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<Trade>();
        queryWrapper.eq(column,value);
        return tradeMapper.selectList(queryWrapper);
    }
    public List<OrderLogistics> getThoseOrderLogistics(String column, String value){
        QueryWrapper<OrderLogistics> queryWrapper = new QueryWrapper<OrderLogistics>();
        queryWrapper.eq(column,value);
        return orderLogisticsMapper.selectList(queryWrapper);
    }
}
