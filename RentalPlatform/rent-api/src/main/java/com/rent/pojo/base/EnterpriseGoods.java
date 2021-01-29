package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class EnterpriseGoods {
    @TableId(type = IdType.AUTO)
    private int goodsId;
    private int entpId;
    private Date goodsTime;
    private String goodsBigCategory;
    private int goodsCategoryId;
    /**
     * 商品的租赁方式，0为只活期，1为只定期，2为都可以
     */
    private int goodsRentWay;
    /**
     * 租赁预期，0为先租后买，1为只租，2为都可以
     */
    private int goodsRentExpect;
    private String goodsPictureId;
    private String goodsValue;
    private String goodsDeposit;
    /**
     * 活期商品每日价格，只能为定期则为0
     */
    private String goodsCurrentValue;
    /**
     * 定期商品时间单位，0为天，1为月，2为年（如果只能为活期则为-1）
     */
    private int goodsRegularUnit;
    /**
     * 定期商品价格（/时间单位）,如果只能为活期则为0
     */
    private String goodsRegularValue;
    private String goodsIntroduce;
    /**
     * 商品的新鲜程度，满分100
     */
    private int goodsNewLevel;
    @TableField(exist = false)
    private double point;
}
