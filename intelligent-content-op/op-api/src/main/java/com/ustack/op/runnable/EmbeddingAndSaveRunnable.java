package com.ustack.op.runnable;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.ResourceDTO;
import com.ustack.feign.client.KbaseApi;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.KmVector;
import com.ustack.kbase.entity.PersonalVector;
import com.ustack.op.enums.ChunksStatusEnum;
import com.ustack.op.service.EmbeddingService;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.RelUserResourceService;
import com.ustack.op.util.FileUtils;
import com.ustack.resource.enums.IndexingStatusEnum;
import com.ustack.resource.enums.ResourceCategoryEnum;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月10日
 */
@Slf4j
public class EmbeddingAndSaveRunnable implements Runnable {

    private final KbaseApi kbaseApi;
    private final EmbeddingService embeddingService;
    private final RagFlowProcessService ragFlowProcessService;

    private final ResourceDTO resourceDTO;

    private final RelUserResourceService relUserResourceService;

    private final RedisUtil redisUtil;


    public EmbeddingAndSaveRunnable(RagFlowProcessService ragFlowProcessService, KbaseApi kbaseApi, EmbeddingService embeddingService,
                                    RelUserResourceService relUserResourceService,
                                    ResourceDTO resourceDTO, RedisUtil redisUtil) {
        this.kbaseApi = kbaseApi;
        this.embeddingService = embeddingService;
        this.ragFlowProcessService = ragFlowProcessService;
        this.relUserResourceService = relUserResourceService;
        this.resourceDTO = resourceDTO;
        this.redisUtil = redisUtil;
    }

    @Override
    public void run() {
        Integer currentTotal = 0;
        this.handler(CommonConstants.CHUNKS_PAGE, CommonConstants.CHUNKS_PAGE_SIZE, currentTotal);
    }

    /**
     * 分页获取切片数据，并向量化存储
     *
     * @param page
     * @param pageSize
     * @param currentTotal
     */
    private void handler(Integer page, Integer pageSize, Integer currentTotal) {

        // 获取切片状态
        String chunksStatus = ragFlowProcessService.getChunksStatus(resourceDTO.getRagDatasetId(), resourceDTO.getDocumentId());
        // 切片状态为失败，更新状态
        if (StrUtil.isEmpty(chunksStatus) || chunksStatus.equals(ChunksStatusEnum.FAIL.getStatus())) {
            // 标记向量化状态为异常
            updateStatusAndReleaseLock(IndexingStatusEnum.CHUNKS_ERROR);
            return;
        }
        // 切片状态为已完成，获取切片内容
        if (StrUtil.isNotEmpty(chunksStatus) && chunksStatus.equals(ChunksStatusEnum.DONE.getStatus())) {
            processDoneStatus(page, pageSize, currentTotal);
            return;
        }
        // 切片状态为进行中，则标记为切片中
        if (StrUtil.isNotEmpty(chunksStatus) && chunksStatus.equals(ChunksStatusEnum.RUNNING.getStatus())) {
            updateStatusAndReleaseLock(IndexingStatusEnum.PARSING);
        }

    }

    /**
     * 更新向量化状态并释放锁
     *
     * @param status
     */
    private void updateStatusAndReleaseLock(IndexingStatusEnum status) {
        String redisKey = CommonConstants.REDIS_CHUNKS_KEY + resourceDTO.getRagDatasetId() + "-" + resourceDTO.getDocumentId();
        try {
            relUserResourceService.updateIndexStatus(
                    resourceDTO.getResourceId(),
                    resourceDTO.getFileId(),
                    status.getIndexingStatus(),
                    status.getIndexingStatusName()
            );
        } finally {
            redisUtil.delete(redisKey);
        }
    }

    /**
     * 切片状态为已完成，获取切片内容并存储
     *
     * @param page
     * @param pageSize
     * @param currentTotal
     */
    private void processDoneStatus(Integer page, Integer pageSize, Integer currentTotal) {

        Map chunksMap = ragFlowProcessService.chunks(resourceDTO.getRagDatasetId(), resourceDTO.getDocumentId(), page, pageSize);
        if (CollUtil.isEmpty(chunksMap)) {
            // 标记状态为切片为空
            updateStatusAndReleaseLock(IndexingStatusEnum.CHUNKS_EMPTY);
            return;
        }
        List<Map> chunks = (List<Map>) chunksMap.get("chunks");
        Integer total = (Integer) chunksMap.get("total");
        if (total == 0 || chunks.size() == 0) {
            // 标记状态为切片为空
            updateStatusAndReleaseLock(IndexingStatusEnum.CHUNKS_EMPTY);
            return;
        }
        currentTotal += chunks.size();
        // 遍历切片向量化
        boolean saveFlag = this.embeddingAndSave(chunks);
        if (!saveFlag) {
            // relUserResourceService.updateIndexStatus(resourceDTO.getResourceId(), resourceDTO.getFileId(), IndexingStatusEnum.EMBEDDING_ERROR.getIndexingStatus(), IndexingStatusEnum.EMBEDDING_ERROR.getIndexingStatusName());
            return;
        }
        // 实际取到的总数等于总数，则标记为已完成
        if (total.equals(currentTotal)) {
            // 标记向量化状态为已完成
            relUserResourceService.updateIndexStatus(resourceDTO.getResourceId(), resourceDTO.getFileId(), IndexingStatusEnum.COMPLETED.getIndexingStatus(), IndexingStatusEnum.COMPLETED.getIndexingStatusName());
        } else {
            // 递归继续分区取切片
            this.processDoneStatus(page + 1, pageSize, currentTotal);
        }
    }

    private Boolean embeddingAndSave(List<Map> chunks) {
        boolean flag = true;
        for (Map chunk : chunks) {
            // 向量化
            String content = chunk.get("content").toString();
            String chunkId = chunk.get("id").toString();
            List<String> keywords = (List<String>) chunk.get("important_keywords");
            String keywordsStr = "";
            if (CollUtil.isNotEmpty(keywords)) {
                keywordsStr = FileUtils.listStringToString(keywords);
            }
            // 判断待向量内容是否超过最大长度
            if (content.length() > CommonConstants.MAX_EMBEDDING_LENGTH) {
                log.error("向量化内容过长，原文内容为{}", content);
                relUserResourceService.updateIndexStatus(resourceDTO.getResourceId(), resourceDTO.getFileId(), IndexingStatusEnum.EMBEDDING_LENGTH_ERROR.getIndexingStatus(), IndexingStatusEnum.EMBEDDING_LENGTH_ERROR.getIndexingStatusName());
                flag = false;
                continue;
            }
            String embedding = embeddingService.embedding(content);
            if (StrUtil.isEmpty(embedding)) {
                log.error("向量化结果为空，原文内容为{}", content);
                // 标记向量化状态为异常
                relUserResourceService.updateIndexStatus(resourceDTO.getResourceId(), resourceDTO.getFileId(), IndexingStatusEnum.EMBEDDING_ERROR.getIndexingStatus(), IndexingStatusEnum.EMBEDDING_ERROR.getIndexingStatusName());
                flag = false;
                continue;
            }
            try {
                // 存储向量化数据到kbase中
                this.saveKmVector(chunkId, content, embedding, keywordsStr);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("向量化处理并存储到kbase异常,", e);
                relUserResourceService.updateIndexStatus(resourceDTO.getResourceId(), resourceDTO.getFileId(), IndexingStatusEnum.EMBEDDING_SAVE_ERROR.getIndexingStatus(), IndexingStatusEnum.EMBEDDING_SAVE_ERROR.getIndexingStatusName());
                return false;
            }

        }
        if (!flag) {
            return false;
        }
        return true;
    }

    /**
     * 存储向量化数据到kbase中
     *
     * @param chunkId
     * @param content
     * @param embedding
     * @param keywordsStr
     */
    private void saveKmVector(String chunkId, String content, String embedding, String keywordsStr) {
        KmVector kmVector = new KmVector();
        kmVector.setFolderId(resourceDTO.getFolderId());
        kmVector.setFileId(resourceDTO.getFileId());
        kmVector.setFileName(resourceDTO.getFileName());
        kmVector.setFileType(resourceDTO.getFileType());
        kmVector.setFileContentType(resourceDTO.getEmbeddingConfigName());
        if (null != resourceDTO.getCreateTime()) {
            kmVector.setUploadTime(resourceDTO.getCreateTime());
            Date date = null;
            try {
                date = CommonConstants.outputFormat.parse(resourceDTO.getCreateTime());
                // 获取年和月（注意：月份从0开始）
                int year = date.getYear() + CommonConstants.YEAR_ADD;
                int month = date.getMonth() + 1; // 需要加1
                kmVector.setYear(year);
                kmVector.setMonth(month);
            } catch (ParseException e) {
                e.printStackTrace();
                log.error("时间转换异常,", e.getMessage());
                throw new RuntimeException("时间转换异常，文件id为" + resourceDTO.getFileId());
            }
        }
        kmVector.setUserId(resourceDTO.getCreateUserId());
        kmVector.setUserName(resourceDTO.getCreateUser());
        kmVector.setDepartmentName(resourceDTO.getDepName());
        kmVector.setDepartmentNum(resourceDTO.getDepNum());
        kmVector.setSliceParent(content);
        kmVector.setSliceParentVector(embedding);
        kmVector.setKeywords(keywordsStr);
        kmVector.setWordCount(content.length());
        kmVector.setValid(resourceDTO.getJoinQuery());
        kmVector.setChunkId(chunkId);
        RestResponse response = kbaseApi.insert(kmVector);
        if (null == response || response.getCode() != 200) {
            throw new RuntimeException("kbase插入数据连接异常，文件id为" + resourceDTO.getFileId());
        }
        boolean bool = (boolean) response.getData();
        if (!bool) {
            throw new RuntimeException("kbase插入数据异常，文件id为" + resourceDTO.getFileId());
        }
    }

    private void institutionStructure(String content, String embedding, String keywordsStr) {
        CompanyVector companyVector = new CompanyVector();
//        String title = (String) sliceMap.get(CommonConstants.CHUNKS_TITLE_PARAM);
//        String section = (String) sliceMap.get(CommonConstants.CHUNKS_SECTION_PARAM);
//        String item = (String) sliceMap.get("item");
//        String parentContent = (String) sliceMap.get(CommonConstants.CHUNKS_PARENT_PARAM);
//        List<String> listChildContent = (List<String>) (sliceMap.get(CommonConstants.CHUNKS_CHILD_PARAM));
//        String listChildCotentStr = null;
//        if (ObjectUtil.isNotEmpty(listChildContent)) {
//            listChildCotentStr = FileUtils.listStringToString(listChildContent);
//        }

        companyVector.setSliceParent(content);
        companyVector.setKeywords(keywordsStr);
        companyVector.setWordCount(content.length());
        // companyVector.setSliceChild(listChildCotentStr);
        companyVector.setCompanyCode(resourceDTO.getDepNum());
        companyVector.setCompanyName(resourceDTO.getDepName());
        companyVector.setUploadUserId(resourceDTO.getCreateUserId());
        companyVector.setFileId(resourceDTO.getFileId());
        companyVector.setFileName(resourceDTO.getFileName());
        companyVector.setFileType(resourceDTO.getFileType());
        companyVector.setFileContentType(resourceDTO.getEmbeddingConfigName());
        if (null != resourceDTO.getCreateTime()) {
            companyVector.setUploadTime(resourceDTO.getCreateTime());
        }
        companyVector.setSliceParentVector(embedding);
//        if (StringUtils.isNotEmpty(title)) {
//            String titleEmbedding = embeddingService.embeddingTemp(title);
//            companyVector.setTitleVector(titleEmbedding);
//        }
//        if (StringUtils.isNotEmpty(section)) {
//            String sectionEmbedding = embeddingService.embeddingTemp(section);
//            companyVector.setChapterVector(sectionEmbedding);
//        }


//        if (!ObjectUtil.isEmpty(listChildContent)) {
//            String contentEmbedding = embeddingService.embeddingListTemp(listChildContent);
//            companyVector.setSliceChildVector(contentEmbedding);
//        }
        kbaseApi.insertCompany(companyVector);
    }

    /**
     * 构造部门向量表并存储
     *
     * @param content
     * @param embedding
     */
    private void departmentStructure(String content, String embedding, String keywordsStr) {
        DepartmentVector departmentVector = new DepartmentVector();
//        String title = (String) sliceMap.get(CommonConstants.CHUNKS_TITLE_PARAM);
//        String section = (String) sliceMap.get(CommonConstants.CHUNKS_SECTION_PARAM);
//        String item = (String) sliceMap.get("item");
//        String edition = (String) sliceMap.get(CommonConstants.CHUNKS_EDITION_PARAM);
//        String parentContent = (String) sliceMap.get(CommonConstants.CHUNKS_PARENT_PARAM);
//        List<String> listChildContent = (List<String>) (sliceMap.get(CommonConstants.CHUNKS_CHILD_PARAM));
//        String listChildCotentStr = null;
//        if (ObjectUtil.isNotEmpty(listChildContent)) {
//            listChildCotentStr = FileUtils.listStringToString(listChildContent);
//        }
//        departmentVector.setTitle(title);
//        departmentVector.setChapter(section);
//        departmentVector.setSliceParent(parentContent);
//        departmentVector.setSliceChild(listChildCotentStr);
//        departmentVector.setEdition(edition);
        departmentVector.setSliceParent(content);
        departmentVector.setKeywords(keywordsStr);
        departmentVector.setWordCount(content.length());
        departmentVector.setDepartmentCode(resourceDTO.getDepNum());
        departmentVector.setDepartmentName(resourceDTO.getDepName());
        departmentVector.setUploadUserId(resourceDTO.getCreateUserId());
        departmentVector.setFileId(resourceDTO.getFileId());
        departmentVector.setFileName(resourceDTO.getFileName());
        departmentVector.setFileType(resourceDTO.getFileType());
        departmentVector.setFileContentType(resourceDTO.getEmbeddingConfigName());
        departmentVector.setSliceParentVector(embedding);
        if (null != resourceDTO.getCreateTime()) {
            departmentVector.setUploadTime(resourceDTO.getCreateTime());
        }
//        if (StringUtils.isNotEmpty(title)) {
//            String titleEmbedding = embeddingService.embeddingTemp(title);
//            departmentVector.setTitleVector(titleEmbedding);
//        }
//        if (StringUtils.isNotEmpty(section)) {
//            String sectionEmbedding = embeddingService.embeddingTemp(section);
//            departmentVector.setChapterVector(sectionEmbedding);
//        }
//        if (StringUtils.isNotEmpty(parentContent)) {
//            String contentEmbedding = embeddingService.embeddingTemp(parentContent);
//            departmentVector.setSliceParentVector(contentEmbedding);
//        }
//        if (!ObjectUtil.isEmpty(listChildContent)) {
//            String contentEmbedding = embeddingService.embeddingListTemp(listChildContent);
//            departmentVector.setSliceChildVector(contentEmbedding);
//        }
        try {
            kbaseApi.insertDepartment(departmentVector);
        } catch (Exception e) {
            log.error("向量入库失败", e);
        }

    }

    /**
     * 构造个人向量表并存储
     *
     * @param content
     * @param embedding
     */
    private void personalStructure(String content, String embedding, String keywordsStr) {
        PersonalVector personalVector = new PersonalVector();
//        String title = (String) sliceMap.get(CommonConstants.CHUNKS_TITLE_PARAM);
//        String section = (String) sliceMap.get(CommonConstants.CHUNKS_SECTION_PARAM);
//        // String item = (String) sliceMap.get("item");
//        String parentContent = (String) sliceMap.get(CommonConstants.CHUNKS_PARENT_PARAM);
//        List<String> listChildContent = (List<String>) sliceMap.get(CommonConstants.CHUNKS_CHILD_PARAM);
//        String listChildCotentStr = null;
//        if (ObjectUtil.isNotEmpty(listChildContent)) {
//            listChildCotentStr = FileUtils.listStringToString(listChildContent);
//        }
//        personalVector.setTitle(title);
//        personalVector.setChapter(section);
        personalVector.setSliceParent(content);
        personalVector.setKeywords(keywordsStr);
        personalVector.setWordCount(content.length());
        // personalVector.setSliceChild(listChildCotentStr);
        personalVector.setUserId(resourceDTO.getCreateUserId());
        personalVector.setUserName(resourceDTO.getCreateUser());
        personalVector.setDepartmentName(resourceDTO.getDepName());
        personalVector.setFileId(resourceDTO.getFileId());
        personalVector.setFileName(resourceDTO.getFileName());
        personalVector.setFileType(resourceDTO.getFileType());
        personalVector.setFileContentType(resourceDTO.getEmbeddingConfigName());
        personalVector.setSliceParentVector(embedding);
        if (null != resourceDTO.getCreateTime()) {
            personalVector.setUploadTime(resourceDTO.getCreateTime());
        }
//        if (StringUtils.isNotEmpty(title)) {
//            String titleEmbedding = embeddingService.embeddingTemp(title);
//            personalVector.setTitleVector(titleEmbedding);
//        }
//        if (StringUtils.isNotEmpty(section)) {
//            String sectionEmbedding = embeddingService.embeddingTemp(section);
//            personalVector.setChapterVector(sectionEmbedding);
//        }
//        if (StringUtils.isNotEmpty(parentContent)) {
//            String contentEmbedding = embeddingService.embeddingTemp(parentContent);
//            personalVector.setSliceParentVector(contentEmbedding);
//        }
//        if (!ObjectUtil.isEmpty(listChildContent)) {
//            String contentEmbedding = embeddingService.embeddingListTemp(listChildContent);
//            personalVector.setSliceChildVector(contentEmbedding);
//        }
        RestResponse response = kbaseApi.insertPersonal(personalVector);
        if (response.getCode() != 200) {
            throw new RuntimeException("查询kbase数据失败");
        }
    }
}
