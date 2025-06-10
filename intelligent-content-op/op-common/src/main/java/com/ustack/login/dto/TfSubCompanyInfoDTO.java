package com.ustack.login.dto;

import lombok.Data;

/**
 * 分部信息 DTO
 */
@Data
public class TfSubCompanyInfoDTO {
    /**
     * 分部id
     */
    private String id;

    /**
     * 分部编码
     */
    private String subcompanycode;

    /**
     * 简称
     */
    private String subcompanyname;

    /**
     * 全称
     */
    private String subcompanydesc;

    /**
     * 封存标志，1 封存，其他为未封存
     */
    private String canceled;

    /**
     * 上级分部id
     */
    private String supsubcomid;

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
