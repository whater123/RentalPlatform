package com.rent.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.GoodsEntityMapper;
import com.rent.dao.GoodsMapper;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.EnterpriseGoodsEntity;
import com.rent.pojo.view.GoodsAttribute;
import com.rent.pojo.view.SimpleGoods;
import com.rent.service.GoodsService;
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
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Autowired
    GoodsEntityMapper goodsEntityMapper;

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

    @Override
    public List<GoodsAttribute> getGoodsAttributes(int goodsId) {
        Map<String, List<String>> goodsAttribute = getGoodsAttribute(goodsId);
        if (goodsAttribute==null){
            return null;
        }
        List<GoodsAttribute> goodsAttributeList = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry : goodsAttribute.entrySet()){
            goodsAttributeList.add(new GoodsAttribute(entry.getKey(),entry.getValue()));
        }
        List<String> list = new ArrayList<>();
        list.add("先租后买");
        list.add("只租");
        goodsAttributeList.add(new GoodsAttribute("租赁预期",list));
        return goodsAttributeList;
    }

    @Override
    public List<EnterpriseGoodsEntity> getGoodsEntities(int goodsId, Map<String, String> map) {
        List<Integer> goodsEntity = getGoodsEntity(goodsId, map);
        System.out.println(goodsEntity);
        if (goodsEntity==null){
            return null;
        }
        String rentWay = map.get("租赁预期");
        if (rentWay == null){
            return null;
        }
        int rentKey;
        if ("先租后买".equals(rentWay)){
            rentKey=1;
        } else {
            rentKey=2;
        }
        Set<Integer> set1 = new HashSet<>(goodsEntity);
        Set<Integer> set2 = new HashSet<>(goodsEntityMapper.selectIdListByRentKey(rentKey));
        Set<Integer> result = new HashSet<>(set1);
        //求交集
        result.retainAll(set2);
        return goodsEntityMapper.selectBatchIds(result);
    }

    /**
     * 根据商品集id获取用户能选择的所有属性
     * @param goodsId 商品集id
     * @return Map<商品个体属性，List<商品个体属性选项>>，如果不存在则返回null
     */
    private Map<String, List<String>> getGoodsAttribute(int goodsId) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_cl",String.valueOf(goodsId))){
            return null;
        }else {
            return (Map<String, List<String>>) opsForHash.get("go_cl",String.valueOf(goodsId));
        }
    }

    /**
     * 根据商品集id和 所属属性类别Map<商品个体属性，商品个体属性选项> 获取到所有的商品个体id
     * @param goodsId 商品集id
     * @param map Map<商品个体属性，商品个体属性选项>
     * @return 对应商品集对应属性map的所有商品个体id，如果不存在则返回null
     */
    private List<Integer> getGoodsEntity(int goodsId, Map<String, String> map) {
        //筛选map中添加商品集id
        map.put("goodsId", String.valueOf(goodsId));
        System.out.println(map);
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_en_cl",String.valueOf(map))){
            return null;
        }else {
            System.out.println(map);
            return (List<Integer>) opsForHash.get("go_en_cl",String.valueOf(map));
        }
    }
}
