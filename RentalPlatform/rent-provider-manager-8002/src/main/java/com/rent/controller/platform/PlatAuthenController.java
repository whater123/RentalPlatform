package com.rent.controller.platform;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ShiroUtil;
import com.rent.pojo.base.EnterpriseAuthentication;
import com.rent.pojo.view.ReturnDoubleData;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseAuthenticationService;
import com.rent.service.EnterpriseService;
import com.rent.service.PlatformService;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author obuivy
 */
@RestController
@RequestMapping("/manager/platform")
public class PlatAuthenController {
    @Autowired
    PlatformService platformService;
    @Autowired
    EnterpriseAuthenticationService enterpriseAuthenticationService;
    @Autowired
    EnterpriseService enterpriseService;

    @RequestMapping(value = "/getSimpleAuthentications", produces = "application/json;charset=UTF-8")
    public ReturnDoubleData getSimpleAuthentications(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnDoubleData("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("platform_manager")){
            return new ReturnDoubleData("302",true,"尚未授权");
        }
        return platformService.getAuthentications(json);
    }

    @RequestMapping(value = "/getDetailAuthentication", produces = "application/json;charset=UTF-8")
    public ReturnMsg getDetailAuthentication(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("301",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("platform_manager")){
            return new ReturnMsg("302",true,"尚未授权");
        }
        if(MyUtil.jsonHasVoid(json,"authId")){
            return new ReturnMsg("401",true,"传参不合法");
        }
        return new ReturnMsg("200",false,"获取成功",
                enterpriseAuthenticationService.getThoseEnterpriseAuthentications("auth_id",
                        JSON.parseObject(json).getString("authId")).get(0));
    }

    @RequestMapping(value = "/handleAuthentication", produces = "application/json;charset=UTF-8")
    public ReturnMsg handleAuthentication(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("30101",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("platform_manager")){
            return new ReturnMsg("30201",true,"尚未授权");
        }
        if(MyUtil.jsonHasVoid(json,"authId","authState")){
            return new ReturnMsg("401",true,"传参不齐");
        }
        try{
            EnterpriseAuthentication authentication = enterpriseAuthenticationService.
                    getThoseEnterpriseAuthentications("auth_id",
                    JSON.parseObject(json).getString("authId")).get(0);
            authentication.setAuthState(JSON.parseObject(json).getInteger("authState"));
            enterpriseAuthenticationService.getMapper().update(authentication,
                    new QueryWrapper<EnterpriseAuthentication>()
                            .eq("auth_id", authentication.getAuthId()));
            return new ReturnMsg("200",true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500",true,"修改失败");
        }
    }

    @RequestMapping(value = "/getEnterpriseInfo", produces = "application/json;charset=UTF-8")
    public ReturnMsg getEnterpriseInfo(@RequestBody String json){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("30101",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("platform_manager")){
            return new ReturnMsg("30201",true,"尚未授权");
        }
        if(MyUtil.jsonHasVoid(json,"entpId")){
            return new ReturnMsg("401",true,"传参不齐");
        }
        return new ReturnMsg("200",false,"获取成功",
                enterpriseService.getThoseEnterprises("entp_id",
                        JSON.parseObject(json).getString("entpId")).get(0));
    }
}
