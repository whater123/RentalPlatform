package com.rent.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    private final String LOGINURL = "";
    private final String UNAUTHORIZEDURL = "";


    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(securityManager);
        /*添加shiro内置过滤器
            anon：无需认证
            authc：必须认证
            user：必须拥有 记住我
            perms：拥有对某个资源的权限才能访问
            role：拥有某个角色权限才能访问
        * */
        Map<String, String> filterMap = new LinkedHashMap<String, String>();

        bean.setFilterChainDefinitionMap(filterMap);
        bean.setLoginUrl(LOGINURL);
        bean.setUnauthorizedUrl(UNAUTHORIZEDURL);

        return bean;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("enterpriseRealm") EnterpriseRealm enterpriseRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联EnterpriseRealm
        securityManager.setRealm(enterpriseRealm);
        return securityManager;
    }

    @Bean(name = "enterpriseRealm")
    public EnterpriseRealm enterpriseRealm() {
        return new EnterpriseRealm();
    }

}
