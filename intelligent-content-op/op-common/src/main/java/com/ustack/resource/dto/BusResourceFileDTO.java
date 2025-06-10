package com.ustack.resource.dto;

import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 文件表
 * @TableName bus_resource_file
 */
@Data
public class BusResourceFileDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 文件名称
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件名不能为空")
    private String name;

    /**
     * 文件夹ID
     */
    private Integer folderId;

    /**
     * 文件下载id
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件下载ID不能为空")
    private String fileId;

    /**
     * 资源大小（文件夹为0）、单位为字节
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "上传文件时请传入文件大小")
    private String size;

    /**
     * 文件类型：doc/docx/excel/txt/……
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "上传文件时请传入文件后缀")
    private String fileType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 向量化配置编码
     */
    private String embeddingConfigCode;

    /**
     * 向量化配置
     */
    private String embeddingConfigName;

    /**
     * 文件年份
     */
    private Integer fileYear;

    /**
     * 是否参与问答
     */
    private Boolean joinQuery;

    /**
     * md5预览文件id
     */
    private String previewFileId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人ID
     */
    private String createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 修改人ID
     */
    private String updateUserId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 密级
     */
    private Integer level;

    /**
     * 知悉范围
     */
    private List<Integer> scope;

    /**
     * 知悉规则
     */
    private String scopeRule;

    /**
     * 年限
     */
    private Integer year;
}
