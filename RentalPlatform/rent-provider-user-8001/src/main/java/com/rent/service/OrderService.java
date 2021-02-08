package com.rent.service;

import com.rent.pojo.base.Trade;

import java.util.Map;

/**
 * @author w
 */
public interface OrderService {
    /**
     * 插入订单
     * @param trade 接收到的不完整订单信息
     * @return code为1则msg返回错误信息，code为0则超过
     */
    Map<String,String> insertOriginOrder(Trade trade) throws Exception;
}
