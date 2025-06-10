package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件夹权限表
 * @TableName FOLDER_AUTH
 */
@TableName(value ="FOLDER_AUTH")
@Data
public class FolderAuthEntity implements Serializable {
    /**
     *
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 文件夹id
     */
    @TableField(value = "FOLDER_ID")
    private Integer folderId;

    /**
     * 用户id
     */
    @TableField(value = "ROLE_ID")
    private Integer roleId;

    /**
     * 权限类型
     */
    @TableField(value = "AUTH_TYPE")
    private Integer authType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}