package com.rent.service.Impl;

import com.rent.dao.EnterpriseMapper;
import com.rent.dao.GoodsEntityMapper;
import com.rent.dao.GoodsMapper;
import com.rent.pojo.base.manager.Enterprise;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.view.GoodsAttribute;
import com.rent.pojo.view.SimpleGoods;
import com.rent.service.GoodsService;
import com.rent.util.MoneyUtil;
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
    @Autowired
    EnterpriseMapper enterpriseMapper;

    @Override
    public EnterpriseGoods getGoodsInformation(int goodsId) {
        EnterpriseGoods enterpriseGoods = goodsMapper.selectById(goodsId);
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();

        Enterprise enterprise = enterpriseMapper.selectById(enterpriseGoods.getEntpId());
        enterpriseGoods.setEntpShopName(enterprise.getEntpShopName());

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
    public EnterpriseGoodsEntity getGoodsEntityInformation(int goodsEntityId) {
        return goodsEntityMapper.selectById(goodsEntityId);
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
        //移除租赁预期，因为这个不存在redis里，是用来查询mysql的数据的
        map.remove("租赁预期");
        List<Integer> goodsEntity = getGoodsEntity(goodsId, map);
        System.out.println(goodsEntity);
        if (goodsEntity==null){
            return null;
        }
        Set<Integer> set1 = new HashSet<>(goodsEntity);
        Set<Integer> set2 = new HashSet<>(goodsEntityMapper.selectIdListByRentKey(rentKey));
        Set<Integer> result = new HashSet<>(set1);
        //求交集
        result.retainAll(set2);
        if (result.isEmpty()){
            return null;
        } else {
            return goodsEntityMapper.selectBatchIds(result);
        }
    }

    @Override
    public List<EnterpriseGoodsEntity> entitySortAndHandle(List<EnterpriseGoodsEntity> enterpriseGoodsEntityList, String sortWay) throws Exception {
        for (int i = 0; i < enterpriseGoodsEntityList.size(); i++) {
            String[] split = enterpriseGoodsEntityList.get(i).getGoodsRegularUnit().split("/");
            String[] split1 = enterpriseGoodsEntityList.get(i).getGoodsRegularPrice().split("/");
            String sum = "0.0";
            if (split.length!=split1.length){
                continue;
            } else {
                for (int j = 0; j < split.length; j++) {
                    if (!MoneyUtil.isRuleString(split1[j])){
                        split1[j] = MoneyUtil.addTail(split1[j]);
                    }
                    if (!MoneyUtil.isRuleString(split[j])){
                        split[j] = MoneyUtil.addTail(split[j]);
                    }
                    sum = MoneyUtil.fractionAdd(sum,MoneyUtil.fractionDivide(split1[j],split[j]));
                }
            }
            EnterpriseGoodsEntity enterpriseGoodsEntity = enterpriseGoodsEntityList.get(i);
            enterpriseGoodsEntity.setGoodsRegularPricePerDay(MoneyUtil.fractionDivide(sum,split.length+".0"));
            enterpriseGoodsEntityList.set(i,enterpriseGoodsEntity);
        }

        if ("0".equals(sortWay)){
            return enterpriseGoodsEntityList;
        }
        enterpriseGoodsEntityList.sort((e1,e2)-> {
            if ("1".equals(sortWay)){
                return (int) (e2.getGoodsNewLevel()-e1.getGoodsNewLevel());
            }
            else {
                return (int) (e1.getGoodsNewLevel()-e2.getGoodsNewLevel());
            }
        });
        return enterpriseGoodsEntityList;
    }

    @Override
    public List<EnterpriseGoods> getHotGoods() {
        List<EnterpriseGoods> enterpriseGoods = goodsMapper.selectList(null);
        enterpriseGoods.sort((e1,e2)-> (e2.getGoodsRent()+e2.getGoodsSold())-(e1.getGoodsRent()+e1.getGoodsSold()));
        return enterpriseGoods.subList(0,4);
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
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_en_cl",String.valueOf(map))){
            return null;
        }else {
            return (List<Integer>) opsForHash.get("go_en_cl",String.valueOf(map));
        }
    }

    /**
     * 删除商品集所有属性
     * @param goodsId 商品集id
     * @return 如果不存在或删除失败则返回false，成功返回true
     */
    private boolean deleteGoodsAttributes(int goodsId){
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (!opsForHash.hasKey("go_cl",String.valueOf(goodsId))){
            return false;
        }else {
            Long goCl = opsForHash.delete("goCl", String.valueOf(goodsId));
            return goCl==1;
        }
    }
}
