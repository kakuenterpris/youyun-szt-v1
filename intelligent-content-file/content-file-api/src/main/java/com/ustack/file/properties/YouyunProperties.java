package com.ustack.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: YouyunProperties
 * @Date: 2025-02-20 16:43
 */
@ConfigurationProperties(prefix = "youyun.doc")
@Component
@Data
public class YouyunProperties {

    /**
     * api key
     */
    private String apikey;

    /**
     * 文档创建接口地址
     */
    private String create;

    /**
     * 删除文档接口
     */
    private String delete;

}
