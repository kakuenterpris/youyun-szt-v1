package com.thtf.emdedding.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年04月10日
 */
@Data
public class ResourceDTO {

    private Long resourceId;

    private String documentId;

    private String fileId;

    private String folderId;

    private String fileName;

    private String category;

    private Integer size;

    private String fileType;

    private String embeddingConfigName;

    private String embeddingConfigCode;

    private String createUser;

    private String createUserId;

    private String createTime;

    private String depNum;

    private String depName;

    private String ragDatasetId;

    private Integer joinQuery;
}
