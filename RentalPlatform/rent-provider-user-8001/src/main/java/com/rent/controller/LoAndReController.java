package com.rent.controller;

import com.rent.pojo.base.User;
import com.rent.pojo.view.LoginMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.util.VerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author w
 */
@RestController
@RequestMapping("/user")
public class LoAndReController {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;

    @PostMapping(path = "/register",produces = "application/json;charset=UTF-8")
    public ReturnMsg register(@RequestBody User user){
        try{
            if (user.getUserName().contains(" ")){
                return new ReturnMsg("101",true,"用户名中不能包含空格！");
            }
            if (loginAndRegisterService.userIsRepeat(user)){
                return new ReturnMsg("304",true);
            }
            if (!loginAndRegisterService.userCheckVerification(user.getUserPhone(),user.getUserVerification())){
                return new ReturnMsg("306",true);
            }
            Map<String, String> map = loginAndRegisterService.userRealAuth(user.getUserIdNumber(), user.getUserRealName());
            String code = map.get("code");
            if (!"01".equals(code)){
                return new ReturnMsg("305",true,map.get("msg"));
            }
            boolean b = loginAndRegisterService.userInsert(user);
            if (!b){
                return new ReturnMsg("500",true,"数据插入错误！");
            }
            return new ReturnMsg("0",false);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping(path = "/getVerification",produces = "application/json;charset=UTF-8")
    public ReturnMsg getVerification(@RequestBody User user){
        try {
            user.setUserPhone(user.getUserPhone().trim());
            if (user.getUserPhone().length()!=11){
                return new ReturnMsg("1",true);
            }
            VerificationUtil verificationUtil = new VerificationUtil();
            String code = verificationUtil.getIntCode();
            boolean b = loginAndRegisterService.userSendVerification(user.getUserPhone(), code);
            if (b){
                return new ReturnMsg("0",false);
            }
            else {
                return new ReturnMsg("1",true);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping(path = "/login",produces = "application/json;charset=UTF-8")
    public ReturnMsg login(@RequestBody LoginMsg loginMsg){
        if (loginMsg.getLoginAccount()==null||loginMsg.getLoginPassword()==null){
            return new ReturnMsg("302",true);
        }
        User user = loginAndRegisterService.userLogin(loginMsg);
        if (user==null){
            return new ReturnMsg("302",true);
        }
        else {
            return new ReturnMsg("0",false,loginAndRegisterService.returnHandle(user));
        }
    }
}
