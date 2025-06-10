package com.ustack.global.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: InterceptorConfigProperties
 * @Date: 2023-12-21 09:42
 */
@ConfigurationProperties(prefix = "platform.interceptor.configs")
@Component
@Data
public class InterceptorConfigProperties {


    /**
     * 不需要登录的接口
     */
    private List<String> excludeLoginUrls;

    /**
     * 不需要进行性能统计的接口
     */
    private List<String> excludeStatisticUrls;

    /**
     * 是否记录请求日志
     */
    private Boolean reqLog;
}
