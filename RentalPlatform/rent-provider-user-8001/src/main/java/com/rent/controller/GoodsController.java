package com.rent.controller;

import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.view.ReturnMsg;
import com.rent.pojo.view.SimpleGoods;
import com.rent.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author w
 */
@RestController
@RequestMapping("/user/goods")
public class GoodsController {
    @Autowired
    RecommendService recommendService;

    @GetMapping("/getRecommendations")
    public ReturnMsg getRecommendations(){
        try{
            List<EnterpriseGoods> testSixGoods = recommendService.getTestSixGoods();
            if (testSixGoods.size()<6){
                return new ReturnMsg("1",true,"数据过少");
            }
            else {
                return new ReturnMsg("0",false,recommendService.returnHandle(testSixGoods));
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/{goodsId}")
    public ReturnMsg getGoods(@PathVariable("goodsId") String goodsId){
        try{
            EnterpriseGoods goodsImformation = recommendService.getGoodsImformation(Integer.parseInt(goodsId));
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
            List<SimpleGoods> list = recommendService.returnHandle(recommendService.getLastList(Integer.parseInt(goodsId)));
            if (list==null){
                return new ReturnMsg("1",true,"id参数错误");
            }
            return new ReturnMsg("0",false,list);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
