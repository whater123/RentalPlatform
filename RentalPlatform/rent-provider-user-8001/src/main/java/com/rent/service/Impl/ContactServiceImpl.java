package com.rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.ContactMapper;
import com.rent.pojo.base.Contact;
import com.rent.service.ContactService;
import com.rent.util.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author w
 */
@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    ContactMapper contactMapper;

    @Override
    public String userGetAddress(String lat, String lng) {
        String host = "https://jisujwddz.market.alicloudapi.com";
        String path = "/geoconvert/coord2addr";
        String method = "ANY";
        String appcode = "75597b63850c493fa533ef6515cdce5f";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
//        querys.put("lat", "30.2812129803");
//        querys.put("lng", "120.11523398");
        querys.put("lat", lat);
        querys.put("lng", lng);
        querys.put("type", "baidu");
        String bodys = null;
        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            String msg = jsonObject.getObject("msg", String.class);
            if (!"ok".equals(msg)){
                return null;
            }
            Object result = jsonObject.getObject("result", Object.class);
            JSONObject jsonObject1 = JSONObject.parseObject(result.toString());
            String address = jsonObject1.getObject("address", String.class);
            String description = jsonObject1.getObject("description", String.class);
            return address+description;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean insertContact(Contact contact) {
        int insert = contactMapper.insert(contact);
        return insert==1;
    }

    @Override
    public List<Contact> getAllContact(String userIdAddU) {
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_receive_id",userIdAddU);
        List<Contact> contacts = contactMapper.selectList(queryWrapper);
        for (int i = 0; i < contacts.size(); i++) {
            contacts.set(i,returnHandle(contacts.get(i)));
        }
        return contacts;
    }

    @Override
    public boolean deleteContact(int contactId) {
        int delete = contactMapper.deleteById(contactId);
        return delete==1;
    }

    @Override
    public boolean updateContact(Contact contact) {
        int i = contactMapper.updateById(contact);
        return i==1;
    }

    private Contact returnHandle(Contact contact){
        contact.setContactReceiveId(null);
        return contact;
    }
}
