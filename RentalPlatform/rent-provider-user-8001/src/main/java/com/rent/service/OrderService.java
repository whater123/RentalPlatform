package com.rent.service;

import com.rent.pojo.base.Order;

/**
 * @author w
 */
public interface OrderService {
    /**
     * 插入订单
     * @param order 接收到的不完整订单信息
     * @return 是否成功
     */
    boolean insertOriginOrder(Order order) throws Exception;
}
