package com.ustack.resource.dto;

import lombok.Data;

/**
 * 文件向量化配置枚举表
 * @TableName file_embedding_config
 */
@Data
public class FileEmbeddingConfigDTO {

    private Long id;

    /**
     *
     */
    private String guid;

    /**
     * 配置编码
     */
    private String configCode;

    /**
     * 配置
     */
    private String configName;

    /**
     * 分段标识符
     */
    private String separatorString;

    /**
     * 最大长度 (token)
     */
    private Integer maxTokens;

    /**
     * 分段重叠 (token)
     */
    private Integer chunkOverlap;

    /**
     * 预处理规则:替换连续空格、换行符、制表符
     */
    private Integer removeExtraSpaces;

    /**
     * 预处理规则:删除 URL、电子邮件地址
     */
    private Integer removeUrlsEmails;
}