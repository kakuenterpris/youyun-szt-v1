package com.ustack.op.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 部门信息表
 * @TableName bus_sub_company_info
 */
@TableName(value ="bus_sub_company_info")
@Data
public class BusSubCompanyInfoEntity {
    /**
     * 自增 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分部id
     */
    @TableField(value = "sub_company_id")
    private String subCompanyId;

    /**
     * 分部编码
     */
    @TableField(value = "sub_company_code")
    private String subCompanyCode;

    /**
     * 简称
     */
    @TableField(value = "sub_company_name")
    private String subCompanyName;

    /**
     * 全称
     */
    @TableField(value = "sub_company_desc")
    private String subCompanyDesc;

    /**
     * 上级分部id
     */
    @TableField(value = "sup_sub_com_id")
    private String supSubComId;

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