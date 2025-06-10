package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 文件夹成员权限表
 * @author allm
 * @TableName bus_resource_member
 */
@TableName(value ="bus_resource_member")
@Data
public class BusResourceMemberEntity extends BaseEntity {

    /**
     * 文件夹ID
     */
    @TableField(value = "folder_id")
    private Integer folderId;

    /**
     * 成员ID
     */
    @TableField(value = "member_id")
    private String memberId;

    /**
     * 成员姓名
     */
    @TableField(value = "member_name")
    private String memberName;

    /**
     * 是否管理员
     */
    @TableField(value = "is_admin")
    private Boolean isAdmin;

    /**
     * 查看权限
     */
    @TableField(value = "view_auth")
    private Boolean viewAuth;

    /**
     * 下载权限
     */
    @TableField(value = "download_auth")
    private Boolean downloadAuth;

    /**
     * 共享权限
     */
    @TableField(value = "share_auth")
    private Boolean shareAuth;

    /**
     * 上传权限
     */
    @TableField(value = "upload_auth")
    private Boolean uploadAuth;

    /**
     * 编辑权限
     */
    @TableField(value = "edit_auth")
    private Boolean editAuth;

    /**
     * 删除权限
     */
    @TableField(value = "delete_auth")
    private Boolean deleteAuth;
}