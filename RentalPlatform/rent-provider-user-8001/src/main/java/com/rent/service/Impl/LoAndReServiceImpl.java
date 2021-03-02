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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
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
    public int userCount(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("user_name",user.getUserName()).or()
                    .eq("user_id_number",user.getUserIdNumber()).or()
                    .eq("user_phone",user.getUserPhone());
        List<User> user1 = userMapper.selectList(queryWrapper);
        return user1.size();
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
        boolean equals = randomCode.trim().equals(o);
        if (equals){
            redisTemplate.delete(userPhone);
            return true;
        }
        else {
            return false;
        }
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
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
        user.setUserRegisterTime(new Date());
        //随机获取信誉分
        user.setUserCreditScore(getRandomScore());
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
        User user = userMapper.selectOne(queryWrapper);
        if (user!=null){
            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
            String uuid = String.valueOf(UUID.randomUUID());
            opsForValue.set("U"+ user.getUserId(),uuid,7L,TimeUnit.DAYS);
            user.setUserToken(uuid);
        }
        return user;
    }

    @Override
    public User returnHandle(User user) {
        user.setUserPassword(null);
        user.setUserIdNumber(null);
        user.setUserRealName(null);
        return user;
    }

    @Override
    public boolean userUpdatePassword(User user) {
        User user1 = getUser(user);
        if (user1==null){
            return false;
        }
        user1.setUserPassword(MD5util.code(user.getUserPassword()));
        int i = userMapper.updateById(user1);
        return i==1;
    }

    @Override
    public User getUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user.getUserId()!=0){
            queryWrapper.eq("user_id",user.getUserId());
            return userMapper.selectOne(queryWrapper);
        }
        if (user.getUserPhone()!=null){
            queryWrapper.eq("user_phone",user.getUserPhone());
            return userMapper.selectOne(queryWrapper);
        }
        if (user.getUserIdNumber()!=null){
            queryWrapper.eq("user_id_number",user.getUserIdNumber());
            return userMapper.selectOne(queryWrapper);
        }
        return null;
    }

    @Override
    public boolean userExtendToken(int userId) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Object o = opsForValue.get("U" + userId);
        if (o==null){
            return false;
        }
        else {
            opsForValue.set("U" + userId,String.valueOf(o),7L,TimeUnit.DAYS);
            return true;
        }
    }

    @Override
    public User userLoginWithoutPassword(String uuid) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Set<String> keys = redisTemplate.keys("*");
        System.out.println(keys);
        if (keys==null){
            return null;
        }
        for (String key : keys) {
            // 获取key对应值
            if (!key.startsWith("U")){
                continue;
            }
            String value = String.valueOf(opsForValue.get(key));
            if (value.equals(uuid)){
                int substring = Integer.parseInt(key.substring(1));
                userExtendToken(substring);
                return getUser(new User(substring));
            }
        }
        return null;
    }

    @Override
    public boolean deleteUserToken(int userId) {
        if (!redisTemplate.hasKey("U"+userId)){
            return false;
        }
        else {
            return redisTemplate.delete("U" + userId);
        }
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

    private int getRandomScore(){
        Random n = new Random();
        int i = n.nextInt(151)+550;
        return (int) ((double)i/850*200+50);
    }
}
