package com.ustack.op.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2025年02月19日
 */
@ConfigurationProperties(prefix = "integration.ppt")
@Component
@Data
public class AiPPTProperties {

    private String apiKey;

    private String secretKey;

    private String tokenStringToSign;

    private String tokenApi;

    private String codeStringToSign;

    private String codeApi;
}
