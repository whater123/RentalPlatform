package com.rent.realm;

import com.rent.dao.UserMapper;
import com.rent.pojo.base.User;
import com.rent.pojo.view.LoginMsg;
import com.rent.service.LoginAndRegisterService;
import lombok.SneakyThrows;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author w
 */
public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private LoginAndRegisterService loginAndRegisterService;

    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @SneakyThrows
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("————身份认证方法————");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        // 从数据库获取对应用户名密码的用户
        System.out.println(token.getUsername()+" "+ Arrays.toString(token.getPassword()) +" "+token.getCredentials()+" "+token.getPrincipal());
        String password = new String((char[]) token.getCredentials());
        User user = loginAndRegisterService.userLogin(new LoginMsg(token.getUsername(), password));
//        if (null == password) {
//            throw new AccountException("用户名不正确");
//        } else if (!password.equals(new String((char[]) token.getCredentials()))) {
//            throw new AccountException("密码不正确");
//        }
        if (user==null){
            throw new AccountException("302");
        }
        return new SimpleAuthenticationInfo(token.getPrincipal(), password, getName());
    }

    /**
     * 获取授权信息
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("————权限认证————");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //获得该用户角色
        Set<String> set = new HashSet<String>();
        //需要将 role 封装到 Set 作为 info.setRoles() 的参数
        set.add("user");
        //设置该用户拥有的角色
        info.setRoles(set);
        return info;
    }
}
