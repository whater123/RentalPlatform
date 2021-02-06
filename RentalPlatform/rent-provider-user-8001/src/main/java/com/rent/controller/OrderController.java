package com.rent.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.rent.pojo.base.Order;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ReturnMsg putOriginOrder(@RequestBody Order order){
        try{
            boolean b = loginAndRegisterService.userExtendToken(order.getUserId());
            if (!b){
                return new ReturnMsg("301",true);
            }

            boolean b1 = orderService.insertOriginOrder(order);
            if (!b1){
                return new ReturnMsg("1",true,"该商品不可租");
            }else {
                return new ReturnMsg("0",false,(Object) order.getOrderId());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
