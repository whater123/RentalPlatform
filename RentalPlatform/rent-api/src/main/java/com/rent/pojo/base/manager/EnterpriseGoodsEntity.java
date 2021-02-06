package com.rent.pojo.base.manager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rent.pojo.view.Property;
import com.rent.util.MoneyUtil;
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
public class EnterpriseGoodsEntity {
    @TableId(type = IdType.AUTO)
    int goodsEntityId;
    int goodsId;
    String goodsTime;
    int goodsRentState;
    String goodsRentTime;
    int goodsRentWay;
    int goodsRentExpert;
    String goodsPrice;
    String goodsDeposit;
    String goodsCurrentPrice;
    String goodRegularUnit;
    String goodsRegularPrice;
    double goodsNewLevel;
    @TableField(exist = false)
    private int addNumber;
    @TableField(exist = false)
    private String goodsRegularPricePerDay = "0.0";
    @TableField(exist = false)
    List<Property> properties;
}

