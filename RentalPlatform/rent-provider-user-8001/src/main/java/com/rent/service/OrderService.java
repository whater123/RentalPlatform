package com.rent.service;

import com.rent.pojo.base.Trade;
import com.rent.pojo.view.LogisticsMsg;
import com.rent.pojo.view.ResBody;

import java.util.List;
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

    /**
     * 获取用户的所有订单信息
     * @param userId 用户id
     * @return 所有的订单信息
     */
    List<Trade> getAllTrades(int userId);

    /**
     * 根据订单id查询物流信息
     * @param orderId 订单id
     * @return 物流信息，数据库数据有误爆500，查询失败返回null
     */
    List<ResBody> getOrderLogistics(String orderId) throws Exception;
}
