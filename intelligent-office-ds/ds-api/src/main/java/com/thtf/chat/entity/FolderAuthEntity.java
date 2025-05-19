package com.thtf.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "file_auth")
public class FolderAuthEntity {
    @TableField(value = "id")
    private Integer id;
    @TableField(value = "role_id")
    private Integer RoleId;
    @TableField(value = "folder_id")
    private Integer folderId;
    @TableField(value = "auth_type")
    private Integer authType;
}
