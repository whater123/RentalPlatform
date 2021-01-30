package com.rent.pojo.view;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class SimpleGoods {
    @TableId(type = IdType.AUTO)
    private int goodsId;
    private int entpId;
    private String goodsPicture;
    private String goodsTitle;
    private String goodsIntroduce;
    private double goodsNewLevel;
    private int goodsSold;
}
