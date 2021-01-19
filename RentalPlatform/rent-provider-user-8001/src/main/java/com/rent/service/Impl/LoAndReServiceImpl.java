package com.rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.UserMapper;
import com.rent.pojo.base.User;
import com.rent.pojo.view.LoginMsg;
import com.rent.service.LoginAndRegisterService;
import com.rent.util.HttpUtils;
import com.rent.util.MD5util;
import com.rent.util.VerificationUtil;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    public boolean userIsRepeat(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("user_name",user.getUserName()).or()
                    .eq("user_id_number",user.getUserIdNumber()).or()
                    .eq("user_phone",user.getUserPhone());
        User user1 = userMapper.selectOne(queryWrapper);
        return user1!=null;
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
            //随机验证码，有效时间10min
            opsForValue.set(userPhone,randomCode,10L, TimeUnit.MINUTES);

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

    @Override
    public boolean userCheckVerification(String userPhone, String randomCode) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Object o = opsForValue.get(userPhone);
        if (o==null){
            return false;
        }
        return randomCode.trim().equals(o);
    }

    @Override
    public Map<String, String> userRealAuth(String ID, String userRealName) {
        // 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String host = "https://idcert.market.alicloudapi.com";
        // 【2】后缀
        String path = "/idcard";
        // 【3】开通服务后 买家中心-查看AppCode
        String appcode = "75597b63850c493fa533ef6515cdce5f";
        // 【4】请求参数，详见文档描述
        String idCard = ID;
        // 【4】请求参数，详见文档描述
        String name = userRealName;
        try {
            // 【5】拼接请求链接
            String urlSend = host + path + "?idCard=" + idCard + "&name="+ URLEncoder.encode(name, "UTF-8");
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            //格式Authorization:APPCODE (中间是英文空格)
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                //{"status":"01","msg":"实名认证通过！","idCard":"430124200107026597","name":"王雨","sex":"男","area":"湖南省长沙市宁乡县","province":"湖南省","city":"长沙市","prefecture":"宁乡县","birthday":"2001-07-02","addrCode":"430124","lastCode":"7","traceId":"20210119140740_e634ic_95566a94"}
                System.out.println("正常请求计费(其他均不计费)");
                System.out.println("获取返回的json：");
                System.out.print(json);
                JSONObject jsonObject = JSONObject.parseObject(json);

                String status = jsonObject.getObject("status", String.class);
                String msg = jsonObject.getObject("msg", String.class);
                Map<String,String> map3 = new HashMap<>();
                map3.put("code",status);
                map3.put("msg",msg);
                return map3;
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && "Invalid AppCode `not exists`".equals(error)) {
                    System.out.println("AppCode错误 ");
                } else if (httpCode == 400 && "Invalid Url".equals(error)) {
                    System.out.println("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && "Invalid Param Location".equals(error)) {
                    System.out.println("参数错误");
                } else if (httpCode == 403 && "Unauthorized".equals(error)) {
                    System.out.println("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && "Quota Exhausted".equals(error)) {
                    System.out.println("套餐包次数用完 ");
                } else {
                    System.out.println("参数名错误 或 其他错误");
                    System.out.println(error);
                }
                Map<String,String> map1 = new HashMap<>();
                map1.put("code","500");
                map1.put("msg","认证服务错误");
                return map1;
            }
        } catch (MalformedURLException e) {
            Map<String,String> map2 = new HashMap<>();
            e.printStackTrace();
            System.out.println("URL格式错误");
            map2.put("code","500");
            map2.put("msg","后台异常,URL格式错误");
            return map2;
        } catch (UnknownHostException e) {
            Map<String,String> map2 = new HashMap<>();
            e.printStackTrace();
            System.out.println("URL地址错误");
            map2.put("code","500");
            map2.put("msg","后台异常,URL地址错误");
            return map2;
        } catch (Exception e) {
            Map<String,String> map2 = new HashMap<>();
            e.printStackTrace();
            map2.put("code","500");
            map2.put("msg","后台异常:"+e.getMessage());
            return map2;
        }
    }

    @Override
    public boolean userInsert(User user) {
        user.setUserPassword(MD5util.code(user.getUserPassword()));
        user.setUserRegisterTime(new Date());
        int insert = userMapper.insert(user);
        return insert==1;
    }

    @Override
    public User userLogin(LoginMsg loginMsg) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (VerificationUtil.isInteger(loginMsg.getLoginAccount())){
            queryWrapper.eq("user_phone",loginMsg.getLoginAccount())
                        .eq("user_password",MD5util.code(loginMsg.getLoginPassword()));
        }
        else {
            queryWrapper.eq("user_name",loginMsg.getLoginAccount())
                        .eq("user_password",MD5util.code(loginMsg.getLoginPassword()));
        }
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User returnHandle(User user) {
        user.setUserPassword(null);
        user.setUserIdNumber(null);
        user.setUserRealName(null);
        return user;
    }

    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

}