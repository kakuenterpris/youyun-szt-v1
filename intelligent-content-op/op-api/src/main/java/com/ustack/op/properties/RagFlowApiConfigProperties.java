package com.ustack.op.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@ConfigurationProperties(prefix = "ragflow")
@Component
@Data
public class RagFlowApiConfigProperties {


    private String loginUrl;


    private String ragflowUrl;

    private String apiHost;

    /**
     * 上传url
     */
    private String uploadUrl;

    /**
     * 解析url
     */
    private String parseUrl;

    /**
     * 切片url
     */
    private String chunksUrl;

    private String chunksStatusUrl;

    private String deleteUrl;

    private String apiKey;

    private String datasetId;
}
