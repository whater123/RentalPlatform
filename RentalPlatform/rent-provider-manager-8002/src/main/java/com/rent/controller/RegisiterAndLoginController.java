package com.rent.controller;

import com.rent.constant.SystemConstant;
import com.rent.pojo.base.Enterprise;
import com.rent.pojo.view.LoginMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseService;
import com.rent.util.MD5util;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping("/manager")
public class RegisiterAndLoginController {
    @Autowired
    EnterpriseService enterpriseService;

    @RequestMapping("/toRegister")
    public ReturnMsg toRegister(@RequestBody Enterprise enterprise) {
        if(MyUtil.hasVoid(enterprise.getEntpAccount(),
                enterprise.getEntpChargePhone(),
                enterprise.getEntpName(),
                enterprise.getEntpPassword(),
                enterprise.getEntpShopName())){
            return new ReturnMsg("406",true,"参数不齐");
        }
        if(!MyUtil.isEmail(enterprise.getEntpAccount())){
            return new ReturnMsg("406",true,"注册用户名不是合法邮箱");
        }
        if(!MyUtil.isPhoneNumber(enterprise.getEntpChargePhone())){
            return new ReturnMsg("406",true,"注册联系方式不是合法手机号码");
        }
        if(enterprise.getEntpPassword().length() > 32){
            return new ReturnMsg("406",true,"密码过长");
        }
        if(enterprise.getEntpPassword().length() < 8){
            return new ReturnMsg("406",true,"密码过短");
        }
        if(enterprise.getEntpName().length() > 128){
            return new ReturnMsg("406",true,"公司名过长");
        }
        if(enterprise.getEntpName().length() < 2){
            return new ReturnMsg("406",true,"公司名过短");
        }
        if(enterprise.getEntpShopName().length() > 32){
            return new ReturnMsg("406",true,"店铺名称过长");
        }
        if(enterprise.getEntpShopName().length() < 2){
            return new ReturnMsg("406",true,"店铺名称过短");
        }
        if(enterpriseService.isThatExist("entp_account", enterprise.getEntpAccount())){
            return new ReturnMsg("400",true,"该邮箱已被注册");
        }
        if(enterpriseService.isThatExist("entp_charge_phone", enterprise.getEntpChargePhone())){
            return new ReturnMsg("400",true,"该电话号码已被注册");
        }
        if(enterpriseService.isThatExist("entp_name", enterprise.getEntpName())){
            return new ReturnMsg("400",true,"该企业名已被注册");
        }
        enterprise.setEntpPassword(MD5util.code(enterprise.getEntpPassword()));
        try{
            enterpriseService.getEnterpriseMapper().insert(enterprise);
            return new ReturnMsg("200",false,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500",true,"服务器内部错误");
        }
    }

    @RequestMapping("/toLogin")
    public ReturnMsg toLogin(@RequestBody LoginMsg loginMsg){
        if(MyUtil.hasVoid(loginMsg.getLoginAccount(),
                loginMsg.getLoginPassword())){
            return new ReturnMsg("406",true,"不接受，参数不齐");
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginMsg.getLoginAccount(), loginMsg.getLoginPassword());
        try {
            subject.login(token);
            if(SystemConstant.PLATFORM_USERNAME.equals(loginMsg.getLoginAccount())){
                return new ReturnMsg("200",false,"登录成功", 0);
            }else {
                return new ReturnMsg("200",false,"登录成功",
                        enterpriseService.getThoseEnterprises("entp_account", loginMsg.getLoginAccount()).get(0).getEntpId());
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new ReturnMsg("400",true,"用户名或密码错误");
        }
    }


    @RequestMapping("/login")
    public ReturnMsg login(@RequestBody LoginMsg loginMsg){
        return new ReturnMsg("403",true,"尚未登录");
    }

    @RequestMapping("/unauthorized")
    public ReturnMsg unauthorized(@RequestBody LoginMsg loginMsg){
        return new ReturnMsg("403",true,"尚未权限");
    }
}
