package com.rent.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.rent.pojo.base.manager.EnterpriseCategory;
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
import org.apache.ibatis.annotations.Param;
import org.slf4j.LoggerFactory;
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
            EnterpriseGoods goodsImformation = goodsService.getGoodsInformation(Integer.parseInt(goodsId));
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
            if (list.size()==0){
                return new ReturnMsg("1",true,"暂无推荐");
            }
            if (list.size()>6){
                return new ReturnMsg("0",false,list.subList(0,6));
            }else {
                return new ReturnMsg("0",false,list);
            }
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

    @GetMapping("/getGoodsEntities/{sortWay}/{goodsId}/{goodsAttributes}")
    public ReturnMsg getGoodsEntities(@PathVariable("sortWay") String sortWay, @PathVariable("goodsAttributes") String goodsAttributes, @PathVariable("goodsId") String goodsId){
        try{
            if (sortWay==null|| "".equals(sortWay)){
                return new ReturnMsg("1",true,"sortWay参数缺失");
            }
            Map<String, String> map = (TreeMap<String, String>) JsonToMapUtil.getValue(goodsAttributes);
            System.out.println(map);
            List<EnterpriseGoodsEntity> goodsEntities
                    = goodsService.getGoodsEntities(Integer.parseInt(goodsId), map);
            if (goodsEntities==null||goodsEntities.size()==0){
                return new ReturnMsg("1",true,"不含有此属性的商品");
            }else {
                return new ReturnMsg("0",false,goodsService.entitySortAndHandle(goodsEntities,sortWay));
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

    @GetMapping("/getHotGoods")
    public ReturnMsg getHotGoods(){
        try{
            List<EnterpriseGoods> hotGoods = goodsService.getHotGoods();
            return new ReturnMsg("0",false,goodsService.returnHandle(hotGoods));
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getEntity/{goodsEntityId}")
    public ReturnMsg getEntity(@PathVariable("goodsEntityId") String goodsEntityId){
        try{
            EnterpriseGoodsEntity entity = goodsService.getEntity(Integer.parseInt(goodsEntityId));
            if (entity==null){
                return new ReturnMsg("1",true,"商品个体不存在");
            }else {
                return new ReturnMsg("0",false,entity);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/search/{context}")
    public ReturnMsg search(@PathVariable("context") String context){
        try{
            List<EnterpriseGoods> search = goodsService.search(context);
            if (search==null||search.size()==0){
                return new ReturnMsg("1",true,"搜索结果不存在");
            }else {
                return new ReturnMsg("0",false,search);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getHotCategory/{bigCategoryId}")
    public ReturnMsg getHotCategory(@PathVariable("bigCategoryId") String bigCategoryId){
        try{
            List<EnterpriseGoods> hotCategoryGoods = goodsService.getHotCategoryGoods(Integer.parseInt(bigCategoryId));
            if (hotCategoryGoods==null||hotCategoryGoods.size()==0){
                return new ReturnMsg("1",true,"无商品集");
            }
            return new ReturnMsg("0",false,hotCategoryGoods);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/entp/category/{entpId}")
    public ReturnMsg category(@PathVariable("entpId") String entpId){
        try{
            List<EnterpriseCategory> enterpriseCategory = goodsService.getEnterpriseCategory(Integer.parseInt(entpId));
            if (enterpriseCategory==null||enterpriseCategory.size()==0){
                return new ReturnMsg("1",false,"无分类");
            }else {
                return new ReturnMsg("0",false,enterpriseCategory);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/entp/{entpId}/{categoryId}")
    public ReturnMsg getEntpGoods(@PathVariable("entpId") String entpId,@PathVariable("categoryId") String categoryId){
        try{
            if (categoryId==null|| "".equals(categoryId)){
                categoryId="0";
            }
            List<EnterpriseGoods> entpGoods = goodsService.getEntpGoods(Integer.parseInt(entpId), Integer.parseInt(categoryId));
            if (entpGoods==null||entpGoods.size()==0){
                return new ReturnMsg("1",false,"暂无商品");
            }else {
                return new ReturnMsg("0",false,entpGoods);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
