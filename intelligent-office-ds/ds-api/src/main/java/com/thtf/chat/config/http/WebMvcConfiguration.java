package com.thtf.chat.config.http;

import cn.hutool.core.collection.CollUtil;
import com.thtf.chat.config.http.filters.HttpServletRequestReplaceFilter;
import com.thtf.chat.config.http.interceptors.HttpStaticInterceptor;
import com.thtf.chat.config.http.interceptors.LoginVerifyInterceptor;
import com.thtf.chat.utils.RedisUtil;
import com.thtf.global.common.properties.AuthConfigProperties;
import com.thtf.global.common.properties.InterceptorConfigProperties;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

/**
 * @Description: mvc 配置，拦截器过滤器，跨域等
 * @author：linxin
 * @ClassName: WebMvcConfiguration
 * @Date: 2025-02-17 10:12
 */
@Configuration
@EnableConfigurationProperties(value = {InterceptorConfigProperties.class, AuthConfigProperties.class})
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final InterceptorConfigProperties configProperties;
    private final AuthConfigProperties authConfigProperties;
    private final RedisUtil redisUtil;

    @Value("${spring.profiles.active}")
    private String env;

//    @Value("${spring.application.name}")
//    private String appName;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration loginRegistry = registry.addInterceptor(loginVerifyInterceptor());
        loginRegistry.addPathPatterns("/**");
        if (CollUtil.isNotEmpty(configProperties.getExcludeLoginUrls())){
            loginRegistry.excludePathPatterns(configProperties.getExcludeLoginUrls());
        }
        InterceptorRegistration statisticsRegistry = registry.addInterceptor(httpStaticInterceptor());
        statisticsRegistry.addPathPatterns("/**");
        if (CollUtil.isNotEmpty(configProperties.getExcludeStatisticUrls())){
            statisticsRegistry.excludePathPatterns(configProperties.getExcludeStatisticUrls());
        }

    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpServletRequestReplaceFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public LoginVerifyInterceptor loginVerifyInterceptor(){
        return new LoginVerifyInterceptor(authConfigProperties, redisUtil);
    }

    @Bean
    public HttpStaticInterceptor httpStaticInterceptor(){
        return new HttpStaticInterceptor(env, DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss"));
    }

//    @Bean
//    public ThreadPoolExecutorShutdownListener executorShutdownListener(){
//        return new ThreadPoolExecutorShutdownListener();
//    }


}
