package com.rent.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rent.config.ShiroUtil;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseService;
import com.rent.service.Goods2Service;
import com.rent.service.UtilsService;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/authEnterprise")
public class GoodsController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    Goods2Service goods2Service;
    @Autowired
    UtilsService utilsService;

    @RequestMapping("/addGoods")
    public ReturnMsg addGoods(@RequestBody EnterpriseGoods goods){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("authEnterprise_manager")){
            return new ReturnMsg("302",true,"尚未授权");
        }
        if(MyUtil.strHasVoid(
                goods.getGoodsPicture(),
                goods.getGoodsTitle(),
                goods.getGoodsIntroduce(),
                goods.getGoodsBigCategory()
        )){
            return new ReturnMsg("401",true,"传参不齐");
        }
        if(MyUtil.intHasVoid(
                goods.getGoodsCategoryId())
        ){
            return new ReturnMsg("401",true,"传参不齐");
        }
        if(goods.getGoodsTitle().length() < 2){
            return new ReturnMsg("402",true,"标题过短");
        }
        if(goods.getGoodsTitle().length() > 40){
            return new ReturnMsg("402",true,"标题过长");
        }
        if(utilsService.getThosePictures("picture_id", goods.getGoodsPicture()).size() == 0){
            return new ReturnMsg("403",true,"上传的图组不存在");
        }
        //分类验证（暂无）
        if(goods2Service.insertGoods(goods)){
            return new ReturnMsg("200",false,"添加成功", goods.getGoodsId());
        }else {
            return new ReturnMsg("500",true,"添加失败");
        }
    }
    @RequestMapping("/addGoodsEntity")
    public ReturnMsg addGoodsEntity(@RequestBody EnterpriseGoodsEntity goodsEntity) throws Exception {
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("authEnterprise_manager")){
            return new ReturnMsg("302",true,"尚未授权");
        }
        try{
            if(!goods2Service.isNowEntpId(goods2Service.getThoseGoods("goods_id",
                    String.valueOf(goodsEntity.getGoodsId())).get(0).getEntpId())){
                return new ReturnMsg("302",true,"尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("403",true,"该商品集不存在");
        }
        if(MyUtil.strHasVoid(
                goodsEntity.getGoodsCurrentPrice(),
                goodsEntity.getGoodsDeposit(),
                goodsEntity.getGoodsPrice(),
                goodsEntity.getGoodsRegularPrice())
        ){
            return new ReturnMsg("401",true,"传参不齐");
        }
        goodsEntity.setGoodsCurrentPrice(MoneyUtil.addTail(goodsEntity.getGoodsCurrentPrice()));
        goodsEntity.setGoodsDeposit(MoneyUtil.addTail(goodsEntity.getGoodsDeposit()));
        goodsEntity.setGoodsPrice(MoneyUtil.addTail(goodsEntity.getGoodsPrice()));
        goodsEntity.setGoodsRegularPrice(MoneyUtil.addTail(goodsEntity.getGoodsRegularPrice()));
        if(MyUtil.intHasVoid(
                goodsEntity.getAddNumber(),
                goodsEntity.getGoodsRegularUnit(),
                goodsEntity.getGoodsId(),
                goodsEntity.getGoodsRentExpert(),
                goodsEntity.getGoodsRentWay())
        ){
            return new ReturnMsg("401",true,"传参不齐");
        }
        if(!MyUtil.isAllRuleMoney(
                goodsEntity.getGoodsCurrentPrice(),
                goodsEntity.getGoodsDeposit(),
                goodsEntity.getGoodsPrice(),
                goodsEntity.getGoodsRegularPrice())){
            return new ReturnMsg("402",true,"传参不合法");
        }
        if(!goods2Service.isRuleProperties(goods2Service.
                getGoodsAttribute(goodsEntity.getGoodsId()),goodsEntity.getProperties())
        ){
            return new ReturnMsg("403",true,"属性不匹配");
        }
        if(goods2Service.insertGoodsEntity(goodsEntity)){
            return new ReturnMsg("200",false,"添加成功");
        }else {
            return new ReturnMsg("500",true,"添加失败");
        }
    }
    @RequestMapping("/addGoodsProperty")
    public ReturnMsg addGoodsProperty(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("authEnterprise_manager")){
            return new ReturnMsg("302",true,"尚未授权");
        }
        if(MyUtil.jsonHasVoid(json,"goodsId","properties")){
            return new ReturnMsg("401",true,"传参不齐");
        }
        try{
            if(!goods2Service.isNowEntpId(goods2Service.getThoseGoods("goods_id",
                    JSON.parseObject(json).getString("goodsId")).get(0).getEntpId())){
                return new ReturnMsg("302",true,"尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("40301",true,"该商品集不存在");
        }
        if(goods2Service.insertGoodsProperty(json)){
            return new ReturnMsg("200",false,"添加成功");
        }else {
            return new ReturnMsg("40302",true,"添加失败，该商品集属性已存在");
        }
    }
    @RequestMapping("/getGoodsProperty")
    public ReturnMsg getGoodsProperty(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("authEnterprise_manager")){
            return new ReturnMsg("302",true,"尚未授权");
        }
        if(MyUtil.jsonHasVoid(json,"goodsId")){
            return new ReturnMsg("401",true,"传参不齐");
        }
        try{
            if(!goods2Service.isNowEntpId(goods2Service.getThoseGoods("goods_id",
                    JSON.parseObject(json).getString("goodsId")).get(0).getEntpId())){
                return new ReturnMsg("302",true,"尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("40301",true,"所查找的商品集不存在");
        }
        Object data = goods2Service.getGoodsAttribute(JSONObject.parseObject(json).getInteger("goodsId"));
        if(!"".equals(data)){
            return new ReturnMsg("200",false,"获取成功",data);
        }else {
            return new ReturnMsg("40302",true,"获取失败，该商品集还未添加属性");
        }

    }
}
