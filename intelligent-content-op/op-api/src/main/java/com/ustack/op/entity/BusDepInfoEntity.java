package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 部门信息表
 * @TableName bus_dep_info
 */
@TableName(value ="bus_dep_info")
@Data
public class BusDepInfoEntity {
    /**
     * 自增 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 部门编码-部门id
     */
    @TableField(value = "dep_num")
    private String depNum;

    /**
     * 部门编码
     */
    @TableField(value = "dep_code")
    private String depCode;

    /**
     * 简称
     */
    @TableField(value = "dep_mark")
    private String depMark;

    /**
     * 全称
     */
    @TableField(value = "dep_name")
    private String depName;

    /**
     * 上级部门编码-上级部门id
     */
    @TableField(value = "sup_dep_num")
    private String supDepNum;

    /**
     * 分部id
     */
    @TableField(value = "sub_company_id")
    private String subCompanyId;

    /**
     * 排序
     */
    @TableField(value = "showorder")
    private String showorder;

    /**
     * 封存标志，1 封存，其他为未封存
     */
    @TableField(value = "canceled")
    private String canceled;

    /**
     * 创建时间戳
     */
    @TableField(value = "created")
    private String created;

    /**
     * 修改时间戳
     */
    @TableField(value = "modified")
    private String modified;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 删除标志 (0-未删除, 1-已删除)
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;
}