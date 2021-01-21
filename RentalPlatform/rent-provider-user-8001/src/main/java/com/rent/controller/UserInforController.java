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
        try{
            if (user.getUserName()==null&&user.getUserPhone()==null){
                return new ReturnMsg("101",true,"需要修改信息为空");
            }
            if (user.getUserName().contains(" ")){
                return new ReturnMsg("101",true,"用户名中不能包含空格！");
            }
            if (loginAndRegisterService.userCount(user)>1){
                return new ReturnMsg("304",true);
            }
            if (!loginAndRegisterService.userCheckVerification(user.getUserPhone(),user.getUserVerification())){
                return new ReturnMsg("306",true);
            }
            boolean b = loginAndRegisterService.userUpdateInfro(user);
            if (!b){
                return new ReturnMsg("500",true,"后端逻辑错误或数据库错误");
            }
            else {
                return new ReturnMsg("0",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
