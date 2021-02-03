package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnterpriseGoods {
    @TableId(type = IdType.AUTO)
    int goodsId;
    int entpId;
    String goodsPicture;
    String goodsTitle;
    String goodsIntroduce;
    int goodsCategoryId;
    int goodsBigCategory;
    double goodsNewLevel;
    int goodsCount;
    int goodsExisting;
    String goodsMaxPrice;
    String goodsMinPrice;
    int goodsSold;
    String goodsMaxRent;
    String goodsMinRent;
    int goodsRent;
}
