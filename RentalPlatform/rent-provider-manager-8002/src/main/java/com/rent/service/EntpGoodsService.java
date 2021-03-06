package com.rent.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseGoodsEntityMapper;
import com.rent.dao.EnterpriseGoodsMapper;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.EnterpriseGoodsEntity;
import com.rent.pojo.view.Property;
import com.rent.service.impl.GoodsImpl;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class EntpGoodsService implements GoodsImpl {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EnterpriseGoodsMapper enterpriseGoodsMapper;
    @Autowired
    EnterpriseGoodsEntityMapper enterpriseGoodsEntityMapper;
    @Autowired
    UtilsService utilsService;

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

    public boolean deleteGoodsAttributes(int goodsId){
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_cl",String.valueOf(goodsId))){
            return false;
        }else {
            Long goCl = opsForHash.delete("goCl", String.valueOf(goodsId));
            return goCl==1;
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
            goods.setGoodsCreateTime(MyUtil.getNowDateTime());
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

    public boolean isMatchedProperties(TreeMap<String,List<String>> goodsAttribute, List<Property> properties){
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


    public boolean insertGoodsEntity(EnterpriseGoodsEntity goodsEntity) throws Exception {
        try{
            goodsEntity.setGoodsTime(MyUtil.getNowDate());
            goodsEntity.setGoodsRentTime(MyUtil.getNowDate());

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

            ArrayList<String> prices = toSortedArray(goodsEntity.getGoodsRegularPrice());
            ArrayList<String> units = toSortedArray(goodsEntity.getGoodsRegularUnit());
            if (prices.size() != units.size()){
                throw new Exception("402，传参出错，prices和units传参不同");
            }
            ArrayList<String> pricesPerDay = new ArrayList<String>();
            for (int i = 0; i < prices.size(); i++) {
                System.out.println(prices.get(i));
                System.out.println(units.get(i));
                pricesPerDay.add(MoneyUtil.fractionDivide(
                        MoneyUtil.addTail(prices.get(i)),
                        MoneyUtil.addTail(units.get(i))));
            }
            pricesPerDay.add(goods.getGoodsMaxRent());
            pricesPerDay.add(goods.getGoodsMinRent());
            pricesPerDay.add(goodsEntity.getGoodsCurrentPrice());
            pricesPerDay = toSortedArray(arrayToString(pricesPerDay));
            System.out.println(pricesPerDay);
            goods.setGoodsMinRent(pricesPerDay.get(0));
            for (int i = 0; MoneyUtil.compare("0.00",goods.getGoodsMinRent()) == 0; i++) {
                goods.setGoodsMinRent(pricesPerDay.get(i));
            }
            goods.setGoodsMaxRent(pricesPerDay.get(pricesPerDay.size() - 1));

//        if(MoneyUtil.compare(goodsEntity.getGoodsCurrentPrice(),
//                regularPrice) < 0){
//            if(MoneyUtil.compare(goods.getGoodsMaxRent(),regularPrice) < 0){
//                goods.setGoodsMaxRent(regularPrice);
//            }
//            if(MoneyUtil.compare(goods.getGoodsMinRent(),goodsEntity.getGoodsCurrentPrice()) > 0){
//                goods.setGoodsMinRent(goodsEntity.getGoodsCurrentPrice());
//            }
//        }else {
//            if(MoneyUtil.compare(goods.getGoodsMinRent(),regularPrice) > 0){
//                goods.setGoodsMinRent(regularPrice);
//            }
//            if(MoneyUtil.compare(goods.getGoodsMaxRent(),goodsEntity.getGoodsCurrentPrice()) < 0){
//                goods.setGoodsMaxRent(goodsEntity.getGoodsCurrentPrice());
//            }
//        }
//        if("0.00".equals(goods.getGoodsMinRent())){
//            goods.setGoodsMinRent(goods.getGoodsMaxRent());
//        }
            goods.setGoodsCount(goods.getGoodsCount() + goodsEntity.getAddNumber());
            goods.setGoodsExisting(goods.getGoodsExisting() + goodsEntity.getAddNumber());

            enterpriseGoodsMapper.update(goods,queryWrapper);

            //更新属性
            ArrayList<Integer> list = new ArrayList<Integer>();
            TreeMap<String,String> map = new TreeMap<String, String>();
            for (Property property :
                    goodsEntity.getProperties()) {
                map.put(property.getPropertyName(),property.getPropertyValue());
            }
            goodsEntity.setGoodsProperties(map.toString());
            for (int i = 0; i < goodsEntity.getAddNumber(); i++) {
                String[] strings = goodsEntity.getGoodsRegularPrice().split("/");
                StringBuilder goodsRegularPrice = new StringBuilder();
                for (String string :
                        strings) {
                    goodsRegularPrice.append(MoneyUtil.addTail(string)).append("/");
                    System.out.println(string);
                }
                goodsEntity.setGoodsRegularPrice(goodsRegularPrice.substring(0,goodsRegularPrice.length() - 1));
                enterpriseGoodsEntityMapper.insert(goodsEntity);
                list.add(goodsEntity.getGoodsEntityId());
            }
            addGoodsEntityAttribute(goodsEntity.getGoodsId(),map,list);
            updateGoodsFresh(goodsEntity.getGoodsId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGoods(int goodsId){
        try{
            QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
            queryWrapper.eq("goods_id",goodsId);
            enterpriseGoodsMapper.delete(queryWrapper);
            QueryWrapper<EnterpriseGoodsEntity> queryWrapper1 = new QueryWrapper<EnterpriseGoodsEntity>();
            queryWrapper1.eq("goods_id",goodsId);
            enterpriseGoodsEntityMapper.delete(queryWrapper1);
            deleteGoodsAttributes(goodsId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateGoods(EnterpriseGoods newGoods){
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
        queryWrapper.eq("goods_id",newGoods.getGoodsId());
        EnterpriseGoods oldGoods = enterpriseGoodsMapper.selectList(queryWrapper).get(0);
        if (!MyUtil.strHasVoid(newGoods.getGoodsTitle())){
            oldGoods.setGoodsTitle(newGoods.getGoodsTitle());
        }
        if (!MyUtil.strHasVoid(newGoods.getGoodsPicture())){
            oldGoods.setGoodsPicture(newGoods.getGoodsPicture());
        }
        if (!MyUtil.strHasVoid(newGoods.getGoodsIntroduce())){
            oldGoods.setGoodsIntroduce(newGoods.getGoodsIntroduce());
        }
        if (!MyUtil.intHasVoid(newGoods.getGoodsBigCategory())){
            oldGoods.setGoodsBigCategory(newGoods.getGoodsBigCategory());
        }
        if (!MyUtil.intHasVoid(newGoods.getGoodsCategoryId())){
            oldGoods.setGoodsCategoryId(newGoods.getGoodsCategoryId());
        }
        return enterpriseGoodsMapper.update(oldGoods, queryWrapper) != 0;

    }

    public void updateGoodsFresh(int goodsId) throws Exception {
        QueryWrapper<EnterpriseGoods> queryWrapper = new QueryWrapper<EnterpriseGoods>();
        queryWrapper.eq("goods_id",String.valueOf(goodsId));
        List<EnterpriseGoods> goodsList = enterpriseGoodsMapper.selectList(queryWrapper);
        EnterpriseGoods goods;
        if(goodsList.size() != 0){
            goods = goodsList.get(0);
        } else {
            throw new Exception("该goodsId不存在");
        }
        List<EnterpriseGoodsEntity> entityList = getThoseGoodsEntity("goods_id", String.valueOf(goodsId));
        double sum = 0;
        for (EnterpriseGoodsEntity e :
                entityList) {
            sum += e.getGoodsNewLevel();
        }
        sum /= entityList.size();
        goods.setGoodsNewLevel(sum);
        enterpriseGoodsMapper.update(goods,queryWrapper);
    }

    private ArrayList<String> toSortedArray(String s) throws Exception {
        System.out.println(s);
        String[] strings = s.split("/");
        ArrayList<BigDecimal> doubleArrayList = new ArrayList<BigDecimal>();
        for (String string :
                strings) {
            try{
                doubleArrayList.add(new BigDecimal(string));
            } catch (NumberFormatException e) {
                throw new Exception("必须输入数字");
            }
        }
        doubleArrayList.sort(null);
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (BigDecimal d :
                doubleArrayList) {
            stringArrayList.add(String.valueOf(d));
        }
        return stringArrayList;
    }

    private String arrayToString(ArrayList<String> list){
        StringBuilder str = new StringBuilder();
        for (String s :
                list) {
            str.append(s).append("/");
        }
        str = new StringBuilder(str.substring(0, str.length() - 1));
        return str.toString();
    }
}
