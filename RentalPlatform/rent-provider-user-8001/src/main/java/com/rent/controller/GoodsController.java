package com.rent.controller;

import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.UserComment;
import com.rent.pojo.view.GoodsAttribute;
import com.rent.pojo.view.ReturnMsg;
import com.rent.pojo.view.SimpleGoods;
import com.rent.service.CommentService;
import com.rent.service.GoodsService;
import com.rent.service.RecommendService;
import com.rent.util.JsonToMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author w
 */
@RestController
@RequestMapping("/user/goods")
public class GoodsController {
    @Autowired
    RecommendService recommendService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    CommentService commentService;

    @GetMapping("/getRecommendations/{userId}")
    public ReturnMsg getRecommendations(@PathVariable("userId") String userId){
        try{
            List<EnterpriseGoods> testSixGoods = recommendService.getTestSixGoods(userId);
            if (testSixGoods==null||testSixGoods.size()<6){
                return new ReturnMsg("1",true,"您已经刷到底了！");
            }
            else {
                return new ReturnMsg("0",false,goodsService.returnHandle(testSixGoods));
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/{goodsId}")
    public ReturnMsg getGoods(@PathVariable("goodsId") String goodsId){
        try{
            EnterpriseGoods goodsImformation = goodsService.getGoodsImformation(Integer.parseInt(goodsId));
            if (goodsImformation==null){
                return new ReturnMsg("1",true,"id参数无效");
            }
            else {
                return new ReturnMsg("0",false,goodsImformation);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getLastRecommendations/{goodsId}")
    public ReturnMsg getLastList(@PathVariable("goodsId") String goodsId){
        try{
            List<SimpleGoods> list = goodsService.returnHandle(recommendService.getLastList(Integer.parseInt(goodsId)));
            if (list==null){
                return new ReturnMsg("1",true,"id参数错误");
            }
            return new ReturnMsg("0",false,list);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getAttribute/{goodsId}")
    public ReturnMsg getAttribute(@PathVariable("goodsId") String goodsId){
        try{
            List<GoodsAttribute> goodsAttributes = goodsService.getGoodsAttributes(Integer.parseInt(goodsId));
            if (goodsAttributes==null){
                return new ReturnMsg("1",true,"不存在该商品");
            }
            else {
                return new ReturnMsg("0",false,goodsAttributes);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getGoodsEntities/{sortWay}")
    public ReturnMsg getGoodsEntities(@RequestBody GoodsAttribute goodsAttribute,@PathVariable("sortWay") String sortWay){
        try{
            if (sortWay==null|| "".equals(sortWay)){
                return new ReturnMsg("1",true,"sortWay参数缺失");
            }
            Map<String, String> map = (TreeMap<String, String>) JsonToMapUtil.getValue(goodsAttribute.getGoodsAttributes());
            List<EnterpriseGoodsEntity> goodsEntities
                    = goodsService.getGoodsEntities(Integer.parseInt(goodsAttribute.getGoodsId()), map);
            if (goodsEntities==null||goodsEntities.size()==0){
                return new ReturnMsg("1",true,"不含有此属性的商品");
            }else {
                return new ReturnMsg("0",false,goodsService.entitySort(goodsEntities,sortWay));
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/comments/{goodsId}")
    public ReturnMsg getComments(@PathVariable("goodsId") String goodsId){
        try{
            List<UserComment> userCommentsByGoodsId = commentService.getUserCommentsByGoodsId(Integer.parseInt(goodsId));
            if (userCommentsByGoodsId==null||userCommentsByGoodsId.size()==0){
                return new ReturnMsg("1",true,"暂无评论");
            } else{
                return new ReturnMsg("0",false,userCommentsByGoodsId);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
