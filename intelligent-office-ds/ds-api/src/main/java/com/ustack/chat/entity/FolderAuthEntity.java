package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "folder_auth")
public class FolderAuthEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @TableField(value = "role_id")
    private Integer RoleId;
    @TableField(value = "folder_id")
    private Integer folderId;
    @TableField(value = "auth_type")
    private Integer authType;
}
