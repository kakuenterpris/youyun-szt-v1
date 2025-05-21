package com.thtf.emdedding.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年03月25日
 */
@Data
public class WonderfulPenSyncDTO {

    private Integer userId;

    private Integer fileId;

    private Integer parentId;

    private Integer type;

    private String query;

    private Integer folderId;
}
