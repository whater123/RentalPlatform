package com.rent.controller;

import com.rent.pojo.base.EnterpriseAuthentication;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseAuthenticationService;
import com.rent.service.EnterpriseService;
import com.rent.service.UtilsService;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/manager/enterprise")
public class EnterpriseController {
    @Autowired
    EnterpriseAuthenticationService enterpriseAuthenticationService;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    UtilsService utilsService;

    @RequestMapping("/toAuthentication")
    public ReturnMsg toAuthentication(@RequestBody EnterpriseAuthentication authentication){
        Subject subject = SecurityUtils.getSubject();
        System.out.println(subject.isAuthenticated());
        System.out.println(subject.hasRole("enterprise_manager"));
        if(subject.isAuthenticated()){
            return new ReturnMsg("403",true,"尚未认证");
        }
        if(subject.hasRole("enterprise_manager")){
            return new ReturnMsg("403",true,"尚未授权");
        }
        if(MyUtil.hasVoid(authentication.getAuthAddress(),
                authentication.getAuthCardPictureId(),
                authentication.getAuthLicensePictureId(),
                authentication.getAuthEmail(),
                String.valueOf(authentication.getEntpId()))){
            return new ReturnMsg("406",true,"传参不齐");
        }
        if(!MyUtil.isEmail(authentication.getAuthEmail())){
            return new ReturnMsg("406",true,"注册邮箱不是合法邮箱");
        }
        if(enterpriseAuthenticationService.getThoseEnterpriseAuthentications("auth_email",authentication.getAuthEmail()).size() == 1){
            return new ReturnMsg("400",true,"该邮箱已被注册");
        }
        if(utilsService.getThosePictures("picture_id",authentication.getAuthCardPictureId()).size() == 0){
            return new ReturnMsg("406",true,"该身份证图组不存在");
        }
        if(utilsService.getThosePictures("picture_id",authentication.getAuthLicensePictureId()).size() == 0){
            return new ReturnMsg("406",true,"该营业执照图组不存在");
        }
        if(enterpriseService.getThoseEnterprises("entp_id", String.valueOf(authentication.getEntpId())).size() == 0){
            return new ReturnMsg("406",true,"该企业id不存在");
        }
        if(authentication.getAuthAddress().length() > 128){
            return new ReturnMsg("406",true,"该企业地址过长");
        }
        if(authentication.getAuthAddress().length() < 2){
            return new ReturnMsg("406",true,"该企业地址过短");
        }
        authentication.setAuthState(0);
        try{
            enterpriseAuthenticationService.getMapper().insert(authentication);
            return new ReturnMsg("200",false,"认证成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500",true,"服务器内部错误");
        }
    }

    @RequestMapping("/sb")
    public String sbhzh(){
        return "hzh";
    }
}
