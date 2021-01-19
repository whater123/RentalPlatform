package com.rent.service;

import com.rent.pojo.base.User;
import com.rent.pojo.view.LoginMsg;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author w
 */
public interface LoginAndRegisterService {
    /**
     * 判断用户名,身份证号码,手机号是否重复
     * @param user 注册用户
     * @return 是否重复
     */
    boolean userIsRepeat(User user);

    /**
     * 用户短信验证
     * @param userPhone 用户手机号
     * @param randomCode 随机生成的验证码
     * @return 是否发送成功
     */
    boolean userSendVerification(String userPhone,String randomCode);

    /**
     * 验证用户的验证码
     * @param userPhone 用户手机号
     * @param randomCode 用户输入的验证码
     * @return 是否正确
     */
    boolean userCheckVerification(String userPhone,String randomCode);

    /**
     * 用户实名认证
     * @param ID 身份证号码
     * @param userRealName 真实姓名
     * @return 认证返回信息
     */
    Map<String,String> userRealAuth(String ID, String userRealName);

    /**
     * 插入用户
     * @param user 注册用户
     * @return 是否成功
     */
    boolean userInsert(User user);

    /**
     * 用户登录
     * @param loginMsg 登录输入
     * @return 是否正确
     */
    User userLogin(LoginMsg loginMsg);

    /**
     * 对返回的数据敏处理
     * @param user 返回数据
     * @return 脱敏后数据
     */
    User returnHandle(User user);
}