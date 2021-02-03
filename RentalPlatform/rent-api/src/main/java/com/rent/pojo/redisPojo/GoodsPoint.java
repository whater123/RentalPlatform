package com.rent.pojo.redisPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

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
