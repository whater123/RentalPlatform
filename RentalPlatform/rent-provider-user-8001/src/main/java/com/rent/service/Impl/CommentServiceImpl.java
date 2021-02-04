package com.rent.service.Impl;

import com.rent.dao.CommentMapper;
import com.rent.pojo.base.user.UserComment;
import com.rent.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author w
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<UserComment> getUserCommentsByGoodsId(int goodsId) {
        return commentMapper.getCommentsAndUserInformation(goodsId);
    }
}
