package com.rent.controller;

import com.rent.pojo.base.user.Contact;
import com.rent.pojo.view.AddressMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.ContactService;
import com.rent.service.LoginAndRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @GetMapping(path = "/getAutoAddress/{lat}/{lng}",produces = "application/json;charset=UTF-8")
    public ReturnMsg getAutoAddress(@PathVariable("lat") String lat,@PathVariable("lng") String lng, HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            if (lat==null||lng==null){
                return new ReturnMsg("1",true,"参数不齐");
            }
            String s = contactService.userGetAddress(lat, lng);
            if (s==null|| "nullnull".equals(s)){
                return new ReturnMsg("1",true,"地址获取失败");
            }
            else {
                ReturnMsg returnMsg = new ReturnMsg("0", false);
                returnMsg.setData(new AddressMsg(s));
                return returnMsg;
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PutMapping(path = "/",produces = "application/json;charset=UTF-8")
    public ReturnMsg add(@RequestBody Contact contact,HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            contact.setContactReceiveId("U"+userId);
            boolean b1 = contactService.insertContact(contact);
            if (!b1){
                return new ReturnMsg("500",true,"后端逻辑或数据库错误");
            }
            else {
                return new ReturnMsg("0",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping(path = "/getAll",produces = "application/json;charset=UTF-8")
    public ReturnMsg getAll(HttpServletRequest request){
        try {
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            List<Contact> allContact = contactService.getAllContact("U" + userId);
            return new ReturnMsg("0",false,allContact);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @DeleteMapping(path = "/{contactId}",produces = "application/json;charset=UTF-8")
    public ReturnMsg getAll(@PathVariable("contactId") String contactId, HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            boolean b1 = contactService.deleteContact(Integer.parseInt(contactId));
            if (!b1){
                return new ReturnMsg("1",true,"id参数无效");
            }
            else {
                return new ReturnMsg("0",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping(path = "/{contactId}",produces = "application/json;charset=UTF-8")
    public ReturnMsg change(@PathVariable("contactId") String contactId, @RequestBody Contact contact, HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            boolean b = loginAndRegisterService.userExtendToken(Integer.parseInt(userId));
            if (!b){
                return new ReturnMsg("301",true);
            }

            contact.setContactReceiveId("U"+userId);
            contact.setContactId(Integer.parseInt(contactId));
            boolean b1 = contactService.updateContact(contact);
            if (!b1){
                return new ReturnMsg("1",true,"修改失败");
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
