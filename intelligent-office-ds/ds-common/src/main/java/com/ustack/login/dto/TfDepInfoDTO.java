package com.ustack.login.dto;

import lombok.Data;

import java.util.Date;

/**
 * 部门信息 DTO
 */
@Data
public class TfDepInfoDTO {
    /**
     * 部门id
     */
    private String id;

    /**
     * 部门编码
     */
    private String departmentcode;

    /**
     * 简称
     */
    private String departmentmark;

    /**
     * 全称
     */
    private String departmentname;

    /**
     * 上级部门id
     */
    private String supdepid;

    /**
     * 分部id
     */
    private String subcompanyid1;

    /**
     * 封存标志，1 封存，其他为未封存
     */
    private String canceled;

    /**
     * 排序
     */
    private String showorder;

    /**
     * 创建时间戳
     */
    private String created;

    /**
     * 修改时间戳
     */
    private String modified;
}
