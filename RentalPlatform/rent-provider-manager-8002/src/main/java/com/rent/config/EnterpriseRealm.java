package com.rent.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.constant.SystemConstant;
import com.rent.pojo.base.Enterprise;
import com.rent.pojo.base.EnterpriseAuthentication;
import com.rent.service.EnterpriseAuthenticationService;
import com.rent.service.EnterpriseService;
import com.rent.util.MD5util;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * @author obuivy
 */

public class EnterpriseRealm extends AuthorizingRealm {
    @Autowired
    EnterpriseAuthenticationService enterpriseAuthenticationService;
    @Autowired
    EnterpriseService enterpriseService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("shiro授权");
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String username = (String) principalCollection.getPrimaryPrincipal();
        if(SystemConstant.PLATFORM_USERNAME.equals(username)){
            Set<String> permissions = new HashSet<String>();
            Set<String> roles = new HashSet<String>();
            roles.add("platform_manager");
            info.addRoles(roles);
            info.addStringPermissions(permissions);
        }else {
            String entpId = String.valueOf(enterpriseService.getThoseEnterprises("entp_account",username).get(0).getEntpId());
            try{
                QueryWrapper<EnterpriseAuthentication> queryWrapper = new QueryWrapper<EnterpriseAuthentication>();
                queryWrapper.eq("entp_id", entpId)
                        .eq("auth_state", 1);
                if(enterpriseAuthenticationService.getMapper().selectList(queryWrapper).size() >= 1){
                    Set<String> permissions = new HashSet<String>();
                    Set<String> roles = new HashSet<String>();
                    roles.add("enterprise_manager");
                    roles.add("authEnterprise_manager");
                    info.addRoles(roles);
                    info.addStringPermissions(permissions);
                }else{
                    Set<String> permissions = new HashSet<String>();
                    Set<String> roles = new HashSet<String>();
                    roles.add("enterprise_manager");
                    info.addRoles(roles);
                    info.addStringPermissions(permissions);
                }
            } catch (Exception e) {
                Set<String> permissions = new HashSet<String>();
                Set<String> roles = new HashSet<String>();
                roles.add("enterprise_manager");
                info.addRoles(roles);
                info.addStringPermissions(permissions);
            }
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("shiro认证");
        String username = (String)token.getPrincipal();
        System.out.println(username);
        String password = new String((char[])token.getCredentials());
        System.out.println(password);
        if(SystemConstant.PLATFORM_USERNAME.equals(username)&&SystemConstant.PLATFORM_PASSWORD.equals(password)){
            return new SimpleAuthenticationInfo(username, password, getName());
        } else {
            List<Enterprise> list = enterpriseService.getThoseEnterprises("entp_account", username);
            if(list.size() != 1) {
                throw new UnknownAccountException();
            }
            if(!list.get(0).getEntpPassword().equals(MD5util.code(password))) {
                throw new IncorrectCredentialsException();
            }
        }
        //如果身份认证验证成功，返回一个AuthenticationInfo实现；
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
