package com.ustack.op.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【rel_user_resource】的数据库操作Service
 * @createDate 2025-02-19 12:18:15
 */
public interface RelUserResourceService {

    /**
     * 根据用户ID获取向量库id
     *
     * @param userId
     * @return
     */
    String getDatasetIdByUserId(String userId);


    void updateDocumentId(String documentId, Long resourceId, String fileId);

    void updateIndexStatus(Long resourceId, String fileId, String indexingStatus, String indexingStatusName);

    void updateProgress(Long resourceId, String fileId, BigDecimal progress);

    List<String> getFileIdListByUserId(String userId);

    List<String> getFolderIdListByUserId(String userId);

    List<String> getDocumentIdListByFolderId(String folderId);

    void updateIndexStatusByFolderId(String folderId, String indexingStatus, String indexingStatusName);

    void updateIndexStatus(List<String> fileIdList, String indexingStatus, String indexingStatusName);
}
