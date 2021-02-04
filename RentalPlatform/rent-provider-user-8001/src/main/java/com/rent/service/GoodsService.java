package com.rent.service;

import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.view.GoodsAttribute;
import com.rent.pojo.view.SimpleGoods;

import java.util.List;
import java.util.Map;

/**
 * @author w
 */
public interface GoodsService {
    /**
     * 获取商品的详细信息
     * @param goodsId 商品id
     * @return 详细信息
     */
    EnterpriseGoods getGoodsImformation(int goodsId);

    /**
     * 简化数据
     * @param enterpriseGoods 原数据
     * @return 转化为简单形式的商品数据
     */
    List<SimpleGoods> returnHandle(List<EnterpriseGoods> enterpriseGoods);

    /**
     * 获取商品的所有属性选项
     * @param goodsId 商品集id
     * @return 商品属性信息，如果不存在则返回null
     */
    List<GoodsAttribute> getGoodsAttributes(int goodsId);

    /**
     * 获取默认排序的商品个体列表
     * @param goodsId 商品集id
     * @param map 商品属性筛选map
     * @return 符合要求的商品个体集合
     */
    List<EnterpriseGoodsEntity> getGoodsEntities(int goodsId,Map<String,String> map);

    /**
     * 根据排序方法返回列表
     * @param enterpriseGoodsEntityList 商品个体列表
     * @param sortWay 排序方法
     * @return 返回结果
     */
    List<EnterpriseGoodsEntity> entitySort(List<EnterpriseGoodsEntity> enterpriseGoodsEntityList,String sortWay);
}
