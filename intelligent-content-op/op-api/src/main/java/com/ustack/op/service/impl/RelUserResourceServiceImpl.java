package com.ustack.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.util.StringUtil;
import com.ustack.op.entity.RelUserResourceEntity;
import com.ustack.op.mapper.RelUserResourceMapper;
import com.ustack.op.repo.RelUserResourceRepo;
import com.ustack.op.service.RelUserResourceService;
import com.ustack.resource.enums.IndexingStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Admin_14104
 * @description 针对表【rel_user_resource】的数据库操作Service实现
 * @createDate 2025-02-19 12:18:15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RelUserResourceServiceImpl implements RelUserResourceService {

    @Autowired
    private RelUserResourceRepo relUserResourceRepo;

    @Autowired
    private RelUserResourceMapper relUserResourceMapper;

    /**
     * 根据用户名获取向量库id
     *
     * @param userId
     * @return
     */
    @Override
    public String getDatasetIdByUserId(String userId) {
        if (StringUtil.isEmpty(userId)) {
            return null;
        }
        List<RelUserResourceEntity> list = relUserResourceRepo.list(userId);
        if (null != list && list.size() > 0) {
            return list.get(0).getDatasetsId();
        }
        return null;
    }

    /**
     * 保存上传到ragflow的documentId
     *
     * @param documentId
     * @param fileId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDocumentId(String documentId, Long resourceId, String fileId) {
        if (StrUtil.isEmpty(fileId) || StrUtil.isEmpty(documentId)) {
            log.error("更新documentId失败，guid或fileId为空");
            return;
        }
        try {
            relUserResourceMapper.updateDocumentId(documentId, resourceId, fileId, IndexingStatusEnum.WAITING.getIndexingStatus(), IndexingStatusEnum.WAITING.getIndexingStatusName());

        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新documentId失败，失败原因{}", e.getMessage());
        }
    }

    /**
     * 更新向量化状态
     *
     * @param fileId
     * @param indexingStatus
     * @param indexingStatusName
     */
    @Override
    public void updateIndexStatus(Long resourceId, String fileId, String indexingStatus, String indexingStatusName) {
        try {
            relUserResourceMapper.updateIndexStatus(resourceId, fileId, indexingStatus, indexingStatusName);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新索引状态失败，失败原因{}", e.getMessage());
        }
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    @Override
    public void updateProgress(Long resourceId, String fileId, BigDecimal progress) {
        try {
            relUserResourceMapper.updateProgress(resourceId,fileId, progress);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新索引状态失败，失败原因{}", e.getMessage());
        }
    }



    /**
     * 根据用户id获取所有能预览的文件id集合
     * @param userId
     * @return
     */
    @Override
    public List<String> getFileIdListByUserId(String userId) {
        if (StrUtil.isEmpty(userId)) {
            return null;
        }
        List<Map> list = relUserResourceMapper.selectFileIdListByUserId(userId);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        List<String> fileIdList = list.stream().map(map -> map.get("file_id").toString()).toList();
        return fileIdList;
    }

    /**
     * 根据用户id获取所有文件夹id集合
     * @param userId
     * @return
     */
    @Override
    public List<String> getFolderIdListByUserId(String userId) {
        if (StrUtil.isEmpty(userId)) {
            return null;
        }
        List<Map> list = relUserResourceMapper.selectFolderIdListByUserId(userId);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        List<String> fileIdList = list.stream().map(map -> map.get("folder_id").toString()).toList();
        return fileIdList;
    }

    /**
     * 根据文件夹id获取所有上传到ragflow的documentId集合
     * @param folderId
     * @return
     */
    @Override
    public List<String> getDocumentIdListByFolderId(String folderId) {
        if (StrUtil.isEmpty(folderId)) {
            return null;
        }
        List<Map> list = relUserResourceMapper.getDocumentIdListByFolderId(folderId);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        List<String> documentIdList = list.stream().map(map -> map.get("document_id").toString()).toList();
        return documentIdList;
    }

    /**
     * 根据文件夹id更新向量化状态
     * @param folderId
     * @param indexingStatus
     * @param indexingStatusName
     */
    @Override
    public void updateIndexStatusByFolderId(String folderId, String indexingStatus, String indexingStatusName) {
        try {
            relUserResourceMapper.updateIndexStatusByFolderId(folderId, indexingStatus, indexingStatusName);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新索引状态失败，失败原因{}", e.getMessage());
        }
    }

    @Override
    public void updateIndexStatus(List<String> fileIdList, String indexingStatus, String indexingStatusName) {
        try {
            relUserResourceMapper.updateIndexStatusByFileIds(fileIdList, indexingStatus, indexingStatusName);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("更新索引状态失败，失败原因{}", e.getMessage());
        }
    }
}




