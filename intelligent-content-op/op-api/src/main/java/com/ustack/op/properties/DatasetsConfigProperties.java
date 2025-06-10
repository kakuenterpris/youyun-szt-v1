package com.ustack.op.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2025年02月19日
 */
@ConfigurationProperties(prefix = "datasets")
@Component
@Data
public class DatasetsConfigProperties {

    private String unitId;

    private String depId;
}
