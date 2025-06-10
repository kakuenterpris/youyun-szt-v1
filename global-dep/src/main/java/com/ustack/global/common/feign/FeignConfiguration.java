package com.ustack.global.common.feign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: feign client 配置
 * @author：linxin
 * @ClassName: FeignConfiguration
 * @Date: 2024-02-23 16:30
 */
@Configuration
public class FeignConfiguration {

    @Value("${spring.application.name}")
    private String appName;
    /**
     * feign 请求头透传token
     * @author linxin
     * @return FeignRequestInterceptor
     * @date 2024/2/23 16:34
     */
    @Bean
    public FeignRequestInterceptor feignRequestInterceptor(){
        return new FeignRequestInterceptor(appName);
    }
}
