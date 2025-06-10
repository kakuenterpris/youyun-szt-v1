package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 知识库文件向量化配置关联表
 * @author allm
 * @TableName bus_resource_embedding
 */
@TableName(value ="bus_resource_embedding")
@Data
public class BusResourceEmbeddingEntity extends BaseEntity {

    /**
     * 资源表id
     */
    @TableField(value = "resource_id")
    private Integer resourceId;

    /**
     * bus_resource_folder表 或 bus_resource_file表 的guid
     */
    @TableField(value = "resource_guid")
    private String resourceGuid;

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