package com.rent.controller;

import com.rent.pojo.base.Picture;
import com.rent.pojo.base.user.User;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.UserImformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author w
 */
@RestController
@RequestMapping("/user")
public class UserInforController {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    UserImformationService userImformationService;
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
            boolean b = userImformationService.userUpdateInfro(user);
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

    @PostMapping(path = "/changePhoto",produces = "application/json;charset=UTF-8")
    public ReturnMsg changePhoto(HttpServletRequest request, @RequestBody Picture picture){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            boolean b1 = userImformationService.userUpdatePhoto(picture.getPictureId(), Integer.parseInt(userId));
            if (!b1){
                return new ReturnMsg("1",true,"id参数错误");
            }else {
                return new ReturnMsg("0",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
