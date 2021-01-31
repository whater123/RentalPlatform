package com.rent.pojo.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsAttribute {
    private String goodsId;
    private String goodsAttributes;
    private String goodsAttributeCategory;
    private List<String> goodsAttributeList;

    public GoodsAttribute(String goodsAttributeCategory, List<String> goodsAttributeList) {
        this.goodsAttributeCategory = goodsAttributeCategory;
        this.goodsAttributeList = goodsAttributeList;
    }

    public GoodsAttribute(String goodsId, String goodsAttributes) {
        this.goodsId = goodsId;
        this.goodsAttributes = goodsAttributes;
    }
}
