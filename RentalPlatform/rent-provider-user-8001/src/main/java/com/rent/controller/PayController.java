package com.rent.controller;

import com.rent.pojo.base.Trade;
import com.rent.pojo.base.OrderPay;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/getPayInformation")
    public ReturnMsg getPayInformation(@RequestBody Trade trade) throws Exception {
        try{
            boolean b = loginAndRegisterService.userExtendToken(trade.getUserId());
            if (!b){
                return new ReturnMsg("301",true);
            }

            PayNeedMsg payNeedMsg = payService.getPayNeedMsg(trade);
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

    @PutMapping("/putPayInformation")
    public ReturnMsg putPayInformation(@RequestBody OrderPay orderPay) throws Exception {
        try{
            boolean b = loginAndRegisterService.userExtendToken(orderPay.getUserId());
            if (!b){
                return new ReturnMsg("301",true);
            }

            Map<String, String> map = payService.payMsgCheck(orderPay);
            String code = map.get("code");
            String msg = map.get("msg");
            if (!"0".equals(code)){
                return new ReturnMsg(code,true,msg);
            }
            boolean b1 = payService.insertPayAndUpdate(orderPay);
            if (!b1){
                return new ReturnMsg("1",true,"数据插入失败");
            }else {
                if (orderPay.getPayType()==2){
                    boolean b2 = payService.buyGoods(orderPay.getOrderId());
                    if (!b2){
                        return new ReturnMsg("500",true,"数据库错误，请联系管理员处理退款");
                    }
                }
                return new ReturnMsg("0",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getPays/{userId}")
    public ReturnMsg getPays(@PathVariable("userId") String userId){
        try{
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }

            List<OrderPay> userAllPays = payService.getUserAllPays(Integer.parseInt(userId));
            return new ReturnMsg("0",false,userAllPays);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
