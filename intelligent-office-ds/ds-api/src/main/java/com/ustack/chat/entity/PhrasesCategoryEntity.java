package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 常用语分类表
 * @TableName bus_common_phrases_category
 */
@TableName(value ="bus_common_phrases_category")
@Data
public class PhrasesCategoryEntity extends BaseEntity {
    /**
     * 分类名称（如 "问候语", "客服回复", "敏感词"）
     */
    @TableField(value = "name")
    private String name;

    /**
     * 分类描述
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 分类描述富文本
     */
    @TableField(value = "comment_rich")
    private String commentRich;

    /**
     * 次序
     */
    @TableField(value = "order_by")
    private Integer orderBy;

    /**
     * 父级分类 ID
     */
    @TableField(value = "parent_id")
    private Integer parentId;
}