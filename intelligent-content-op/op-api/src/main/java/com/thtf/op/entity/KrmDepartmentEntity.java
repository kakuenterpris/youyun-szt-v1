package com.thtf.op.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 数聚平台部门体系
 * @TableName KRM_DEPARTMENT
 */
@TableName(value ="KRM_DEPARTMENT")
@Data
public class KrmDepartmentEntity implements Serializable {
    /**
     * 
     */
    @TableId(value = "UUID")
    private String uuid;

    /**
     * 
     */
    @TableField(value = "ID")
    private String id;

    /**
     * 
     */
    @TableField(value = "CODE")
    private String code;

    /**
     * 
     */
    @TableField(value = "DEPARTMENTID")
    private String departmentId;

    /**
     * 
     */
    @TableField(value = "DEPTID")
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
    @TableLogic
    private Boolean enabled;

    /**
     * 
     */
    @TableField(value = "ISSUB")
    private Integer isSub;

    /**
     * 
     */
    @TableField(value = "LEVEL")
    private Integer level;

    /**
     * 
     */
    @TableField(value = "MAPPINGCODE")
    private String mappingCode;

    /**
     * 
     */
    @TableField(value = "NAME")
    private String name;

    /**
     * 
     */
    @TableField(value = "NODE")
    private String node;

    /**
     * 
     */
    @TableField(value = "ORDERNUM")
    private Integer orderNum;

    /**
     * 
     */
    @TableField(value = "PID")
    private String pId;

    /**
     * 
     */
    @TableField(value = "SYSID")
    private String sysId;

    /**
     * 
     */
    @TableField(value = "POSITIONCODE")
    private String positionCode;

    @TableField(exist = false)
    private List<KrmDepartmentEntity> children;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}