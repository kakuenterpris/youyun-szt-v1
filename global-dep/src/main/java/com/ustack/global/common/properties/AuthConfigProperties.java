package com.ustack.global.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description: token认证配置
 * @author：linxin
 * @ClassName: AuthConfigProperties
 * @Date: 2024-03-28 10:13
 */
@ConfigurationProperties(prefix = "platform.auth.configs")
@Component
@Data
public class AuthConfigProperties {

    /**
     * 认证服务地址
     */
    private String verifyUrl;

    /**
     * 是否启用，false使用默认系统用户
     */
    private Boolean enable;
}
