package com.rent.config;

import com.rent.constant.SystemConstant;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * @author obuivy
 */
@Configuration
public class ShiroConfig {

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
//        filterMap.put("/manager/enterprise/*","roles[enterprise_manager]");
//        filterMap.put("/manager/*","anon");
//        filterMap.put("/**","authc");

        bean.setFilterChainDefinitionMap(filterMap);
        bean.setLoginUrl(SystemConstant.LOGIN_URI);
        bean.setUnauthorizedUrl(SystemConstant.UNAUTHORIZED_URI);

        return bean;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("enterpriseRealm") EnterpriseRealm enterpriseRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联EnterpriseRealm
        securityManager.setSessionManager(sessionManager());
        securityManager.setRealm(enterpriseRealm);
        return securityManager;
    }

    @Bean(name = "enterpriseRealm")
    public EnterpriseRealm enterpriseRealm() {
        return new EnterpriseRealm();
    }

    //自定义sessionManager
    @Bean("sessionManager")
    public DefaultWebSessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        //mySessionManager.setSessionDAO(new RedisSessionDAO());
        return mySessionManager;
    }

    /**
     * 配置shiro redisManager
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        String host = "106.54.174.38";
        String password = "qwaszxc1303";
        int port = 6379;
        int timeout = 100000;
        redisManager.setHost(host);
        redisManager.setTimeout(timeout);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        return redisManager;
    }


    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }
}





