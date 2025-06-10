package com.ustack.phrases.dto;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 常用语表 DTO
 */
@Data
public class PhrasesDTO {

    /**
     * 自增 ID
     */
    @NotNull(groups = {ValidGroup.Update.class}, message = "常用语id不能为空")
    private Integer id;

    /**
     * 常用语内容
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "常用语内容不能为空")
    private String content;

    /**
     * 分类 ID
     */
    private Integer categoryId;

    /**
     * 状态（0-待审核，1-启用，2-禁用）
     */
    private Integer status;

    /**
     * 权重（用于排序）
     */
    private Integer weight;

    /**
     * 次序
     */
    private Integer orderBy;

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