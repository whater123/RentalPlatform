package com.rent.service;

import com.rent.dao.GoodsEntityMapper;
import com.rent.pojo.base.manager.EnterpriseCategory;
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
     * 获取商品集的详细信息
     * @param goodsId 商品id
     * @return 详细信息
     */
    EnterpriseGoods getGoodsInformation(int goodsId);

    /**
     * 获取商品个体信息
     * @param goodsEntityId 商品个体id
     * @return 商品个体信息
     */
    EnterpriseGoodsEntity getGoodsEntityInformation(int goodsEntityId);

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
    List<EnterpriseGoodsEntity> entitySortAndHandle(List<EnterpriseGoodsEntity> enterpriseGoodsEntityList, String sortWay) throws Exception;

    /**
     * 获取首页推荐商品
     * @return 推荐列表
     */
    List<EnterpriseGoods> getHotGoods();

    /**
     * 获取商品个体详细信息
     * @param goodsEntityId 商品个体id
     * @return 商品个体，没有则返回null
     */
    EnterpriseGoodsEntity getEntity(int goodsEntityId);

    /**
     * 模糊搜索 商品标题和描述
     * @param context 搜索内容
     * @return 商品集列表
     */
    List<EnterpriseGoods> search(String context);

    /**
     * 获取热门分类的商品集列表
     * @param categoryId 热门分类id
     * @return 商品集列表
     */
    List<EnterpriseGoods> getHotCategoryGoods(int categoryId);

    /**
     * 获取企业的内部分类
     * @param entpId 企业id
     * @return 企业分类id
     */
    List<EnterpriseCategory> getEnterpriseCategory(int entpId);

    /**
     * 获取企业某分类的商品集（如category为null则传0进来）
     * @param entpId 企业id
     * @param categoryId 企业分类id/0
     * @return 商品集
     */
    List<EnterpriseGoods> getEntpGoods(int entpId,int categoryId);
}
