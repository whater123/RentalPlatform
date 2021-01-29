package com.rent.service;

import com.rent.pojo.base.User;

/**
 * @author w
 */
public interface UserImformationService {
    /**
     * 根据id修改用户个人信息
     * @param user 含id的用户，手机或用户名只要不为空的都会被更新
     * @return  是否更新成功
     */
    boolean userUpdateInfro(User user);

    /**
     * 绑定pictureId与user
     * @param picureId 头像id
     * @param userId 用户id
     * @return 是否成功
     */
    boolean userUpdatePhoto(String picureId,int userId);
}
