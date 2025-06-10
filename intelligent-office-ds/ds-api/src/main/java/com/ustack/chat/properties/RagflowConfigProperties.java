package com.ustack.chat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhoufei
 * @date 2025年02月19日
 */
@ConfigurationProperties(prefix = "ragflow")
@Component
@Data
public class RagflowConfigProperties {

    private String commonUrl;

}
