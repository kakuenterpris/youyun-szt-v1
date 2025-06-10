package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 文件向量化配置枚举表
 * @TableName file_embedding_config
 */
@TableName(value ="file_embedding_config")
@Data
public class FileEmbeddingConfigEntity extends BaseEntity {

    /**
     * 配置编码
     */
    @TableField(value = "config_code")
    private String configCode;

    /**
     * 配置
     */
    @TableField(value = "config_name")
    private String configName;

    /**
     * 分段标识符
     */
    @TableField(value = "separator_string")
    private String separatorString;

    /**
     * 最大长度 (token)
     */
    @TableField(value = "max_tokens")
    private Integer maxTokens;

    /**
     * 分段重叠 (token)
     */
    @TableField(value = "chunk_overlap")
    private Integer chunkOverlap;

    /**
     * 预处理规则:替换连续空格、换行符、制表符
     */
    @TableField(value = "remove_extra_spaces")
    private Integer removeExtraSpaces;

    /**
     * 预处理规则:删除 URL、电子邮件地址
     */
    @TableField(value = "remove_urls_emails")
    private Integer removeUrlsEmails;
}