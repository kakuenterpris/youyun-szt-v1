package com.thtf.emdedding.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年03月25日
 */
@Data
public class WonderfulPenSyncDTO {

    private String userId;

    private String fileId;

    private List<String> fileIds;

    private Integer parentId;

    private String type;

    private String query;

    private Integer folderId;

    private List<Map<String, Object>> folderIds;

    private String filePath;

    private String fileName;

    private String url;

    private String securityLevel;

    // 返回条目数，默认1024
    private Integer topK;
    // 相似度阈值，取值范围0~1。默认0.2
    private Double similarityThreshold;
    // 关键词占比，取值范围0~1，默认0.3
    private Double vectorSimilarityWeight;
}
