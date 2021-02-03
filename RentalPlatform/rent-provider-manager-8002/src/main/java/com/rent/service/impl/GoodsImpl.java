package com.rent.service.impl;

import java.util.List;
import java.util.Map;

public interface GoodsImpl {
    /**
     * 添加商品集用户能选择的所有属性
     * @param goodsId 商品集id
     * @param map Map<商品个体属性，List<商品个体属性选项>>
     * @return 是否成功，如果该商品集已有属性map，则返回false，否则返回true
     */
    boolean addGoodsAttribute(int goodsId, Map<String,List<String>> map);

    /**
     * 批量添加某类商品集属性的商品个体
     * @param goodsId 商品集id
     * @param map Map<商品个体属性，商品个体属性选项>
     * @param list 本次添加的 对应上个参数商品个体属性的 所有商品个体id
     * @return 是否成功,如果对应商品集和属性map已有 商品个体列表，返回false，否则返回true
     */
    boolean addGoodsEntityAttribute(int goodsId,Map<String,String> map,List<Integer> list);

    /**
     * 根据商品集id获取用户能选择的所有属性
     * @param goodsId 商品集id
     * @return Map<商品个体属性，List<商品个体属性选项>>，如果不存在则返回null
     */
    Map<String,List<String>> getGoodsAttribute(int goodsId);

    /**
     * 根据商品集id和 所属属性类别Map<商品个体属性，商品个体属性选项> 获取到所有的商品个体id
     * @param goodsId 商品集id
     * @param map Map<商品个体属性，商品个体属性选项>
     * @return 对应商品集对应属性map的所有商品个体id，如果不存在则返回null
     */
    List<Integer> getGoodsEntity(int goodsId,Map<String,String> map);
}
