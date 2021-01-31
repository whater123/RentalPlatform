package com.rent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author MSI-PC
 */
@RestController
public class MyController {
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @RequestMapping("/")
    public String rsndm(){
        Map<String,String> map = new TreeMap<String, String>();
        map.put("颜色","白色");
        map.put("尺码","s");
        map.put("租赁预期","先租后买");
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);

        return String.valueOf(addGoodsEntityAttribute(1,map,list));
    }

    public boolean addGoodsEntityAttribute(int goodsId, Map<String, String> map, List<Integer> list) {
        //筛选map中添加商品集id
        map.put("goodsId", String.valueOf(goodsId));

        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        if (opsForHash.hasKey("go_en_cl",String.valueOf(map))){
            return false;
        } else {
            opsForHash.put("go_en_cl",String.valueOf(map),list);
            return true;
        }
    }
}
