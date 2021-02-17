package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author w
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPay {
    private String payId;
    private int goodsEntityId;
    private String orderId;
    private int userId;
    private int payType;
    private String payAmount;
    private String payTime;
    private double payScore;
    private String payPlatform;
    @TableField(exist = false)
    private String goodsTitle;
}
