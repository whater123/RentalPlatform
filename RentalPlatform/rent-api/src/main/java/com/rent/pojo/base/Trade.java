package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.Contact;
import com.rent.pojo.base.user.User;
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
    @TableId(value = "order_id")
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
    private int orderState;
    private int orderGoodsCount;
    private int orderRentWay;
    private int orderRentTime;
    private int entpId;
    private String orderRentUnit;
    private String orderTotalMoney;
    private String orderDeposit;
    @TableField(exist = false)
    private int goodsId;
    @TableField(exist = false)
    EnterpriseGoodsEntity enterpriseGoodsEntity;
    @TableField(exist = false)
    EnterpriseGoods enterpriseGoods;
    @TableField(exist = false)
    Contact contact;
    @TableField(exist = false)
    User user;
}
