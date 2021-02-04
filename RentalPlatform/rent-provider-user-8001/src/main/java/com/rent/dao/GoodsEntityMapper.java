package com.rent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author w
 */
@Repository
public interface GoodsEntityMapper extends BaseMapper<EnterpriseGoodsEntity> {
    @Select("select goods_entity_id from enterprise_goods_entity where goods_rent_way=3 or goods_rent_way=#{rentKey}")
    List<Integer> selectIdListByRentKey(@Param("rentKey") int rentKey);
}
