package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件权限表
 * @TableName FILE_AUTH
 */
@TableName(value ="FILE_AUTH")
@Data
public class FileAuthEntity implements Serializable {
    /**
     *
     */
    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "FILE_ID")
    private Integer fileId;

    /**
     *
     */
    @TableField(value = "USER_ID")
    private Integer userId;

    /**
     *
     */
    @TableField(value = "AUTH_TYPE")
    private Integer authType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}