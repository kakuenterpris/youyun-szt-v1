package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @TableName table_test
 */
@TableName(value ="table_test")
@Data
public class TableTestEntity  extends BaseEntity implements Serializable {

    /**
     *
     */
    @TableField(value = "name")
    private String name;

    /**
     *
     */
    @TableField(value = "age")
    private Integer age;


}