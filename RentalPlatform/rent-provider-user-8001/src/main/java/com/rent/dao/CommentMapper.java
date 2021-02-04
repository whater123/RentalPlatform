package com.rent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rent.pojo.base.user.UserComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author w
 */
@Repository
public interface CommentMapper extends BaseMapper<UserComment> {
    /**
     * 根据goodsId拿到返回所需信息
     * @param goodsId 商品集id
     * @return 返回所需信息
     */
    @Select("select * from user_comment uc,user u where uc.user_id=u.user_id and goods_id=#{goodsId} order by comment_star desc")
    List<UserComment> getCommentsAndUserInformation(@Param("goodsId") int goodsId);
}
