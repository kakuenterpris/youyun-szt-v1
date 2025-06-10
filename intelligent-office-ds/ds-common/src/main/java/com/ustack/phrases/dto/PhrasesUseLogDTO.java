package com.ustack.phrases.dto;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * 常用语使用记录表 DTO
 */
@Data
public class PhrasesUseLogDTO {

    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * 常用语 ID
     */
    private Integer phraseId;

    /**
     * 调用来源（如 "API", "Web", "App"）
     */
    private String source;

    /**
     * 使用时间
     */
    private Date useTime;

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