package com.ustack.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description : 文件上传记录entity
 * @Author : LinXin
 * @ClassName : FileUploadRecordEntity
 * @Date: 2021-03-10 11:47
 */
@TableName("file_upload_record")
@Data
public class FileUploadRecordEntity extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "document_id")
    private String documentId;

    @TableField(value = "batch")
    private String batch;

    @TableField(value = "file_name")
    private String fileName;

    @TableField(value = "origin_name")
    private String originName;

    @TableField(value = "path")
    private String path;

    @TableField(value = "suffix")
    private String suffix;

    @TableField(value = "size")
    private BigDecimal size;

    @TableField(value = "md5")
    private String md5;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "enable_download")
    private Boolean enableDownload;

    @TableField(value = "enable_preview")
    private Boolean enablePreview;

    @TableField(value = "order_by")
    private Integer orderBy;

    @TableField(value = "enable_edit")
    private Boolean enableEdit;

    @TableField(value = "edit_key")
    private String editKey;

    /**
     * 是否已生成到知识库
     * @date 2025/2/18 19:46
     */
    @TableField(value = "preview_generated")
    private Boolean previewGenerated;



}
