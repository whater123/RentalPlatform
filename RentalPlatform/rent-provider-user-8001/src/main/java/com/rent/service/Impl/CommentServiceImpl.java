package com.rent.service.Impl;

import com.rent.dao.CommentMapper;
import com.rent.dao.TradeMapper;
import com.rent.pojo.base.Trade;
import com.rent.pojo.base.user.UserComment;
import com.rent.service.CommentService;
import com.rent.util.MyUtil;
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
    @Autowired
    TradeMapper tradeMapper;

    @Override
    public List<UserComment> getUserCommentsByGoodsId(int goodsId) {
        return commentMapper.getCommentsAndUserInformation(goodsId);
    }

    @Override
    public boolean insertComment(UserComment comment) {
        comment.setCommentTime(MyUtil.getNowTime());
        int insert = commentMapper.insert(comment);
        if (insert!=1){
            return false;
        }
        Trade trade = tradeMapper.mySelectById(comment.getOrderId());
        trade.setOrderState(7);
        int i = tradeMapper.updateById(trade);
        if (i!=1){
            commentMapper.deleteById(comment.getCommentId());
            return false;
        }else {
            return true;
        }
    }
}
