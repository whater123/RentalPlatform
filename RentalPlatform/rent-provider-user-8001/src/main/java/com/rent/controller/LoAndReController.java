package com.rent.controller;

import com.rent.config.ThreadPoolConfig;
import com.rent.pojo.base.User;
import com.rent.pojo.view.LoginMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.thread.SMSThread;
import com.rent.util.VerificationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * @author w
 */
@RestController
@RequestMapping("/user")
public class LoAndReController {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    ThreadPoolConfig threadPoolConfig;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @PostMapping(path = "/register",produces = "application/json;charset=UTF-8")
    public ReturnMsg register(@RequestBody User user){
        try{
            if (user.getUserName().contains(" ")){
                return new ReturnMsg("101",true,"用户名中不能包含空格！");
            }
            if (loginAndRegisterService.userCount(user)>0){
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
//            boolean b = loginAndRegisterService.userSendVerification(user.getUserPhone(), code);
//            if (b){
//                return new ReturnMsg("0",false);
//            }
//            else {
//                return new ReturnMsg("1",true);
//            }

            //放入子线程,防止请求超时
            threadPoolConfig.threadPoolTaskExecutor().execute(new SMSThread(user.getUserPhone(),code,redisTemplate));
            return new ReturnMsg("0",false);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping(path = "/login",produces = "application/json;charset=UTF-8")
    public ReturnMsg login(@RequestBody LoginMsg loginMsg){
        try{
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
            //使用shiro
//            Subject subject = SecurityUtils.getSubject();
//            // 在认证提交前准备 token（令牌）
//            UsernamePasswordToken token = new UsernamePasswordToken(loginMsg.getLoginAccount(), loginMsg.getLoginPassword());
//            // 执行认证登陆，直接跳到认证方法内，没有异常才执行下面的程序，有异常直接被全局捕捉然后返回json信息了
//            subject.login(token);

        }catch (Exception e){
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping(path = "/changePassword",produces = "application/json;charset=UTF-8")
    public ReturnMsg changePassword(@RequestBody User user){
        try{
            boolean b = loginAndRegisterService.userCount(user)>0;
            if (!b){
                return new ReturnMsg("307",true);
            }
            boolean b1 = loginAndRegisterService.userCheckVerification(user.getUserPhone(), user.getUserVerification());
            if (!b1){
                return new ReturnMsg("306",true);
            }
            boolean b2 = loginAndRegisterService.userUpdatePassword(user);
            if (!b2){
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

    @PostMapping(path = "/loginOut",produces = "application/json;charset=UTF-8")
    public ReturnMsg loginOut(@RequestBody User user){
        try{
            boolean b = loginAndRegisterService.deleteUserToken(user.getUserId());
            if (!b){
                return new ReturnMsg("1",true);
            }
            else {
                return new ReturnMsg("0",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping(path = "/login2",produces = "application/json;charset=UTF-8")
    public ReturnMsg login2(@RequestBody User user){
        try{
            User user1 = loginAndRegisterService.userLoginWithoutPassword(user.getUserToken());
            if (user1==null){
                return new ReturnMsg("1",true);
            }
            else {
                user1.setUserToken(null);
                return new ReturnMsg("0",false,loginAndRegisterService.returnHandle(user1));
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
