package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.ustack.global.common.validation.ValidGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * @TableName bus_resource_manage
 */
@TableName(value = "bus_resource_manage")
@Data
public class BusResourceManageEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 资源名称：文件或者文件夹
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "名称不能为空")
    @TableField(value = "name")
    private String name;

    /**
     * 父级资源 ID（根目录为 0）
     */
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "父级ID不能为空，顶层父级传0")
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
     * 资源类型：1-文件夹;2-文件
     */
    @TableField(value = "resource_type")
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "资源类型不能为空，1-文件夹；2-文件")
    private Integer resourceType;

    /**
     * 文件下载id
     */
    @TableField(value = "file_id")
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件下载ID不能为空")
    private String fileId;

    /**
     * 资源大小（文件夹为0）、单位为字节
     */
    @TableField(value = "size")
    private String size;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 文件类型：doc/docx/excel/txt/……
     */
    @TableField(value = "file_type")
    @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "文件后缀不能为空")
    private String fileType;

    /**
     *
     */
    @TableField(value = "guid", fill = FieldFill.INSERT)
    private String guid;

    /**
     *
     */
    @TableField(value = "parent_guid", fill = FieldFill.INSERT)
    private String parentGuid;

    /**
     * 分类（机构、部门、个人）
     */
    @TableField(value = "category")
    private String category;

    /**
     * 是否固定节点
     */
    @TableField(value = "is_fixed")
    private Boolean fixed;

    /**
     * 部门编码-部门id
     */
    @TableField(value = "dep_num")
    private String depNum;

    /**
     * 全称
     */
    @TableField(value = "dep_name")
    private String depName;

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
     * 预览md文件id
     */
    @TableField(value = "preview_file_id")
    private String previewFileId;

    /**
     * 创建人
     */
    @TableField(value = "create_user", fill = FieldFill.INSERT)
    private String createUser;

    /**
     * 创建人ID
     */
    @TableField(value = "create_user_id", fill = FieldFill.INSERT)
    private String createUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_user", fill = FieldFill.INSERT_UPDATE)
    private String updateUser;

    /**
     * 修改人ID
     */
    @TableField(value = "update_user_id", fill = FieldFill.INSERT_UPDATE)
    private String updateUserId;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Long version;

    /**
     * 逻辑删除字段
     */
    @TableField(value = "is_deleted")
    @TableLogic
    private Boolean deleted;
}