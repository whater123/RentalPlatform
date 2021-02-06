package com.rent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rent.pojo.base.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author w
 */
@Repository
public interface OrderMapper extends BaseMapper<Order> {
    @Select("select count(1) from `order`")
    Integer mySelectCount();

    @Insert("INSERT INTO `order`  ( order_id, order_parent_id, user_id, goods_entity_id, contact_id, logistics_id, order_urgent_relation, order_urgent_name, order_urgent_phone, order_create_time, order_rent_expect, order_stop_state, order_goods_count, order_rent_way, order_rent_time, order_rent_unit )  " +
                         "VALUES  ( #{orderId}, #{orderParentId}, #{userId}, #{goodsEntityId}, #{contactId}, #{logisticsId}, #{orderUrgentRelation}, #{orderUrgentName}, #{orderUrgentPhone}, #{orderCreateTime}, #{orderRentExpect}, #{orderStopState}, #{orderGoodsCount}, #{orderRentWay}, #{orderRentTime}, #{orderRentUnit} )")
    int myInsert(Order order);
}
