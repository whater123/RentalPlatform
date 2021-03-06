package com.rent.service.Impl;

import com.rent.pojo.redisPojo.ClickMsg;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author w
 */
@Component
public class RabbitmqSenderService {
    @Autowired
    private AmqpTemplate template;
    /**
     * 如果要发送个体需要继承serilizable接口
     */
    public void send(ClickMsg context) {
    template.convertAndSend("queue",context);
    }
}