package com.rent.service.Impl;

import com.rent.dao.UserMapper;
import com.rent.pojo.base.user.User;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.UserImformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author w
 */
@Service
public class UserImforServiceImpl implements UserImformationService {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    UserMapper userMapper;

    @Override
    public boolean userUpdateInfro(User user) {
        User user1 = loginAndRegisterService.getUser(user);
        if (user.getUserPhone()!=null){
            user1.setUserPhone(user.getUserPhone());
        }
        if (user.getUserName()!=null){
            user1.setUserName(user.getUserName());
        }
        int i = userMapper.updateById(user1);
        return i==1;
    }

    @Override
    public boolean userUpdatePhoto(String picureId, int userId) {
        User user1 = loginAndRegisterService.getUser(new User(userId));
        user1.setUserPictureId(picureId);
        int i = userMapper.updateById(user1);
        return i==1;
    }
}
