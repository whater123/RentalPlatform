package com.rent.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.rent.pojo.base.User;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.util.VerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author w
 */
@RestController
@RequestMapping("/user")
public class LoAndReController {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;

    @PostMapping(path = "/register",produces = "application/json;charset=UTF-8")
    public ReturnMsg userLogin(@RequestBody User user){
        if (user.getUserName().contains(" ")){
            return new ReturnMsg("102",true);
        }
        if (loginAndRegisterService.userIsRepeat(user.getUserName())){
            return new ReturnMsg("304",true);
        }

        return null;
    }

    @PostMapping(path = "/getVerification",produces = "application/json;charset=UTF-8")
    public ReturnMsg getVerification(@RequestBody User user){
        try {
            user.setUserPhone(user.getUserPhone().trim());
            if (user.getUserPhone().length()!=11){
                return new ReturnMsg("1",true);
            }
            VerificationUtil verificationUtil = new VerificationUtil();
            String code = verificationUtil.getCode();
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
}
