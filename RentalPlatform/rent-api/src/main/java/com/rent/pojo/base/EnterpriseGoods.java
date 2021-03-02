package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class EnterpriseGoods {
    @TableId(type = IdType.AUTO)
    private int goodsId;
    private int entpId;
    private String goodsPicture;
    private String goodsTitle;
    private String goodsIntroduce;
    private double goodsNewLevel;
    private int goodsCount;
    private int goodsExisting;
    private String goodsMaxPrice;
    private String goodsMinPrice;
    private int goodsSold;
    private String goodsMaxRent;
    private String goodsMinRent;
    private int goodsRent;
    private int goodsBigCategory;
    private int goodsCategoryId;
    @TableField(exist = false)
    private double point;
}
