package com.rent.config;

import org.apache.shiro.SecurityUtils;
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