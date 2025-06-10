package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志表
 * @TableName SYS_OPT_LOG
 */
@TableName(value ="SYS_OPT_LOG")
@Data
public class SysOptLogEntity implements Serializable {
    /**
     * 操作表主键id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 资源id
     */
    @TableField(value = "RESOURCE_ID")
    private Long resourceId;

    /**
     * 父资源id
     */
    @TableField(value = "PARENT_ID")
    private Long parentId;

    /**
     * 文件类型（1文件夹 2文件）
     */
    @TableField(value = "FILE_TYPE")
    private Integer fileType;

    /**
     * 操作类型
     */
    @TableField(value = "OPERATE_TYPE")
    private String operateType;

    /**
     * 操作内容
     */
    @TableField(value = "OPERATE_CONTENT")
    private String operateContent;

    /**
     * 创建人
     */
    @TableField(value = "CREATE_USER")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private Date createTime;

    /**
     * 创建人ID
     */
    @TableField(value = "CREATE_USER_ID")
    private String createUserId;

    /**
     * 修改人
     */
    @TableField(value = "UPDATE_USER")
    private String updateUser;

    /**
     * 修改时间
     */
    @TableField(value = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 修改人ID
     */
    @TableField(value = "UPDATE_USER_ID")
    private String updateUserId;

    /**
     * 乐观锁版本号
     */
    @TableField(value = "VERSION")
    private Integer version;

    /**
     * 逻辑删除字段1 表示删除，0 表示未删除
     */
    @TableField(value = "IS_DELETED")
    private Boolean isDeleted;

    /**
     *
     */
    @TableField(value = "GUID")
    private String guid;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}