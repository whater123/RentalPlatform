package com.rent.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.*;
import com.rent.pojo.base.Trade;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.Contact;
import com.rent.service.OrderService;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author w
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    GoodsEntityMapper goodsEntityMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    ContactMapper contactMapper;

    /**
     * 获取订单id
     * @param userId 用户id
     * @return 半随机订单id
     */
    private String getRandomOrderId(int userId){
        StringBuilder stringBuilder = new StringBuilder();
        Integer integer = tradeMapper.selectCount(null);
        stringBuilder.append("RPO");
        stringBuilder.append(integer+1);
        stringBuilder.append(UUID.randomUUID().toString().toUpperCase(), 0, 5);
        stringBuilder.append(userId);
        return String.valueOf(stringBuilder);
    }

    @Override
    public Map<String,String> insertOriginOrder(Trade trade) {
        Map<String,String> map = new HashMap<>();
        EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(trade.getGoodsEntityId());
        if (enterpriseGoodsEntity.getGoodsRentState()!=0&&enterpriseGoodsEntity.getGoodsRentState()!=1){
            map.put("code","1");
            map.put("msg","商品为不可租状态");
            return map;
        }
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_receive_id","U"+trade.getUserId())
                    .eq("contact_id",trade.getContactId());
        Contact contacts = contactMapper.selectOne(queryWrapper);
        if (contacts==null){
            map.put("code","1");
            map.put("msg","联系方式不存在");
            return map;
        }
        int updateRentState = goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), trade.getOrderRentWay() + 1);
        if (updateRentState!=1){
            map.put("code","1");
            map.put("msg","修改商品租赁状态失败");
            return map;
        }
        //销量增加
        EnterpriseGoods enterpriseGoods = goodsMapper.selectById(enterpriseGoodsEntity.getGoodsId());
        if (enterpriseGoods==null){
            goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), 1);
            map.put("code","1");
            map.put("msg","该商品的商品集不存在");
            return map;
        }
        if (enterpriseGoods.getGoodsExisting()<trade.getOrderGoodsCount()){
            map.put("code","1");
            map.put("msg","商品库存不足");
            return map;
        }
        enterpriseGoods.setGoodsRent(enterpriseGoods.getGoodsRent()+ trade.getOrderGoodsCount());
        enterpriseGoods.setGoodsExisting(enterpriseGoods.getGoodsExisting()-trade.getOrderGoodsCount());
        int i = goodsMapper.updateById(enterpriseGoods);
        if (i!=1){
            goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), 1);
            map.put("code","1");
            map.put("msg","修改商品集销量与库存失败");
            return map;
        }
        trade.setLogisticsId(0);
        trade.setOrderParentId("0");
        trade.setOrderCreateTime(MyUtil.getNowTime());
        trade.setOrderStopState(0);
        trade.setOrderId(getRandomOrderId(trade.getUserId()));
        trade.setEntpId(enterpriseGoods.getEntpId());
        trade.setOrderState(1);
        //这里的逻辑需要改一下，处理错误的回滚
        int insert = tradeMapper.insert(trade);
        if (insert!=1){
            goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), 1);
            enterpriseGoods.setGoodsRent(enterpriseGoods.getGoodsRent()- trade.getOrderGoodsCount());
            goodsMapper.updateById(enterpriseGoods);
            map.put("code","1");
            map.put("msg","订单提交失败");
            return map;
        }
        map.put("code","0");
        return map;
    }
}
