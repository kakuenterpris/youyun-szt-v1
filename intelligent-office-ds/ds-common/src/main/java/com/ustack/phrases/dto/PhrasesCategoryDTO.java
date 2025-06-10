package com.ustack.phrases.dto;

import lombok.Data;

import java.util.Date;

/**
 * 常用语分类表
 */
@Data
public class PhrasesCategoryDTO {
    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * 分类名称（如 "问候语", "客服回复", "敏感词"）
     */
    private String name;

    /**
     * 分类描述
     */
    private String comment;

    /**
     * 分类描述富文本
     */
    private String commentRich;

    /**
     * 次序
     */
    private Integer orderBy;

    /**
     * 父级分类 ID
     */
    private Integer parentId;

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