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
     * 返回用户名,身份证号码,手机号相同的用户有几个
     * @param user 判断用户的用户名,身份证号码,手机号信息
     * @return 相符合个数
     */
    int userCount(User user);

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
     * 用户登录，并且把token存在redis中，：U+id，uuid，7D
     * @param loginMsg 登录输入
     * @return 登录用户信息，登录失败则返回null
     */
    User userLogin(LoginMsg loginMsg);

    /**
     * 对返回的数据脱敏处理
     * @param user 返回数据
     * @return 脱敏后数据
     */
    User returnHandle(User user);

    /**
     * 更新用户密码
     * @param user 用户手机号和修改后的密码
     * @return 是否成功
     */
    boolean userUpdatePassword(User user);

    /**
     * 根据id或手机号或身份证号获取到用户信息
     * @param user 用户不完整的信息
     * @return 完整的用户信息,未找到则返回null
     */
    User getUser(User user);

    /**
     * 根据id修改用户个人信息
     * @param user 含id的用户，手机或用户名只要不为空的都会被更新
     * @return  是否更新成功
     */
    boolean userUpdateInfro(User user);

    /**
     * 延长用户的令牌有效时间，在每次请求发送都要执行此方法
     * @param userId 用户id
     * @return 未登录返回false
     */
    boolean userExtendToken(int userId);

    /**
     * 用户免密登录
     * @param uuid 生成的认证秘钥
     * @return 用户信息
     */
    User userLoginWithoutPassword(String uuid);

    /**
     * 删除用户token
     * @param userId userId
     * @return 是否成功
     */
    boolean deleteUserToken(int userId);
}
