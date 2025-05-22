package com.thtf.op.runnable;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thtf.emdedding.constants.CommonConstants;
import com.thtf.emdedding.dto.FileUploadRecordDTO;
import com.thtf.emdedding.dto.RagProcessDTO;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.global.common.rest.ContextUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.BusResourceDatasetEntity;
import com.thtf.op.mapper.FileEmbeddingConfigMapper;
import com.thtf.op.mapper.FileUploadRecordMapper;
import com.thtf.op.repo.BusResourceDatasetRepo;
import com.thtf.op.service.RagFlowProcessService;
import com.thtf.op.service.RelUserResourceService;
import com.thtf.op.service.ResourceProcessService;
import com.thtf.resource.dto.BusResourceDatasetDTO;
import com.thtf.resource.enums.IndexingStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */

@Slf4j
public class RagFlowProcessRunnable implements Runnable {
    private static final String[] whiteList = {"DOC", "DOCX", "PDF", "TXT", "PPTX", "HTML", "XLSX", "XLS", "CSV", "MD", "HTM", "JSON", "PNG", "JPEG", "JPG", "EML", "MARKDOWN",
            "doc", "docx", "markdown", "mdx", "pdf", "txt", "html", "xlsx", "xls", "csv", "md", "htm", "json", "pptx", "png", "jpeg", "jpg", "eml"};

    private final List<RagProcessDTO> ragProcessDTOS;
    private final RagFlowProcessService ragFlowProcessService;

    private final FileUploadRecordMapper fileUploadRecordMapper;

    private final String fileBasePath;

    private final RelUserResourceService relUserResourceService;

    private final BusResourceDatasetRepo datasetRepo;

    FileEmbeddingConfigMapper fileEmbeddingConfigMapper;

    private final ResourceProcessService resourceProcessService;





    public RagFlowProcessRunnable(List<RagProcessDTO> ragProcessDTOS,
                                  RagFlowProcessService ragFlowProcessService,
                                  RelUserResourceService relUserResourceService,
                                  FileUploadRecordMapper fileUploadRecordMapper,
                                  FileEmbeddingConfigMapper fileEmbeddingConfigMapper,
                                  String fileBasePath, BusResourceDatasetRepo datasetRepo, ResourceProcessService resourceProcessService) {
        this.ragProcessDTOS = ragProcessDTOS;
        this.ragFlowProcessService = ragFlowProcessService;
        this.relUserResourceService = relUserResourceService;
        this.fileUploadRecordMapper = fileUploadRecordMapper;
        this.fileEmbeddingConfigMapper = fileEmbeddingConfigMapper;
        this.fileBasePath = fileBasePath;
        this.datasetRepo = datasetRepo;
        this.resourceProcessService = resourceProcessService;
    }

    @Override
    public void run() {
        this.handler();
    }

    private void handler() {
        for (RagProcessDTO ragProcessDTO : ragProcessDTOS) {
            // 将文档上传到ragflow并触发解析
            this.uploadAndParse(ragProcessDTO);
        }
    }

    private void uploadAndParse(RagProcessDTO ragProcessDTO) {
        // 获取上传的文件路径
        FileUploadRecordDTO fileUploadRecordDTO = fileUploadRecordMapper.getByFileId(ragProcessDTO.getFileId());
        if (null == fileUploadRecordDTO) {
            // 更新资源状态为上传失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.FILE_ERROR.getIndexingStatus(), IndexingStatusEnum.FILE_ERROR.getIndexingStatusName());
            log.error("上传文件查询为空，文件id{}", ragProcessDTO.getFileId());
            return;
        }
        String path = fileUploadRecordDTO.getPath();
        String fileName = fileUploadRecordDTO.getFileName();
        String originalName = fileUploadRecordDTO.getOriginName();
        // 获取文件流全路径
        String filePath = fileBasePath + path + fileName;
        System.out.println("filePath:===>" + filePath);
//        String filePath = fileBasePath + path + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            // 更新资源状态为上传失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.FILE_ERROR.getIndexingStatus(), IndexingStatusEnum.FILE_ERROR.getIndexingStatusName());
            log.error("上传文件不存在，文件id{}", ragProcessDTO.getFileId());
            return;
        }
        // 重命名文件
        String originalPath = fileBasePath + path + originalName;
        File originalFile = new File(originalPath);
        try {
            Files.copy(file.toPath(), originalFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传ragflow，文件复制失败", e.getMessage());
        }
        if (originalFile.exists()) {
            file = originalFile;
        }
        // 知识化状态配置味空，无法获取到知识库id
//        if (StrUtil.isEmpty(ragProcessDTO.getEmbeddingConfigCode())) {
//            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.RAG_CONFIG_ERROR.getIndexingStatus(), IndexingStatusEnum.RAG_CONFIG_ERROR.getIndexingStatusName());
//            log.error("知识化配置为空，无法上传文件");
//            return;
//        }

        // 获取知识库id
        BusResourceDatasetDTO busResourceDatasetDTO = datasetRepo.getByCode(ContextUtil.currentUser().getUserId());
        // 如果没有知识库则创建
        String datesetId = "";
        if (null == busResourceDatasetDTO) {
            datesetId = resourceProcessService.createRagFlow(ContextUtil.getUserId());
            if (StrUtil.isEmpty(datesetId)) {
                relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.RAG_CREATE_ERROR.getIndexingStatus(), IndexingStatusEnum.RAG_ERROR.getIndexingStatusName());
                log.error("创建ragflow知识库id失败，无法上传文件,用户id为{}", ContextUtil.getUserId());
                return;
            }
            boolean add = datasetRepo.add("user", ContextUtil.getUserId(), datesetId);
            if (!add) {
                relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.RAG_CONFIG_ERROR.getIndexingStatus(), IndexingStatusEnum.RAG_ERROR.getIndexingStatusName());
                log.error("添加ragflow知识库id失败，无法上传文件,用户id为{}",ContextUtil.getUserId());
                return;
            }
        }else {
            datesetId = busResourceDatasetDTO.getDatasetsId();
        }

        // 获取文件所在文件夹的配置规则
        String fileId = ragProcessDTO.getFileId();



//        Map dataSetMap = fileEmbeddingConfigMapper.getDataSetData(ragProcessDTO.getEmbeddingConfigCode());
//        if (null == dataSetMap || StrUtil.isEmpty((CharSequence) dataSetMap.get("rag_dataset_id"))) {
//            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.RAG_CONFIG_ERROR.getIndexingStatus(), IndexingStatusEnum.RAG_ERROR.getIndexingStatusName());
//            log.error("获取ragflow知识库id为空，无法上传文件,知识库编码为{}", ragProcessDTO.getEmbeddingConfigCode());
//            return;
//        }
//
////        String datesetId = (String) dataSetMap.get("rag_dataset_id");
//        String supportTypeStr = (String) dataSetMap.get("support_type");
//        String[] supportTypeArray = supportTypeStr.split(CommonConstants.split_semicolon);
//        // 判断知识库是否支持当前文件类型解析
//        String suffix = FileNameUtil.getSuffix(file.getName());
//        if (Arrays.stream(supportTypeArray).noneMatch(s -> suffix.equalsIgnoreCase(s))) {
//            log.info("当前文档{}类型不在{}知识库解析范围，跳过", suffix, ragProcessDTO.getEmbeddingConfigCode());
//            return;
//        }

        // 将文件上传到ragflow
        String uploadFileId = ragFlowProcessService.uploadFile(datesetId, file);
        if (StrUtil.isEmpty(uploadFileId)) {
            // 更新资源状态为上传失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.UPLOAD_RAG_FAIL.getIndexingStatus(), IndexingStatusEnum.UPLOAD_RAG_FAIL.getIndexingStatusName());
            return;
        } else {
            // 保存ragflow的文件id
            relUserResourceService.updateDocumentId(uploadFileId, ragProcessDTO.getResourceId(), ragProcessDTO.getFileId());
        }
        // 触发ragflow解析
        boolean parseBoolean = ragFlowProcessService.parseFile(datesetId, uploadFileId);
        if (!parseBoolean) {
            // 更新资源状态为解析失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.PARSE_ERROR.getIndexingStatus(), IndexingStatusEnum.PARSE_ERROR.getIndexingStatusName());
        } else {
            // 更新资源为解析中
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.PARSING.getIndexingStatus(), IndexingStatusEnum.PARSING.getIndexingStatusName());

        }
    }
}
