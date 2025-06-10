package com.ustack.resource.dto;

import lombok.Data;

/**
 * @author PingY
 * @Classname RelUserResourceDTO
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
@Data
public class RelUserResourceDTO {

    private Integer id;

    private Long resourceId;

    private String fileId;

    private String userId;

    private String datasetsId;

    /**
     * 文件ID：有云系统文件ID返回
     */
    private String documentId;

    /**
     * 文件批次ID：有云系统返回
     */
    private String batch;

    /**
     * 文件向量化状态代码：有云系统返回
     */
    private String indexingStatus;

    /**
     * 文件向量化状态名称（前端显示：知识化状态）：有云系统返回
     */
    private String indexingStatusName;

    private String guid;
}
