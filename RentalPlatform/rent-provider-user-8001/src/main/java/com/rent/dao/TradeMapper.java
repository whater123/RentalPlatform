package com.rent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rent.pojo.base.Trade;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author w
 */
@Repository
public interface TradeMapper extends BaseMapper<Trade> {
    @Select("select * from `trade` where order_id=#{orderId}")
    Trade mySelectById(@Param("orderId") String orderId);
}
