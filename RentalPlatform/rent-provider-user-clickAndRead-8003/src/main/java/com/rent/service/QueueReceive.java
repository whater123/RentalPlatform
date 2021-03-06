package com.rent.service;

import com.rent.pojo.redisPojo.ClickMsg;
import com.rent.util.MyUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QueueReceive {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    //监听器监听指定的Queue（1对1）
    @RabbitListener(queues="queue")
    public void processC(ClickMsg clickMsg) {
        String[] s = clickMsg.getTime().split(" ");
        if (clickMsg.getEntpId()!=0){
            String key = s[0]+"E";
            if (redisTemplate.hasKey(key)){
                List<Integer> list = (List<Integer>) redisTemplate.opsForValue().get(key);
                assert list != null;
                list.add(clickMsg.getEntpId());
                redisTemplate.opsForValue().set(key,list);
            }else {
                List<Integer> list = new ArrayList<>();
                list.add(clickMsg.getEntpId());
                redisTemplate.opsForValue().set(key,list);
            }
        }
        if (clickMsg.getGoodsId()!=0){
            String key = s[0]+"G";
            if (redisTemplate.hasKey(key)){
                List<Integer> list = (List<Integer>) redisTemplate.opsForValue().get(key);
                assert list != null;
                list.add(clickMsg.getGoodsId());
                redisTemplate.opsForValue().set(key,list);
            }else {
                List<Integer> list = new ArrayList<>();
                list.add(clickMsg.getGoodsId());
                redisTemplate.opsForValue().set(key,list);
            }
        }
        System.out.println("Receive queue:"+clickMsg);
    }

}