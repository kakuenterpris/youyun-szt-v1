package com.ustack.chat.config.validation;

import com.ustack.chat.config.http.filters.JwtFilter;
import com.ustack.chat.realm.UsernameRealm;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * shiro配置类
 * @author heyunlin
 * @version 1.0
 */
@Configuration
public class ShiroConfig {

    /**
     * 配置安全管理器
     * @param usernameRealm UserRealm
     * @return DefaultWebSecurityManager
     */
    @Bean
    public DefaultSecurityManager securityManager(UsernameRealm usernameRealm) {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setRealm(usernameRealm);
        ThreadContext.bind(securityManager);
        return securityManager;
    }

    /**
     * 配置Shiro过滤器工厂
     * @param defaultSecurityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultSecurityManager defaultSecurityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 给filter设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(defaultSecurityManager);
        // 默认认证界面路径---当认证不通过时跳转
        shiroFilterFactoryBean.setLoginUrl("/login.jsp");

        // 添加自己的过滤器并且取名为jwt
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("jwt", new JwtFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        // 配置系统受限资源
        Map<String, String> map = new HashMap<String, String>();
        map.put("/index.jsp", "authc");
        map.put("/user/login","anon");
        map.put("api/v1/chat/login","anon");
        map.put("/user/register","anon");
        map.put("/login.jsp","anon");
        map.put("/**", "jwt");
        // 所有请求通过我们自己的过滤器
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);

        return shiroFilterFactoryBean;
    }


}