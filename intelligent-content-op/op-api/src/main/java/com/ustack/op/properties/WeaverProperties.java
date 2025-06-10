package com.ustack.op.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 泛微相关配置
 *
 * @author qkh
 * @date 2025年05月12日
 */
@ConfigurationProperties(prefix = "integration.weaver")
@Component
@Data
public class WeaverProperties {

    /**
     * ecology系统发放的授权许可证(appid)
     */
    private String appId;

    private String baseUrl;

    /**
     * 获取token
     */
    private String getTokenUrl;

    /**
     * 向OA系统发送许可证信息进行注册认证
     */
    private String registerUrl;

    /**
     * 接口token鉴权
     */
    private String processPermissionsUrl;

    private String routeUrl;

    /**
     * 获取泛微员工信息
     */
    private String userInfoUrl;

    /**
     * 获取泛微岗位信息
     */
    private String jobInfoUrl;

    /**
     * 获取泛微部门信息
     */
    private String depInfoUrl;

    /**
     * 获取泛微分部信息
     */
    private String subCompanyInfoUrl;
}
