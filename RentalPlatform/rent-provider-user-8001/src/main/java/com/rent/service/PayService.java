package com.rent.service;

import com.rent.pojo.base.Order;
import com.rent.pojo.view.PayNeedMsg;

/**
 * @author w
 */
public interface PayService {
    /**
     * 获取返回信息
     * @param order 部分订单信息
     * @return 需要支付的金额等信息
     */
    PayNeedMsg getPayNeedMsg(Order order) throws Exception;
}
