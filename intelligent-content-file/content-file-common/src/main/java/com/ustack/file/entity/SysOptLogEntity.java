package com.ustack.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 操作日志表
 * @TableName sys_opt_log
 */
@TableName(value ="sys_opt_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysOptLogEntity extends BaseEntity implements Serializable {

    /**
     * 资源id
     */
    @TableField(value = "resource_id")
    private Long resourceId;

    /**
     * 父资源id
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 文件类型（1文件夹 2文件）
     */
    @TableField(value = "file_type")
    private Integer fileType;

    /**
     * 操作类型
     */
    @TableField(value = "operate_type")
    private String operateType;

    /**
     * 操作内容
     */
    @TableField(value = "operate_content")
    private String operateContent;
}