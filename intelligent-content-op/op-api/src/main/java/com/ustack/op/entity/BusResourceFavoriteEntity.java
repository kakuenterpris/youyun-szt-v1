package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 收藏表
 * @TableName bus_resource_favorite
 */
@TableName(value ="bus_resource_favorite")
@Data
public class BusResourceFavoriteEntity extends BaseEntity {

    /**
     * 资源名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 资源id
     */
    @TableField(value = "resource_id")
    private Integer resourceId;

    /**
     * 资源类型（1文件夹 2文件）
     */
    @TableField(value = "resource_type")
    private Integer resourceType;

    /**
     * 父级资源 ID（根目录为 0）
     */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
     * 文件类型：doc/docx/excel/txt/……、多值用英文逗号分隔
     */
    @TableField(value = "file_type")
    private String fileType;
}