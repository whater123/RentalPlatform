package com.rent.pojo.view;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rent.pojo.base.OrderPay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayNeedMsg {
    private int userCreditScore;
    private String orderNeedDeposit;
    private String orderNeedPay;
    private String orderSubMoney;
    private String errorCode;
    private String orderId;
    private List<OrderPay> payRecord;

    public PayNeedMsg(String orderNeedPay, List<OrderPay> payRecord) {
        this.orderNeedPay = orderNeedPay;
        this.payRecord = payRecord;
    }

    public PayNeedMsg(String orderNeedPay, String orderId) {
        this.orderNeedPay = orderNeedPay;
        this.orderId = orderId;
    }

    public PayNeedMsg(int userCreditScore, String orderNeedDeposit, String orderNeedPay, String orderSubMoney) {
        this.userCreditScore = userCreditScore;
        this.orderNeedDeposit = orderNeedDeposit;
        this.orderNeedPay = orderNeedPay;
        this.orderSubMoney = orderSubMoney;
    }

    public PayNeedMsg(String errorCode) {
        this.errorCode = errorCode;
    }
}

