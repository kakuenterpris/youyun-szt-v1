package com.ustack.login.dto;

import lombok.Data;

import java.util.Date;

/**
 * 部门信息表 DTO
 */
@Data
public class BusSubCompanyInfoDTO {
    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * 分部id
     */
    private String subCompanyId;

    /**
     * 分部编码
     */
    private String subCompanyCode;

    /**
     * 简称
     */
    private String subCompanyName;

    /**
     * 全称
     */
    private String subCompanyDesc;

    /**
     * 上级分部id
     */
    private String supSubComId;

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
    private Date createTime;
}
