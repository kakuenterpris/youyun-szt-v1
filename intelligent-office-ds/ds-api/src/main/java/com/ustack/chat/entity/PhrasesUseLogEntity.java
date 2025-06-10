package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 常用语使用记录表
 * @TableName bus_common_phrases_use_log
 */
@TableName(value ="bus_common_phrases_use_log")
@Data
public class PhrasesUseLogEntity extends BaseEntity {

    /**
     * 常用语 ID
     */
    @TableField(value = "phrase_id")
    private Integer phraseId;

    /**
     * 调用来源（如 "API", "Web", "App"）
     */
    @TableField(value = "source")
    private String source;

    /**
     * 使用时间
     */
    @TableField(value = "use_time")
    private Date useTime;
}