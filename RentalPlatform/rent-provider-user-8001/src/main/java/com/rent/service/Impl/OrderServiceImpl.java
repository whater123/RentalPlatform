package com.rent.service.Impl;

import com.rent.dao.GoodsEntityMapper;
import com.rent.dao.OrderMapper;
import com.rent.pojo.base.Order;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.service.OrderService;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author w
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    GoodsEntityMapper goodsEntityMapper;

    /**
     * 获取订单id
     * @param userId 用户id
     * @return 半随机订单id
     */
    private String getRandomOrderId(int userId){
        StringBuilder stringBuilder = new StringBuilder();
        Integer integer = orderMapper.mySelectCount();
        stringBuilder.append("RP");
        stringBuilder.append(integer+1);
        stringBuilder.append(UUID.randomUUID().toString().toUpperCase(), 0, 5);
        stringBuilder.append(userId);
        return String.valueOf(stringBuilder);
    }

    @Override
    public boolean insertOriginOrder(Order order) {
        EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(order.getGoodsEntityId());
        if (enterpriseGoodsEntity.getGoodsRentState()!=0&&enterpriseGoodsEntity.getGoodsRentState()!=1){
            return false;
        }
        order.setLogisticsId(0);
        order.setOrderParentId("0");
        order.setOrderCreateTime(MyUtil.getNowTime());
        order.setOrderStopState(0);
        order.setOrderId(getRandomOrderId(order.getUserId()));
        int insert = orderMapper.myInsert(order);
        int updateRentState = goodsEntityMapper.updateRentState(order.getGoodsEntityId(), order.getOrderRentWay() + 1);
        return insert==1&&updateRentState==1;
    }
}
