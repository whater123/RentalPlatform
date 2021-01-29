package com.rent.service;

import com.rent.enumeration.SortWayEnum;
import com.rent.pojo.base.EnterpriseGoods;

import java.util.List;

/**
 * @author w
 */
public interface GoodsService {

    /**
     * 获取redis存储的推荐依据数据，并point倒序，取前一半，
     * 一半的前25%随机抽取2个做推荐，
     * 前25%-50%抽取1个，
     * 前50%-75%随机抽取1个，
     * 前75%-100%随机抽取一个
     * @param list 基础的数据集
     * @return 根据redis数据推荐的排序结果
     */
    List<EnterpriseGoods> getRedisBaseRecommendations(List<EnterpriseGoods> list);

    /**
     * 从数据库选择查询热门分类的商品，并根据推荐，销量，价格排序（如果是查询主页，则category为null）
     * @param sortWayEnum 排序的方式
     * @param category 热门商品种类 null为主页推荐，绑定sortWayEnum为DEFAULT
     * @return 对应热门分类的商品
     */
    List<EnterpriseGoods> getCategorySorted(String category, SortWayEnum sortWayEnum);

    /**
     * 从数据库选择查询目标企业的商品，并根据推荐，销量，价格排序
     * @param enterpriseId 企业id
     * @param sortWayEnum 排序方式
     * @return 对应企业显示的商品
     */
    List<EnterpriseGoods> getEnterpriseSorted(int enterpriseId,SortWayEnum sortWayEnum);

    /**
     * 从数据库模糊查询搜索的商品，并根据推荐，销量，价格排序
     * @param searchContext 搜索内容
     * @param sortWayEnum 排序方式
     * @return 对应搜索显示的商品
     */
    List<EnterpriseGoods> getSearchSorted(String searchContext,SortWayEnum sortWayEnum);

    /**
     * 根据商品id，得到所处商铺与商铺分类，根据销量排序
     * @param goodsId 浏览的商品id
     * @return 商品详情页面最下方的推荐列表
     */
    List<EnterpriseGoods> getGoodsLastSoted(int goodsId);

    /**
     * 随机获取最近一个发布的商品
     * @return 商品
     */
    EnterpriseGoods getCurrentPutGoods();
}
