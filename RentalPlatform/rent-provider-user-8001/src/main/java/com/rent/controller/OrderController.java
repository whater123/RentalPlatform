package com.rent.controller;

import com.rent.pojo.base.Trade;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author w
 */
@RestController
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    LoginAndRegisterService loginAndRegisterService;

    @PutMapping("/putOriginOrder")
    public ReturnMsg putOriginOrder(@RequestBody Trade trade){
        try{
            boolean b = loginAndRegisterService.userExtendToken(trade.getUserId());
            if (!b){
                return new ReturnMsg("301",true);
            }

            Map<String, String> map = orderService.insertOriginOrder(trade);
            if (!"0".equals(map.get("code"))){
                return new ReturnMsg("1",true, map.get("msg"));
            }else {
                return new ReturnMsg("0",false,(Object) trade.getOrderId());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
