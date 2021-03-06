package com.rent.controller.authEnterprise;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ShiroUtil;
import com.rent.dao.EnterpriseGoodsMapper;
import com.rent.dao.UserCommentMapper;
import com.rent.dao.UserMapper;
import com.rent.pojo.base.*;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseCategoryService;
import com.rent.service.EnterpriseService;
import com.rent.service.EntpGoodsService;
import com.rent.service.UtilsService;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/manager/authEnterprise")
public class AuthGoodsController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EntpGoodsService entpGoodsService;
    @Autowired
    UtilsService utilsService;
    @Autowired
    EnterpriseCategoryService enterpriseCategoryService;
    @Autowired
    UserCommentMapper userCommentMapper;
    @Autowired
    UserMapper userMapper;

    @RequestMapping(value = "/addGoods", produces = "application/json;charset=UTF-8")
    public ReturnMsg addGoods(@RequestBody EnterpriseGoods goods) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        if (MyUtil.strHasVoid(
                goods.getGoodsPicture(),
                goods.getGoodsTitle(),
                goods.getGoodsIntroduce()
        )) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        if (MyUtil.intHasVoid(
                goods.getGoodsCategoryId(),
                goods.getGoodsBigCategory())
        ) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        if (goods.getGoodsTitle().length() < 2) {
            return new ReturnMsg("402", true, "标题过短");
        }
        if (goods.getGoodsTitle().length() > 40) {
            return new ReturnMsg("402", true, "标题过长");
        }
        if (utilsService.getThosePictures("picture_id", goods.getGoodsPicture()).size() == 0) {
            return new ReturnMsg("403", true, "上传的图组不存在");
        }
        //分类验证（暂无）
        if (entpGoodsService.insertGoods(goods)) {
            return new ReturnMsg("200", false, "添加成功", goods.getGoodsId());
        } else {
            return new ReturnMsg("500", true, "添加失败");
        }
    }

    @RequestMapping(value = "/deleteGoods", produces = "application/json;charset=UTF-8")
    public ReturnMsg deleteGoods(@RequestBody EnterpriseGoods goods){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        if (MyUtil.intHasVoid(goods.getGoodsId())){
            return new ReturnMsg("401", true, "传参不齐");
        }
        if (entpGoodsService.getThoseGoods("goods_id",String.valueOf(goods.getGoodsId())).size() == 0){
            return new ReturnMsg("403", true, "目标商品集不存在");
        }
        if (!utilsService.isNowEntpId(entpGoodsService.getThoseGoods(
                "goods_id",String.valueOf(goods.getGoodsId())).get(0).getEntpId())){
            return new ReturnMsg("302", true, "尚未授权，目标商品集不属于您");
        }
        if (entpGoodsService.deleteGoods(goods.getGoodsId())){
            return new ReturnMsg("200",false,"删除成功");
        }else {
            return new ReturnMsg("500",false,"删除失败");
        }
    }

    @RequestMapping(value = "/updateGoods", produces = "application/json;charset=UTF-8")
    public ReturnMsg updateGoods(@RequestBody EnterpriseGoods goods){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("30101", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("30201", true, "尚未授权");
        }
        if (MyUtil.intHasVoid(goods.getGoodsId())){
            return new ReturnMsg("40101", true, "传参不齐");
        }
        if (entpGoodsService.getThoseGoods("goods_id",String.valueOf(goods.getGoodsId())).size() == 0){
            return new ReturnMsg("40301", true, "目标商品集不存在");
        }
        if (!utilsService.isNowEntpId(entpGoodsService.getThoseGoods(
                "goods_id",String.valueOf(goods.getGoodsId())).get(0).getEntpId())){
            return new ReturnMsg("30201", true, "尚未授权，您无权修改目标商品集");
        }
        if (!MyUtil.strHasVoid(goods.getGoodsTitle())){
            if (goods.getGoodsTitle().length() < 2 || goods.getGoodsTitle().length() > 40) {
                return new ReturnMsg("40301",true,"标题传参不合法");
            }
        }
        if (!MyUtil.strHasVoid(goods.getGoodsPicture())){
            if (utilsService.getThosePictures("picture_id", goods.getGoodsPicture()).size() == 0) {
                return new ReturnMsg("40302",true,"该图组不存在");
            }
        }
        if (!MyUtil.intHasVoid(goods.getGoodsCategoryId())){
            if (enterpriseCategoryService.getThoseEnterpriseCategories(
                    "category_id", String.valueOf(goods.getGoodsCategoryId())).size() == 0) {
                return new ReturnMsg("40303",true,"该企业内部分类不存在");
            }
            if (!utilsService.isNowEntpId(enterpriseCategoryService.getThoseEnterpriseCategories(
                    "category_id", String.valueOf(goods.getGoodsCategoryId())).get(0).getEntpId())){
                return new ReturnMsg("30202",true,"您无权使用该企业内部分类");
            }
        }
        if (!MyUtil.intHasVoid(goods.getGoodsBigCategory())){
            if (enterpriseCategoryService.getThoseEnterpriseCategories(
                    "category_id", String.valueOf(goods.getGoodsBigCategory())).size() == 0) {
                return new ReturnMsg("40304",true,"该大分类不存在");
            }
            if (enterpriseCategoryService.getThoseEnterpriseCategories(
                    "category_id", String.valueOf(goods.getGoodsBigCategory())).get(0).getEntpId() != 0){
                return new ReturnMsg("40305",true,"该分类不是大分类");
            }
        }

        try{
            if (entpGoodsService.updateGoods(goods)){
                return new ReturnMsg("20001",false,"修改成功");
            }else {
                return new ReturnMsg("20101",false,"未进行修改");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("50001",true,"修改失败");
        }
    }

    @RequestMapping(value = "/getGoods", produces = "application/json;charset=UTF-8")
    public ReturnMsg getGoods(){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("30101", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("30201", true, "尚未授权");
        }
        return new ReturnMsg("200",false,"获取成功",
                entpGoodsService.getThoseGoods("entp_id",
                        String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId())));
    }


    @RequestMapping(value = "/addGoodsProperty", produces = "application/json;charset=UTF-8")
    public ReturnMsg addGoodsProperty(@RequestBody String json) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        if (MyUtil.jsonHasVoid(json, "goodsId", "properties")) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        try {
            if (!utilsService.isNowEntpId(entpGoodsService.getThoseGoods("goods_id",
                    JSON.parseObject(json).getString("goodsId")).get(0).getEntpId())) {
                return new ReturnMsg("302", true, "尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("40301", true, "该商品集不存在");
        }
        if (entpGoodsService.insertGoodsProperty(json)) {
            return new ReturnMsg("200", false, "添加成功");
        } else {
            return new ReturnMsg("40302", true, "添加失败，该商品集属性已存在");
        }
    }

    @RequestMapping(value = "/getGoodsProperty", produces = "application/json;charset=UTF-8")
    public ReturnMsg getGoodsProperty(@RequestBody String json) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        if (MyUtil.jsonHasVoid(json, "goodsId")) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        try {
            if (!utilsService.isNowEntpId(entpGoodsService.getThoseGoods("goods_id",
                    JSON.parseObject(json).getString("goodsId")).get(0).getEntpId())) {
                return new ReturnMsg("302", true, "尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("40301", true, "所查找的商品集不存在");
        }
        Object data = entpGoodsService.getGoodsAttribute(JSONObject.parseObject(json).getInteger("goodsId"));
        if (!"".equals(data)) {
            return new ReturnMsg("200", false, "获取成功", data);
        } else {
            return new ReturnMsg("40302", true, "获取失败，该商品集还未添加属性");
        }

    }

    @RequestMapping(value = "/getComment", produces = "application/json;charset=UTF-8")
    public ReturnMsg getComment(@RequestBody EnterpriseGoods goods) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        if (MyUtil.intHasVoid(goods.getGoodsId())) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        try {
            List<EnterpriseGoods> thoseGoods = entpGoodsService.getThoseGoods("goods_id",
                    String.valueOf(goods.getGoodsId()));
            if (thoseGoods.size() != 0) {
                if (utilsService.isNowEntpId(thoseGoods.get(0).getEntpId())) {
                    QueryWrapper<UserComment> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("goods_id", goods.getGoodsId());
                    List<UserComment> userComments = userCommentMapper.selectList(queryWrapper);
                    if(userComments.isEmpty()){
                        return new ReturnMsg("200", false, "尚无评论");
                    }
                    for (UserComment userComment :
                            userComments) {
                        User user = userMapper.selectById(userComment.getUserId());
                        userComment.setUserName(user.getUserName());
                        userComment.setUserPictureId(user.getUserPictureId());
                    }
                    return new ReturnMsg("200", false, "获取成功", userComments);
                } else {
                    return new ReturnMsg("403", true, "获取成功", "尚未授权，您无权获取该商品集的评论");
                }
            } else {
                return new ReturnMsg("403", true, "获取成功", "该商品集不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误");
        }
    }
}
