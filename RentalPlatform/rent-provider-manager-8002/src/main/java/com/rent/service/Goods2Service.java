package com.rent.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseGoodsEntityMapper;
import com.rent.dao.EnterpriseGoodsMapper;
import com.rent.pojo.base.Enterprise;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.EnterpriseGoodsEntity;
import com.rent.pojo.base.Property;
import com.rent.service.impl.GoodsImpl;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Goods2Service implements GoodsImpl {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EnterpriseGoodsMapper enterpriseGoodsMapper;
    @Autowired
    EnterpriseGoodsEntityMapper enterpriseGoodsEntityMapper;

    public List<EnterpriseGoods> getThoseGoods(String column, String value){
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
        queryWrapper.eq(column,value);
        return enterpriseGoodsMapper.selectList(queryWrapper);
    }

    public List<EnterpriseGoodsEntity> getThoseGoodsEntity(String column, String value){
        QueryWrapper<EnterpriseGoodsEntity> queryWrapper = new QueryWrapper<EnterpriseGoodsEntity>();
        queryWrapper.eq(column,value);
        return enterpriseGoodsEntityMapper.selectList(queryWrapper);
    }

    @Override
    public boolean addGoodsAttribute(int goodsId, Map<String, List<String>> map) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (opsForHash.hasKey("go_cl",String.valueOf(goodsId))){
            return false;
        }else {
            opsForHash.put("go_cl",String.valueOf(goodsId),map);
            return true;
        }
    }

    @Override
    public boolean addGoodsEntityAttribute(int goodsId, Map<String, String> map, List<Integer> list) {
        //筛选map中添加商品集id
        try{
            map.put("goodsId", String.valueOf(goodsId));
            HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
            if (opsForHash.hasKey("go_en_cl",String.valueOf(map))){
                list.addAll(getGoodsEntity(goodsId,map));
                opsForHash.put("go_en_cl",String.valueOf(map),list);
            } else {
                opsForHash.put("go_en_cl",String.valueOf(map),list);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public TreeMap<String, List<String>> getGoodsAttribute(int goodsId) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_cl",String.valueOf(goodsId))){
            return null;
        }else {
            return (TreeMap<String, List<String>>) opsForHash.get("go_cl",String.valueOf(goodsId));
        }
    }

    @Override
    public List<Integer> getGoodsEntity(int goodsId, Map<String, String> map) {
        //筛选map中添加商品集id
        map.put("goodsId", String.valueOf(goodsId));

        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_en_cl",String.valueOf(map))){
            return null;
        }else {
            return (List<Integer>) opsForHash.get("go_en_cl",String.valueOf(map));
        }
    }

    public boolean insertGoods(EnterpriseGoods goods){
        try{
            goods.setEntpId(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal())).get(0).getEntpId());
            goods.setGoodsMaxPrice("0.00");
            goods.setGoodsMinPrice("0.00");
            goods.setGoodsMaxRent("0.00");
            goods.setGoodsMinRent("0.00");
            enterpriseGoodsMapper.insert(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertGoodsProperty(String json){
        List<Property> properties = JSONArray.parseArray(
                JSONObject.parseObject(json).getString("properties"),Property.class);
        Map<String, List<String>> map = getGoodsAttribute(
                JSONObject.parseObject(json).getInteger("goodsId"));
        if(map == null){
            map = new TreeMap<String, List<String>>();
            for (Property property :
                    properties) {
                map.put(property.getPropertyName(), property.getPropertyList());
            }
            addGoodsAttribute(JSONObject.parseObject(json).getInteger("goodsId"),map);
            return true;
        }else {
            return false;
        }
    }

    public boolean isRuleProperties(TreeMap<String,List<String>> goodsAttribute, List<Property> properties){
        if(goodsAttribute.size() != properties.size()){
            return false;
        }
        int i = 0;
        for (String key:
                goodsAttribute.keySet()) {
            for (Property property :
                    properties) {
                if(key.equals(property.getPropertyName())){
                    if(goodsAttribute.get(key).contains(property.getPropertyValue())){
                        i++;
                    }else{
                        return false;
                    }
                }
            }
        }
        return i == properties.size();
    }

    public boolean isNowEntpId(int entpId){
        return entpId == enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId();
    }



    public boolean insertGoodsEntity(EnterpriseGoodsEntity goodsEntity) throws Exception {

        goodsEntity.setGoodsTime(MyUtil.getNowTime());
        goodsEntity.setGoodsRentTime(MyUtil.getNowTime());

        //更新商品集
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
        queryWrapper.eq("goods_id",goodsEntity.getGoodsId());
        EnterpriseGoods goods = enterpriseGoodsMapper.selectList(queryWrapper).get(0);

        if(MoneyUtil.compare(goods.getGoodsMaxPrice(), goodsEntity.getGoodsPrice()) < 0){
            goods.setGoodsMaxPrice(goodsEntity.getGoodsPrice());
        }
        if(MoneyUtil.compare(goods.getGoodsMinPrice(), goodsEntity.getGoodsPrice()) > 0){
            goods.setGoodsMinPrice(goodsEntity.getGoodsPrice());
        }
        if("0.00".equals(goods.getGoodsMinPrice())){
            goods.setGoodsMinPrice(goodsEntity.getGoodsPrice());
        }

        String regularPrice = MoneyUtil.fractionDivide(goodsEntity.getGoodsRegularPrice(),
                (goodsEntity.getGoodRegularUnit())+".00");


        if(MoneyUtil.compare(goodsEntity.getGoodsCurrentPrice(),
                regularPrice) < 0){
            if(MoneyUtil.compare(goods.getGoodsMaxRent(),regularPrice) < 0){
                goods.setGoodsMaxRent(regularPrice);
            }
            if(MoneyUtil.compare(goods.getGoodsMinRent(),goodsEntity.getGoodsCurrentPrice()) > 0){
                goods.setGoodsMinRent(goodsEntity.getGoodsCurrentPrice());
            }
        }else {
            if(MoneyUtil.compare(goods.getGoodsMinRent(),regularPrice) > 0){
                goods.setGoodsMinRent(regularPrice);
            }
            if(MoneyUtil.compare(goods.getGoodsMaxRent(),goodsEntity.getGoodsCurrentPrice()) < 0){
                goods.setGoodsMaxRent(goodsEntity.getGoodsCurrentPrice());
            }
        }
        if("0.00".equals(goods.getGoodsMinRent())){
            goods.setGoodsMinRent(goods.getGoodsMaxRent());
        }
        goods.setGoodsCount(goods.getGoodsCount() + goodsEntity.getAddNumber());
        goods.setGoodsExisting(goods.getGoodsExisting() + goodsEntity.getAddNumber());

        enterpriseGoodsMapper.update(goods,queryWrapper);

        //更新属性
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < goodsEntity.getAddNumber(); i++) {
            enterpriseGoodsEntityMapper.insert(goodsEntity);
            list.add(goodsEntity.getGoodsEntityId());
        }
        TreeMap<String,String> map = new TreeMap<String, String>();
        for (Property property :
                goodsEntity.getProperties()) {
            map.put(property.getPropertyName(),property.getPropertyValue());
        }
        addGoodsEntityAttribute(goodsEntity.getGoodsId(),map,list);

        return true;
    }


}
