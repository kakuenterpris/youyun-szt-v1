package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName sys_dict_type
 */
@TableName(value ="sys_dict_type")
@Data
@Schema(name = "字典信息表")
public class SysDictTypeEntity extends BaseEntity implements Serializable {

    /**
     * 字典名称
     */
    @TableField(value = "dict_name")
    private String dictName;

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