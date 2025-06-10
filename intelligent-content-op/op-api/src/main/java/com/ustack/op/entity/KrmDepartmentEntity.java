package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 数聚平台部门体系
 *
 * @TableName KRM_DEPARTMENT
 */
@TableName(value = "KRM_DEPARTMENT")
@Data
public class KrmDepartmentEntity {
    /**
     *
     */
    @TableField(value = "ID")
    @TableId
    private String id;

    /**
     *
     */
    @TableField(value = "CODE")
    private String code;

    /**
     *
     */
    @TableField(value = "DEPARTMENT_ID")
    private String departmentId;

    /**
     *
     */
    @TableField(value = "DEPT_ID")
    private String deptId;

    /**
     *
     */
    @TableField(value = "DESCRIPTION")
    private String description;

    /**
     *
     */
    @TableField(value = "ENABLED")
    private Integer enabled;

    /**
     *
     */
    @TableField(value = "IS_SUB")
    private Integer isSub;

    /**
     *
     */
    @TableField(value = "LEVEL")
    private Integer level;

    /**
     *
     */
    @TableField(value = "MAPPING_CODE")
    private String mappingCode;

    /**
     *
     */
    @TableField(value = "NAME")
    private String name;

    /**
     *
     */
    @TableField(value = "ORDER_NUM")
    private Integer orderNum;

    /**
     *
     */
    @TableField(value = "P_ID")
    private String pId;

    /**
     *
     */
    @TableField(value = "SYS_ID")
    private String sysId;

    /**
     *
     */
    @TableField(value = "POSITION_CODE")
    private String positionCode;

    @TableField(exist = false)
    private List<KrmDepartmentEntity> children;

    @TableField(exist = false)
    private String node;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}