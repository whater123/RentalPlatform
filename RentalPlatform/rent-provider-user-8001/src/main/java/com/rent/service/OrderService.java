package com.rent.service;

import com.rent.pojo.base.OrderLogistics;
import com.rent.pojo.base.Trade;
import com.rent.pojo.view.LogisticsMsg;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.pojo.view.ResBody;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @author w
 */
public interface OrderService {
    /**
     * 插入订单
     * @param trade 接收到的不完整订单信息
     * @return code为1则msg返回错误信息，code为0则成功
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

    /**
     * 订单确认收货
     * @param orderId 订单id
     * @return 成功返回true，失败返回false（订单不存在/状态不为待收货）
     */
    boolean confirmReceipt(String orderId);

    /**
     * 订单续租定期
     * @param trade 续租信息
     * @return code为1则msg返回错误信息，code为0则成功
     */
    Map<String,String> renewalD(Trade trade) throws Exception;

    /**
     * 订单续租活期
     * @param trade 续租信息
     * @return code为1则msg返回错误信息，code为0则成功
     */
    Map<String,String> renewalH(Trade trade) throws Exception;

    /**
     * 获取买断所需信息
     * @param orderId 订单id
     * @return 买断所需信息
     */
    PayNeedMsg getBuyMsg(String orderId) throws Exception;

    /**
     * 填写归还物流信息
     * @param orderLogistics 订单id，归还单号和快递公司
     * @return 是否成功
     */
    boolean updateUserToEntpLo(OrderLogistics orderLogistics);

    /**
     * 获取活期订单已产生费用
     * @param orderId 订单id
     * @return 支付信息
     */
    PayNeedMsg getCurrentPay(String orderId) throws Exception;
}
