package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * @TableName rel_user_resource
 */
@TableName(value = "rel_user_resource")
@Data
public class RelUserResourceEntity extends BaseEntity implements Serializable {

    /**
     *
     */
    @TableField(value = "resource_id")
    private Integer resourceId;
    /**
     *
     */
    @TableField(value = "file_Id")
    private String fileId;
    /**
     *
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     *
     */
    @TableField(value = "datasets_id")
    private String datasetsId;

    /**
     * 文件ID：有云系统文件ID返回
     */
    @TableField(value = "document_id")
    private String documentId;

    /**
     * 文件批次ID：有云系统返回
     */
    @TableField(value = "batch")
    private String batch;

    /**
     * 文件向量化状态代码：有云系统返回
     */
    @TableField(value = "indexing_status")
    private String indexingStatus;

    /**
     * 文件向量化状态名称（前端显示：知识化状态）：有云系统返回
     */
    @TableField(value = "indexing_status_name")
    private String indexingStatusName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}