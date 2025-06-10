package com.ustack.resource.dto;

import lombok.Data;

import java.util.Date;

/**
 * 知识库文件向量化配置关联表
 * @TableName bus_resource_embedding
 */
@Data
public class BusResourceEmbeddingDTO {
    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 资源表id
     */
    private Integer resourceId;

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

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人 ID
     */
    private String createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新人 ID
     */
    private String updateUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;
}
