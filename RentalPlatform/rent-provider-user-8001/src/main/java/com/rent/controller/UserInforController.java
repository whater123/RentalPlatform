package com.rent.controller;

import com.rent.pojo.base.User;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
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
public class UserInforController {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;

    @PostMapping("/changeInformation")
    public ReturnMsg changeInformation(@RequestBody User user){
        if (user.getUserName()==null&&user.getUserPhone()==null){
            return new ReturnMsg("101",true,"需要修改信息为空");
        }
        if (user.getUserName().contains(" ")){
            return new ReturnMsg("101",true,"用户名中不能包含空格！");
        }
        if (loginAndRegisterService.userIsRepeat(user)){
            return new ReturnMsg("304",true);
        }
        if (!loginAndRegisterService.userCheckVerification(user.getUserPhone(),user.getUserVerification())){
            return new ReturnMsg("306",true);
        }
        return null;
    }
}
