package com.ustack.emdedding.dto;

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
}
