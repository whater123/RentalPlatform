package com.rent.controller;

import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.EnterpriseGoodsEntity;
import com.rent.pojo.view.GoodsAttribute;
import com.rent.pojo.view.ReturnMsg;
import com.rent.pojo.view.SimpleGoods;
import com.rent.service.GoodsService;
import com.rent.service.RecommendService;
import com.rent.util.JsonToMapUtil;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.reflect.generics.tree.Tree;

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

    @GetMapping("/getRecommendations")
    public ReturnMsg getRecommendations(){
        try{
            List<EnterpriseGoods> testSixGoods = recommendService.getTestSixGoods();
            if (testSixGoods.size()<6){
                return new ReturnMsg("1",true,"数据过少");
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

    @PostMapping("/getGoodsEntities")
    public ReturnMsg getGoodsEntities(@RequestBody GoodsAttribute goodsAttribute){
        try{
            Map<String, String> map = (TreeMap<String, String>) JsonToMapUtil.getValue(goodsAttribute.getGoodsAttributes());
            List<EnterpriseGoodsEntity> goodsEntities
                    = goodsService.getGoodsEntities(Integer.parseInt(goodsAttribute.getGoodsId()), map);
            if (goodsEntities==null||goodsEntities.size()==0){
                return new ReturnMsg("1",true,"不含有此属性的商品");
            }else {
                return new ReturnMsg("0",false,goodsEntities);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
