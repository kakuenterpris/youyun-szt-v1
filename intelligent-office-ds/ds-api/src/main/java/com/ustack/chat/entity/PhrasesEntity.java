package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 常用语表
 * @TableName bus_common_phrases
 */
@TableName(value ="bus_common_phrases")
@Data
public class PhrasesEntity extends BaseEntity {

    /**
     * 常用语内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 分类 ID
     */
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
     * 状态（0-待审核，1-启用，2-禁用）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 权重（用于排序）
     */
    @TableField(value = "weight")
    private Integer weight;

    /**
     * 次序
     */
    @TableField(value = "order_by")
    private Integer orderBy;
}