package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName sys_dict_data
 */
@TableName(value ="sys_dict_data")
@Data
@Schema(name = "字典数据表")
public class SysDictDataEntity extends BaseEntity implements Serializable {

    /**
     * 字典排序
     */
    @TableField(value = "dict_sort")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @TableField(value = "dict_label")
    private String dictLabel;

    /**
     * 字典键值
     */
    @TableField(value = "dict_value")
    private String dictValue;

    /**
     * 字典类型
     */
    @TableField(value = "dict_type")
    private String dictType;

    /**
     * 状态（1正常 0停用）
     */
    @TableField(value = "status")
    private String status;

}