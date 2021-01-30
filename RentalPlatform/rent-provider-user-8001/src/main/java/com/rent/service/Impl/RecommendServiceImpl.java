package com.rent.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.GoodsMapper;
import com.rent.enumeration.SortWayEnum;
import com.rent.pojo.base.Enterprise;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.view.SimpleGoods;
import com.rent.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author w
 */
@Service
public class RecommendServiceImpl implements RecommendService {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    GoodsMapper goodsMapper;


    @Override
    public List<EnterpriseGoods> getRedisBaseRecommendations(List<EnterpriseGoods> list) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Map<Integer,EnterpriseGoods> enterpriseGoodsMap = (Map<Integer,EnterpriseGoods>) opsForValue.get("goods");
        //如果没有推荐依据或推荐依据过少，直接返回原数据集
        if (enterpriseGoodsMap==null||enterpriseGoodsMap.size()<20){
            return list;
        }
        //将选择查询结果id存到set中
        Set<Integer> set = new TreeSet<>();
        list.forEach(enterpriseGoods -> {
            set.add(enterpriseGoods.getGoodsId());
        });
        //将选择查询结果的goodsPoint取出来
        List<EnterpriseGoods> goodsPoints = new ArrayList<>();
        for (Map.Entry<Integer,EnterpriseGoods> entry : enterpriseGoodsMap.entrySet()) {
            if (set.contains(entry.getKey())){
                goodsPoints.add(entry.getValue());
            }
        }
        //根据point进行排序操作,初始化返回集
        List<EnterpriseGoods> enterpriseGoods = new ArrayList<>();
        //point倒序
        goodsPoints.sort((e1,e2)-> (int)(e2.getPoint()*1000)-(int)(e1.getPoint()*1000));
        //取前一半
        goodsPoints = goodsPoints.subList(0,goodsPoints.size()/2);

        //一半的前25%随机抽取2个做推荐
        List<EnterpriseGoods> goodsList1 = goodsPoints.subList(0,goodsPoints.size()/4);
        Random random = new Random();
        //随机抽取[0,size/4-1]的元素
        int n = random.nextInt(goodsPoints.size()/4);
        //添加到返回列表
        enterpriseGoods.add(goodsList1.get(n));
        goodsList1.remove(n);
        Random random2 = new Random();
        //随机抽取[0,size/4-2]的元素
        int n2 = random2.nextInt(goodsPoints.size()/4-1);
        //添加到返回列表
        enterpriseGoods.add(goodsList1.get(n2));

        for (int i = 0; i < 3; i++) {
            //前25%-50%,50%-75%,75%-100%抽取1个
            List<EnterpriseGoods> goodsList2 = goodsPoints.subList(goodsPoints.size()*(i+1)/4,goodsPoints.size()*(i+2)/4);
            Random random3 = new Random();
            //随机抽取[0,size/4-2]的元素
            int n3 = random3.nextInt(goodsPoints.size()/4-1);
            //添加到返回列表
            enterpriseGoods.add(goodsList2.get(n3));
        }
        return enterpriseGoods;
    }

    @Override
    public List<EnterpriseGoods> getCategorySorted(String category, SortWayEnum sortWayEnum) {
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<>();
        List<EnterpriseGoods> enterpriseGoodsList = null;
        switch (sortWayEnum){
            case DEFAULT:
                queryWrapper.eq("goods_big_category",category);
                List<EnterpriseGoods> enterpriseGoodsList1 = goodsMapper.selectList(queryWrapper);
                enterpriseGoodsList =  getRedisBaseRecommendations(enterpriseGoodsList1);
                break;
            case PRICE_DESC:
                queryWrapper.eq("goods_big_category",category);
                List<EnterpriseGoods> enterpriseGoodsList2 = goodsMapper.selectList(queryWrapper);
                //字符串数字降序算法

                enterpriseGoodsList =  enterpriseGoodsList2;
                break;
            case PRICE_ASC:
                queryWrapper.eq("goods_big_category",category);
                List<EnterpriseGoods> enterpriseGoodsList3 = goodsMapper.selectList(queryWrapper);
                //字符串数字升序算法

                enterpriseGoodsList =  enterpriseGoodsList3;
                break;
            case SALESVOLUME_DESC:
                //根据销量降序查询
                break;
            case SALESVOLUME_ASC:
                //根据销量升序查询
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sortWayEnum);
        }
        return enterpriseGoodsList;
    }

    @Override
    public List<EnterpriseGoods> getEnterpriseSorted(int enterpriseId, SortWayEnum sortWayEnum) {
        return null;
    }

    @Override
    public List<EnterpriseGoods> getSearchSorted(String searchContext, SortWayEnum sortWayEnum) {
        return null;
    }

    @Override
    public List<EnterpriseGoods> getGoodsLastSoted(int goodsId) {
        return null;
    }

    @Override
    public EnterpriseGoods getCurrentPutGoods() {
        //补充添加一个新发布的商品
        QueryWrapper<EnterpriseGoods> enterpriseGoodsQueryWrapper = new QueryWrapper<>();
        //根据时间降序
        enterpriseGoodsQueryWrapper.orderByDesc("goods_time");

        List<EnterpriseGoods> enterpriseGoodsList3 = goodsMapper.selectList(enterpriseGoodsQueryWrapper);
        if (enterpriseGoodsList3==null){
            return null;
        }
        if (enterpriseGoodsList3.size()<=3){
            return enterpriseGoodsList3.get(0);
        }
        else {
            int baseNumber = enterpriseGoodsList3.size() / 2;
            //取前50%数据，[0,size/2-1]
            enterpriseGoodsList3.subList(0, baseNumber);
            Random random4 = new Random();
            //随机抽取[0,size/2-1]的元素
            int n4 = random4.nextInt(baseNumber);
            return enterpriseGoodsList3.get(n4);
        }
    }

    @Override
    public List<EnterpriseGoods> getTestSixGoods() {
        List<EnterpriseGoods> enterpriseGoodsList = goodsMapper.selectList(null);
        if (enterpriseGoodsList.size()<=6){
            return enterpriseGoodsList;
        }
        else {
            return enterpriseGoodsList.subList(0,6);
        }
    }

    @Override
    public EnterpriseGoods getGoodsImformation(int goodsId) {
        EnterpriseGoods enterpriseGoods = goodsMapper.selectById(goodsId);
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        if (opsForValue.get("goods")==null){
            return enterpriseGoods;
        }
        else {
            Map<Integer,EnterpriseGoods> goods = (Map<Integer, EnterpriseGoods>) opsForValue.get("goods");
            assert goods != null;
            EnterpriseGoods enterpriseGoods1 = goods.get(enterpriseGoods.getGoodsId());
            enterpriseGoods.setPoint(enterpriseGoods1.getPoint());
        }
        return enterpriseGoods;
    }

    @Override
    public List<EnterpriseGoods> getLastList(int goodsId) {
        EnterpriseGoods goodsImformation = getGoodsImformation(goodsId);
        if (goodsImformation==null){
            return null;
        }
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_big_category",goodsImformation.getGoodsBigCategory())
                    .eq("goods_category_id",goodsImformation.getGoodsCategoryId());
        return goodsMapper.selectList(queryWrapper);
    }

    private boolean addGoodsToRedis(EnterpriseGoods enterpriseGoods){
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Map<Integer,EnterpriseGoods> enterpriseGoodsMap = (Map<Integer,EnterpriseGoods>) opsForValue.get("goods");
        if (enterpriseGoodsMap==null||enterpriseGoods==null){
            return false;
        }
        enterpriseGoodsMap.put(enterpriseGoods.getGoodsId(),enterpriseGoods);
        opsForValue.set("goods",enterpriseGoodsMap);
        return true;
    }

    //private int goodsId;
    //    private int entpId;
    //    private String goodsPicture;
    //    private String goodsTitle;
    //    private String goodsIntroduce;
    //    private double goodsNewLevel;
    //    private int goodsSold;

    @Override
    public List<SimpleGoods> returnHandle(List<EnterpriseGoods> enterpriseGoods){
        List<SimpleGoods> list = new ArrayList<>();
        if (enterpriseGoods==null){
            return null;
        }
        for (EnterpriseGoods en :enterpriseGoods) {
            list.add(new SimpleGoods(en.getGoodsId(),
                    en.getEntpId(),
                    en.getGoodsPicture(),
                    en.getGoodsTitle(),
                    en.getGoodsIntroduce(),
                    en.getGoodsNewLevel(),
                    en.getGoodsSold()));
        }
        return list;
    }
}
