package com.ustack.resource.dto;

import lombok.Data;

import java.util.Date;

/**
 * 人员或部门与知识库关联表 DTO
 */
@Data
public class BusResourceDatasetDTO {
    /**
     * 自增 ID
     */
    private Integer id;

    /**
     * guid
     */
    private String guid;

    /**
     * 类别编码，区别用户/部门/机构
     */
    private String categoryCode;

    /**
     * user_id或dep_num
     */
    private String code;

    /**
     * 知识库ID（调用有云接口生成）
     */
    private String datasetsId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建人 ID
     */
    private String createUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新人 ID
     */
    private String updateUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 版本号（用于乐观锁）
     */
    private Integer version;
}
