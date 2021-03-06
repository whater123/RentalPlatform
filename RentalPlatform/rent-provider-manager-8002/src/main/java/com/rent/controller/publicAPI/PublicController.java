package com.rent.controller.publicAPI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.ExprUnparser;
import com.rent.config.ShiroUtil;
import com.rent.pojo.base.EnterpriseCategory;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseCategoryService;
import com.rent.service.EnterpriseService;
import com.rent.util.ExpressUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author obuivy
 */
@RestController
@RequestMapping("/manager")
public class PublicController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EnterpriseCategoryService enterpriseCategoryService;

    @RequestMapping("/getBigCategory")
    public ReturnMsg getBigCategory(){
        return new ReturnMsg("200", false, "获取成功",
                enterpriseCategoryService.getThoseEnterpriseCategories("entp_id","0"));
    }

    @RequestMapping("/queryExpress")
    public String queryExpress(@RequestBody String json){
        System.out.println(json);
        JSONObject jsonObject = JSON.parseObject(json);
        if(MyUtil.strHasVoid(json, "no")){
            return "传参不齐";
        }
        return ExpressUtil.queryExpress(jsonObject.getString("no"), jsonObject.getString("type"));
    }
    @RequestMapping(value = "/whoAmI", produces = "application/json;charset=UTF-8")
    public ReturnMsg whoAmI() {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (ShiroUtil.hasRoles("platform_manager")) {
            return new ReturnMsg("20001", false, "你是平台管理员");
        }
        if (ShiroUtil.hasRoles("authEnterprise_manager","enterprise_manager")) {
            return new ReturnMsg("20002", false, "你是已认证企业管理员",
                    enterpriseService.getThoseEnterprises("entp_account",
                            String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0));
        }
        if (ShiroUtil.hasRoles("enterprise_manager")) {
            return new ReturnMsg("20003", false, "你是未认证企业管理员",
                    enterpriseService.getThoseEnterprises("entp_account",
                            String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0));
        }
        return new ReturnMsg("500",true,"未知错误");
    }
}

