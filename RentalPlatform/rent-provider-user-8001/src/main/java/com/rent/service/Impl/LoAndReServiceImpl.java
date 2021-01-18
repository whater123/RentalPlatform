package com.rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.UserMapper;
import com.rent.pojo.base.User;
import com.rent.service.LoginAndRegisterService;
import com.rent.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author w
 */
@Service
public class LoAndReServiceImpl implements LoginAndRegisterService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean userIsRepeat(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("user_name",userName);
        User user = userMapper.selectOne(queryWrapper);
        return user!=null;
    }

    @Override
    public boolean userSendVerification(String userPhone,String randomCode) {
        String host = "https://smssend.shumaidata.com";
        String path = "/sms/send";
        String method = "POST";
        String appcode = "75597b63850c493fa533ef6515cdce5f";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<>();
        querys.put("receive", userPhone);
        querys.put("tag", randomCode);
        querys.put("templateId", "M09DD535F4");
        Map<String, String> bodys = new HashMap<>();

        try {
            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
            //随机验证码，有效时间5min
            opsForValue.set(userPhone,randomCode,5L, TimeUnit.MINUTES);

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            return jsonObject.getObject("success", boolean.class);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
