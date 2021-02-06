package com.rent.controller;

import com.rent.pojo.base.Order;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author w
 */
@RestController
@RequestMapping("/user/pay")
public class PayController {
    @Autowired
    PayService payService;
    @Autowired
    LoginAndRegisterService loginAndRegisterService;

    @GetMapping("/getPayInformation")
    public ReturnMsg getPayInformation(@RequestBody Order order) throws Exception {
        try{
            boolean b = loginAndRegisterService.userExtendToken(order.getUserId());
            if (!b){
                return new ReturnMsg("301",true);
            }

            PayNeedMsg payNeedMsg = payService.getPayNeedMsg(order);
            if (payNeedMsg.getErrorCode()!=null){
                return new ReturnMsg("1",true,payNeedMsg.getErrorCode());
            }else {
                return new ReturnMsg("0",false,payNeedMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
