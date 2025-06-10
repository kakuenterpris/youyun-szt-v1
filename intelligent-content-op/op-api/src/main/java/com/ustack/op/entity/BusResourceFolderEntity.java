package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 文件夹表
 * @author allm
 * @TableName bus_resource_folder
 */
@TableName(value ="bus_resource_folder")
@Data
public class BusResourceFolderEntity extends BaseEntity {

    /**
     * 文件夹名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 父级资源 ID（根目录为 0）
     */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
     * 父级guid
     */
    @TableField(value = "parent_guid")
    private String parentGuid;

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
     * 是否公开
     */
    @TableField(value = "open_view")
    private Boolean openView;

    /**
     * 是否能“创建/修改/删除”下级目录
     */
    @TableField(value = "can_add_sub")
    private Boolean canAddSub;

    /**
     * 文件夹类型
     */
    @TableField(value = "type")
    private Integer type;
}