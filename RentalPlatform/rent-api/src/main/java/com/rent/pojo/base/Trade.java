package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Trade {
    private String orderId;
    private String orderParentId;
    private int userId;
    private int goodsEntityId;
    private int contactId;
    private int logisticsId;
    private String orderUrgentRelation;
    private String orderUrgentName;
    private String orderUrgentPhone;
    private String orderCreateTime;
    private int orderRentExpect;
    private int orderStopState;
    private int orderGoodsCount;
    private int orderRentWay;
    private int orderRentTime;
    private String orderRentUnit;
    private int enptId;
}
