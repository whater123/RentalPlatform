package com.rent.controller;

import com.rent.pojo.view.AddressMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.ContactService;
import com.rent.service.LoginAndRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author w
 */
@RequestMapping("/user/contact")
@RestController
public class ContactController {
    @Autowired
    ContactService contactService;
    @Autowired
    LoginAndRegisterService loginAndRegisterService;

    @GetMapping(path = "/getAutoAddress",produces = "application/json;charset=UTF-8")
    public ReturnMsg getAutoAddress(@RequestBody AddressMsg addressMsg, HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            if (addressMsg==null||addressMsg.getLat()==null||addressMsg.getLng()==null){
                return new ReturnMsg("1",true,"参数不齐");
            }
            String s = contactService.userGetAddress(addressMsg.getLat(), addressMsg.getLng());
            if (s==null|| "nullnull".equals(s)){
                return new ReturnMsg("1",true,"地址获取失败");
            }
            else {
                return new ReturnMsg("0",false,s);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
    
}
