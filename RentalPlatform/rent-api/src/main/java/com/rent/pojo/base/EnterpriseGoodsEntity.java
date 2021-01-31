package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    int goodRegularUnit;
    String goodsRegularPrice;
    double goodsNewLevel;
    @TableField(exist = false)
    private String addNumber;
    @TableField(exist = false)
    private String goodsRegularPricePerUnit;
}

