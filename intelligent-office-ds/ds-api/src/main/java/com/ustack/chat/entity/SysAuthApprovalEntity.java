package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.chat.dto.UpdateRoleDto;
import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * 系统审批表
 * @TableName SYS_AUTH_APPROVAL
 */
@TableName(value ="SYS_AUTH_APPROVAL")
@Data
public class SysAuthApprovalEntity extends BaseEntity {
    /**
     * 审批角色
     */
    @TableField(value = "ROLE")
    private Integer roleId;

    /**
     * 审批类型（文件夹、菜单）
     */
    @TableField(value = "TYPE")
    private String type;


    @TableField(value = "IS_UPDATE_FOLDER_AUTH")
    private Integer isUpdateFolderAuth;


    @TableField(value = "IS_UPDATE_MENU_AUTH")
    private Integer isUpdateMenuAuth;
    /**
     * 文件夹权限集合
     */
    @TableField(value = "STATUS")
    private String status;

    @TableField(exist = false)
    private List<Integer> folderAuthList;

    @TableField(exist = false)
    private List<Integer> menuAuthList;

}