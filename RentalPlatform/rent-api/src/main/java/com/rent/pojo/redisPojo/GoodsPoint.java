package com.rent.pojo.redisPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsPoint {
    private int goodsId;
    /**
     * 推荐指数
     */
    private double point;
}
