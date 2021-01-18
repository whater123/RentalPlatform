package com.rent.service;

import org.springframework.stereotype.Service;

/**
 * @author w
 */
public interface LoginAndRegisterService {
    /**
     * 判断用户名是否重复
     * @param userName 注册用户名
     * @return 是否重复
     */
    boolean userIsRepeat(String userName);

    /**
     * 用户短信验证
     * @param userPhone 用户手机号
     * @param randomCode 随机生成的验证码
     * @return 是否发送成功
     */
    boolean userSendVerification(String userPhone,String randomCode);
}
