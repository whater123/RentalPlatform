package com.rent.controller;

import com.rent.pojo.base.User;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author w
 */
@RestController
@RequestMapping("/user")
public class UserInforController {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
//    @Autowired
//    RestTemplate restTemplate;

    @PostMapping("/changeInformation")
    public ReturnMsg changeInformation(@RequestBody User user){
        try{
            if (!loginAndRegisterService.userExtendToken(user.getUserId())){
                return new ReturnMsg("301",true);
            }
            if (user.getUserName()==null&&user.getUserPhone()==null){
                return new ReturnMsg("101",true,"需要修改信息为空");
            }
            if (user.getUserName()!=null&&user.getUserName().contains(" ")){
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

//    @PostMapping(path = "/changePhoto",produces = "application/json;charset=UTF-8")
//    public ReturnMsg changePhoto(MultipartFile userPhoto, int userId){
//        System.out.println(userPhoto);
//        System.out.println(userId);
//        ReturnMsg returnMsg = restTemplate.postForObject("http://106.54.174.38:8001/user/utils/uploadPicture", userPhoto, ReturnMsg.class);
//        System.out.println(returnMsg);
//        assert returnMsg != null;
//        if ("200".equals(returnMsg.getCode())){
//            //存入picturename
//            return new ReturnMsg("0",false,returnMsg.getData());
//        }
//        else {
//            return returnMsg;
//        }
//    }
}
