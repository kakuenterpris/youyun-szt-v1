package com.ustack.login.dto;

import lombok.Data;

/**
 * 部门信息 DTO
 */
@Data
public class BusDepInfoDTO {
    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * 部门编码-部门id
     */
    private String depNum;

    /**
     * 部门编码
     */
    private String depCode;

    /**
     * 简称
     */
    private String depMark;

    /**
     * 全称
     */
    private String depName;

    /**
     * 上级部门编码-上级部门id
     */
    private String supDepNum;

    /**
     * 分部id
     */
    private String subCompanyId;

    /**
     * 排序
     */
    private String showorder;

    /**
     * 封存标志，1 封存，其他为未封存
     */
    private String canceled;

    /**
     * 创建时间戳
     */
    private String created;

    /**
     * 修改时间戳
     */
    private String modified;

    /**
     * 创建时间
     */
    private java.util.Date createTime;
}
