package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName APPROVAL_DETAIL
 */
@TableName(value ="APPROVAL_DETAIL")
@Data
public class ApprovalDetailEntity implements Serializable {
    /**
     * 权限id
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 角色id
     */
    @TableField(value = "ROLE_ID")
    private Integer roleId;

    /**
     * 审批id
     */
    @TableField(value = "APPROVAL_ID")
    private Long approvalId;

    /**
     * 文件或菜单
     */
    @TableField(value = "FOLDER_OR_MENU")
    private Integer folderOrMenu;

    /**
     * 权限类型
     */
    @TableField(value = "MANAGE_AUTH")
    private Integer authManage;

    /**
     * 目标类型
     */
    @TableField(value = "AUTH_TYPE")
    private String authType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}