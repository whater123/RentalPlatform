package com.rent.controller;

import com.rent.pojo.redisPojo.ClickMsg;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.Impl.RabbitmqSenderService;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author w
 */
@RestController
@RequestMapping("/user")
public class RabbitmqController {
    @Autowired
    RabbitmqSenderService rabbitmqSenderService;

    @PutMapping("/click")
    public ReturnMsg click(@RequestBody ClickMsg clickMsg){
        try{
            clickMsg.setTime(MyUtil.getNowTime());
            rabbitmqSenderService.send(clickMsg);
            //只发一次消息会被吞
//            rabbitmqSenderService.send(clickMsg);
            return new ReturnMsg("0",false);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
