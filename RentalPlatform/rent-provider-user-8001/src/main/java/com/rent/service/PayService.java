package com.rent.service;

import com.rent.pojo.base.Trade;
import com.rent.pojo.base.OrderPay;
import com.rent.pojo.view.PayNeedMsg;

import java.util.Map;

/**
 * @author w
 */
public interface PayService {
    /**
     * 获取返回信息
     * @param trade 部分订单信息
     * @return 需要支付的金额等信息
     * @throws Exception 算数异常
     */
    PayNeedMsg getPayNeedMsg(Trade trade) throws Exception;

    /**
     * 检查支付信息
     * @param orderPay 支付信息
     * @return 检查结果map,code"1"为异常，"0"为正常，code"1" msg内有错误信息
     */
    Map<String,String> payMsgCheck(OrderPay orderPay) throws Exception;

    /**
     * 插入支付信息并且更新相关信息
     * @param orderPay 支付信息
     * @return 是否成功
     */
    boolean insertPayAndUpdate(OrderPay orderPay) throws Exception;
}
