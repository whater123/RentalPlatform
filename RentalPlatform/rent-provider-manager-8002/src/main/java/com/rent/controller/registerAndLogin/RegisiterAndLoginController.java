package com.rent.controller.registerAndLogin;

import com.rent.constant.SystemConstant;
import com.rent.pojo.base.Enterprise;
import com.rent.pojo.view.LoginMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseService;
import com.rent.util.MD5util;
import com.rent.util.MyUtil;
import com.rent.util.RexExUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author obuivy
 */
@RestController
@RequestMapping("/manager")
public class RegisiterAndLoginController {
    @Autowired
    EnterpriseService enterpriseService;

    @RequestMapping("/toRegister")
    public ReturnMsg toRegister(@RequestBody Enterprise enterprise) {
        if(MyUtil.strHasVoid(enterprise.getEntpAccount(),
                enterprise.getEntpChargePhone(),
                enterprise.getEntpName(),
                enterprise.getEntpPassword(),
                enterprise.getEntpShopName())){
            return new ReturnMsg("40101",true,"参数不齐");
        }
        if(!MyUtil.isEmail(enterprise.getEntpAccount())){
            return new ReturnMsg("40201",true,"注册用户名不是合法邮箱");
        }
        if(!MyUtil.isPhoneNumber(enterprise.getEntpChargePhone())){
            return new ReturnMsg("40202",true,"注册联系方式不是合法手机号码");
        }
        if(enterprise.getEntpPassword().length() > 32){
            return new ReturnMsg("40203",true,"密码过长");
        }
        if(enterprise.getEntpPassword().length() < 8){
            return new ReturnMsg("40204",true,"密码过短");
        }
        if(enterprise.getEntpName().length() > 128){
            return new ReturnMsg("40205",true,"公司名过长");
        }
        if(enterprise.getEntpName().length() < 2){
            return new ReturnMsg("40206",true,"公司名过短");
        }
        if(enterprise.getEntpShopName().length() > 32){
            return new ReturnMsg("40207",true,"店铺名称过长");
        }
        if(enterprise.getEntpShopName().length() < 2){
            return new ReturnMsg("40208",true,"店铺名称过短");
        }
        if(!RexExUtil.isRulePassword(enterprise.getEntpPassword())){
            return new ReturnMsg("40209",true,"密码不合法");
        }
        if(enterpriseService.isThatExist("entp_account", enterprise.getEntpAccount())){
            return new ReturnMsg("40301",true,"该邮箱已被注册");
        }
        if(enterpriseService.isThatExist("entp_charge_phone", enterprise.getEntpChargePhone())){
            return new ReturnMsg("40302",true,"该电话号码已被注册");
        }
        if(enterpriseService.isThatExist("entp_name", enterprise.getEntpName())){
            return new ReturnMsg("40303",true,"该企业名已被注册");
        }
        if(!enterpriseService.userCheckVerification(enterprise.getEntpChargePhone(),
                enterprise.getEntpVerification())){
            return new ReturnMsg("40304",true,"验证码错误");
        }
        try{
            enterprise.setEntpRegisterTime(MyUtil.getNowDateTime());
            enterprise.setEntpAccountMoney("0");
            enterprise.setEntpPassword(MD5util.code(enterprise.getEntpPassword()));
            enterpriseService.getMapper().insert(enterprise);
            return new ReturnMsg("20001",false,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("50001",true,"数据插入失败");
        }
    }

    @RequestMapping("/toLogin")
    public ReturnMsg toLogin(@RequestBody LoginMsg loginMsg){
        if(MyUtil.strHasVoid(loginMsg.getLoginAccount(),
                loginMsg.getLoginPassword())){
            return new ReturnMsg("40101",true,"不接受，参数不齐");
        }
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(loginMsg.getLoginAccount(), loginMsg.getLoginPassword());
        try {
            subject.login(token);
            if(SystemConstant.PLATFORM_USERNAME.equals(loginMsg.getLoginAccount())){
                return new ReturnMsg("20001",false,"平台管理员登录成功",subject.getSession().getId());
            }else {
                return new ReturnMsg("20002",false,"企业管理员登录成功",subject.getSession().getId());
            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return new ReturnMsg("40301",true,"用户名或密码错误");
        }
    }


    @RequestMapping("/login")
    public ReturnMsg login(){
        return new ReturnMsg("30101",true,"尚未登录");
    }

    @RequestMapping("/unauthorized")
    public ReturnMsg unauthorized(){
        return new ReturnMsg("30201",true,"尚未权限");
    }
}
