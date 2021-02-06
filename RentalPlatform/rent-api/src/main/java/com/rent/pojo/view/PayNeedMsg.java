package com.rent.pojo.view;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @TableField(exist = false)
    private String errorCode;

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

