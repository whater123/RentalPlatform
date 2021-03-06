package com.rent.controller.enterprise;

import com.rent.config.ShiroUtil;
import com.rent.pojo.base.manager.Enterprise;
import com.rent.pojo.base.manager.EnterpriseAuthentication;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseAuthenticationService;
import com.rent.service.EnterpriseService;
import com.rent.service.UtilsService;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author obuivy
 */
@RestController
@RequestMapping("/manager/enterprise")
public class EntpAuthenticationController {
    @Autowired
    EnterpriseAuthenticationService enterpriseAuthenticationService;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    UtilsService utilsService;


    @RequestMapping("/toAuthentication")
    public ReturnMsg toAuthentication(@RequestBody EnterpriseAuthentication authentication){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("30101",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("enterprise_manager")){
            return new ReturnMsg("30201",true,"尚未授权");
        }
        if(MyUtil.strHasVoid(authentication.getAuthAddress(),
                authentication.getAuthCardPictureId(),
                authentication.getAuthLicensePictureId(),
                authentication.getAuthEmail())){
            return new ReturnMsg("40101",true,"传参不齐");
        }
        if(!MyUtil.isEmail(authentication.getAuthEmail())){
            return new ReturnMsg("40201",true,"注册邮箱不是合法邮箱");
        }
        if(authentication.getAuthAddress().length() > 128){
            return new ReturnMsg("40202",true,"该企业地址过长");
        }
        if(authentication.getAuthAddress().length() < 2){
            return new ReturnMsg("40203",true,"该企业地址过短");
        }
        if(enterpriseAuthenticationService.getThoseEnterpriseAuthentications("auth_email",
                authentication.getAuthEmail()).size() == 1){
            return new ReturnMsg("40301",true,"该邮箱已被注册");
        }
        if(utilsService.getThosePictures("picture_id",authentication.getAuthCardPictureId()).size() == 0){
            return new ReturnMsg("40302",true,"该身份证图组不存在");
        }
        if(utilsService.getThosePictures("picture_id",authentication.getAuthLicensePictureId()).size() == 0){
            return new ReturnMsg("40303",true,"该营业执照图组不存在");
        }
        try{
            authentication.setAuthTime(MyUtil.getNowDateTime());
            authentication.setAuthState(0);
            authentication.setEntpId(enterpriseService.getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId());
            enterpriseAuthenticationService.getMapper().insert(authentication);
            return new ReturnMsg("20001",false,"认证提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("50001",true,"插入数据失败");
        }
    }


}
