package com.ustack.op.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "thtf.ldap")
public class ThtfLdapProperties {

    /**
     * AD 域认证地址
     */
    private String server;

    /**
     * 域认证地址域名，用于拼接账号
     */
    private String domain;

    /**
     * 启用AD域登录
     */
    private Boolean enable;
}
