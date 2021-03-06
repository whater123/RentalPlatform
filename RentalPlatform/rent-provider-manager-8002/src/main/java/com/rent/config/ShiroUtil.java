package com.rent.config;

import com.rent.pojo.base.Enterprise;
import com.rent.service.EnterpriseService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @author obuivy
 */
@Component
public class ShiroUtil{
    public static boolean isAuthenticed(){
        return SecurityUtils.getSubject().isAuthenticated();
    }
    public static boolean hasRoles(String...roles){
        for (String role :
                roles) {
            if(!SecurityUtils.getSubject().hasRole(role)){
                return false;
            }
        }
        return true;
    }
}