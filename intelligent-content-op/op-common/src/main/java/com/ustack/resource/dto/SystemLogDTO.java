package com.ustack.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * @author Liyingzheng
 * @data 2025/4/23 10:07
 * @describe
 */
@Data
public class SystemLogDTO {
    /**
     * 操作日志表主键id
     */
    private Long id;
    /**
     * 资源id
     */
    private Long resourceId;

    /**
     * 父资源id
     */
    private Long parentId;

    /**
     * 文件类型（1文件夹 2文件）
     */
    private Integer fileType;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作内容
     */
    private String operateContent;

    /**
     *
     */
    private String guid;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人ID
     */
    private String createUserId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改人ID
     */
    private String updateUserId;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 乐观锁版本号
     */
    private Long version;

    /**
     * 逻辑删除字段
     */
    private Boolean deleted;

    /**
     * 页码
     */
    @NotBlank(message = "页码start不能为空")
    private Integer start;

    /**
     * 页大小
     */
    @NotBlank(message = "页大小size不能为空")
    private Integer size;
}
