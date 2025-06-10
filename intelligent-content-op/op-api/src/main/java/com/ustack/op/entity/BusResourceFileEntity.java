package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 文件表
 * @author allm
 * @TableName bus_resource_file
 */
@TableName(value ="bus_resource_file")
@Data
public class BusResourceFileEntity extends BaseEntity {

    /**
     * 文件名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 文件夹ID
     */
    @TableField(value = "folder_id")
    private Integer folderId;

    /**
     * 文件下载id
     */
    @TableField(value = "file_id")
    private String fileId;

    /**
     * 资源大小（文件夹为0）、单位为字节
     */
    @TableField(value = "size")
    private String size;

    /**
     * 文件类型：doc/docx/excel/txt/……
     */
    @TableField(value = "file_type")
    private String fileType;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 向量化配置编码
     */
    @TableField(value = "embedding_config_code")
    private String embeddingConfigCode;

    /**
     * 向量化配置
     */
    @TableField(value = "embedding_config_name")
    private String embeddingConfigName;

    /**
     * 文件年份
     */
    @TableField(value = "file_year")
    private Integer fileYear;

    /**
     * 是否参与问答
     */
    @TableField(value = "join_query")
    private Boolean joinQuery;

    /**
     * md5预览文件id
     */
    @TableField(value = "preview_file_id")
    private String previewFileId;

    /**
     * 密级
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 密级（妙笔代码）
     */
    @TableField(exist = false)
    private String levelWonderPen;


    /**
     * 知悉规则
     */
    @TableField(value = "scope_rule")
    private String scopeRule;

    /**
     * 年限
     */
    @TableField(value = "year")
    private Integer year;
}