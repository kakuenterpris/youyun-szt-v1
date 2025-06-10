package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

/**
 * 人员或部门与知识库关联表
 * @TableName bus_resource_dataset
 */
@TableName(value ="bus_resource_dataset")
@Data
public class BusResourceDatasetEntity extends BaseEntity {

    /**
     * 类别编码，区别用户/部门/机构
     */
    @TableField(value = "category_code")
    private String categoryCode;

    /**
     * user_id或dep_num
     */
    @TableField(value = "code")
    private String code;

    /**
     * 知识库ID（调用有云接口生成）
     */
    @TableField(value = "datasets_id")
    private String datasetsId;
}