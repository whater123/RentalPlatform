package com.rent.thread;

import com.rent.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author w
 */
public class SMSThread implements Runnable {
    private String userPhone;
    private String randomCode;
    private RedisTemplate<String,Object> redisTemplate;

    public SMSThread(String userPhone, String randomCode, RedisTemplate<String, Object> redisTemplate) {
        this.userPhone = userPhone;
        this.randomCode = randomCode;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
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
            //随机验证码，有效时间10min
            opsForValue.set(userPhone,randomCode,10L, TimeUnit.MINUTES);

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
