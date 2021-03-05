package com.rent.controller.authEnterprise;

import com.alibaba.fastjson.JSON;
import com.rent.config.ShiroUtil;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.DataVisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MSI-PC
 */
@RestController
@RequestMapping("/manager/authEnterprise")
public class AuthDataVisController {
    @Autowired
    DataVisService dataVisService;

    @RequestMapping(value = "/getPendingOrder", produces = "application/json;charset=UTF-8")
    public ReturnMsg getPendingOrder(){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        return new ReturnMsg("200", false, "获取成功", dataVisService.getPendingOrder());
    }

    @RequestMapping(value = "/getProfitInfo", produces = "application/json;charset=UTF-8")
    public ReturnMsg getProfitInfo() {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        try{
            return new ReturnMsg("200", false, "获取成功", dataVisService.getProfitInfoJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误", e.getMessage());
        }
    }

    @RequestMapping(value = "/getOrderInfo", produces = "application/json;charset=UTF-8")
    public ReturnMsg getOrderInfo(){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        try{
            return new ReturnMsg("200", false, "获取成功", dataVisService.getOrderInfoJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误", e.getMessage());
        }
    }

    @RequestMapping(value = "/getGoodsInfo", produces = "application/json;charset=UTF-8")
    public ReturnMsg getGoodsInfo(){
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        try{
            return new ReturnMsg("200", false, "获取成功", dataVisService.getGoodsInfoJSONObejct());
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误", e.getMessage());
        }
    }

    @RequestMapping(value = "/getOrderCurveMonthly", produces = "application/json;charset=UTF-8")
    public ReturnMsg getOrderCurveMonthly(@RequestBody String json) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        try{
            if(JSON.parseObject(json).getInteger("month") >= 1){
                return new ReturnMsg("402", true, "参数不合法");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("401", true, "参数不齐");
        }
        try{
            return new ReturnMsg("200", false, "获取成功",dataVisService.getOrderCurveMonthlyJsonArray(json));
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500", true, "服务器错误", e.getMessage());
        }
    }
}