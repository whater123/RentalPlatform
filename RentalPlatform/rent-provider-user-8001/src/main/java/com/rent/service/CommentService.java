package com.rent.service;

import com.rent.pojo.base.user.UserComment;

import java.util.List;

/**
 * @author w
 */
public interface CommentService {
    /**
     * 根据商品集id获取评论，按星级降序
     * @param goodsId 商品集id
     * @return 评论列表
     */
    List<UserComment> getUserCommentsByGoodsId(int goodsId);
}
