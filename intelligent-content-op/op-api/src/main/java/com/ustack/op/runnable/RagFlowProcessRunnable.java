package com.ustack.op.runnable;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.FileUploadRecordDTO;
import com.ustack.emdedding.dto.RagProcessDTO;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.BusResourceDatasetEntity;
import com.ustack.op.entity.BusResourceFileEntity;
import com.ustack.op.entity.BusResourceFolderEntity;
import com.ustack.op.entity.SysRuleTagEntity;
import com.ustack.op.enums.RagFlowStatusEnum;
import com.ustack.op.mapper.FileEmbeddingConfigMapper;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.repo.*;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.RelUserResourceService;
import com.ustack.op.service.ResourceProcessService;
import com.ustack.resource.dto.BusResourceDatasetDTO;
import com.ustack.resource.enums.IndexingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

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


    private final SysRuleTagRepo sysRuleTagRepo;

    private final BusResourceFileRepo busResourceFileRepo;

    private final BusResourceFolderRepo busResourceFolderRepo;


    // 新增结果字段
    private RestResponse processResult;

    // 新增结果获取方法
    public RestResponse getProcessResult() {
        return processResult;
    }



    public RagFlowProcessRunnable(List<RagProcessDTO> ragProcessDTOS,
                                  RagFlowProcessService ragFlowProcessService,
                                  RelUserResourceService relUserResourceService,
                                  FileUploadRecordMapper fileUploadRecordMapper,
                                  FileEmbeddingConfigMapper fileEmbeddingConfigMapper,
                                  String fileBasePath, BusResourceDatasetRepo datasetRepo,
                                  ResourceProcessService resourceProcessService,
                                  SysRuleTagRepo sysRuleTagRepo,
                                  BusResourceFileRepo busResourceFileRepo,
                                  BusResourceFolderRepo busResourceFolderRepo) {
        this.ragProcessDTOS = ragProcessDTOS;
        this.ragFlowProcessService = ragFlowProcessService;
        this.relUserResourceService = relUserResourceService;
        this.fileUploadRecordMapper = fileUploadRecordMapper;
        this.fileEmbeddingConfigMapper = fileEmbeddingConfigMapper;
        this.fileBasePath = fileBasePath;
        this.datasetRepo = datasetRepo;
        this.sysRuleTagRepo = sysRuleTagRepo;
        this.busResourceFileRepo = busResourceFileRepo;
        this.busResourceFolderRepo = busResourceFolderRepo;
    }

    @Override
    public void run() {
        try {
            this.handler();
            // 成功时构造响应
            this.processResult = this.handler();
        }catch (Exception e){
            log.error("文件处理失败", e);
            // 失败时构造错误响应
            this.processResult = RestResponse.fail(500, "文件处理失败：" + e.getMessage());
        }
    }

    public RestResponse handler() {

        List<String> successFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        for (RagProcessDTO ragProcessDTO : ragProcessDTOS) {
            // 将文档上传到ragflow并触发解析
            RestResponse response =this.uploadAndParse(ragProcessDTO);
            if (response.getCode() == 200) {
                successFiles.add(ragProcessDTO.getFileId());
            } else {
                failedFiles.add(ragProcessDTO.getFileId());
            }
        }
        // 构建最终处理结果
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successFiles.size());
        result.put("failedCount", failedFiles.size());
        result.put("successFiles", successFiles);
        result.put("failedFiles", failedFiles);
        return RestResponse.success(result);
    }

    private RestResponse  uploadAndParse(RagProcessDTO ragProcessDTO) {
        System.out.println("知识提取方法开始:===>" + ragProcessDTO.getFileId());
        // 获取上传的文件路径
        FileUploadRecordDTO fileUploadRecordDTO = fileUploadRecordMapper.getByFileId(ragProcessDTO.getFileId());
        if (null == fileUploadRecordDTO) {
            // 更新资源状态为上传失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.FILE_ERROR.getIndexingStatus(), IndexingStatusEnum.FILE_ERROR.getIndexingStatusName());
            log.error("上传文件查询为空，文件id{}", ragProcessDTO.getFileId());
            return RestResponse.fail(500, "文件记录不存在");
        }
        String path = fileUploadRecordDTO.getPath();
        String fileName = fileUploadRecordDTO.getFileName();
        String originalName = fileUploadRecordDTO.getOriginName();
        // 获取文件流全路径
        String filePath = fileBasePath + path + fileName;
        System.out.println("filePath:===>" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            // 更新资源状态为上传失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.FILE_ERROR.getIndexingStatus(), IndexingStatusEnum.FILE_ERROR.getIndexingStatusName());
            log.error("上传文件不存在，文件id{}", ragProcessDTO.getFileId());
            return RestResponse.fail(500, "文件不存在");
        }
        // 重命名文件
        String originalPath = fileBasePath + path + originalName;
        File originalFile = new File(originalPath);
        if (!originalFile.exists()) {
            try {
                Files.copy(file.toPath(), originalFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("上传ragflow，文件复制失败", e.getMessage());
            }
            if (originalFile.exists()) {
                file = originalFile;
            }
        }

        BusResourceFileEntity fileEntity = busResourceFileRepo.getById(ragProcessDTO.getResourceId());
        LambdaQueryWrapper<BusResourceDatasetEntity> queryBusResourceDataset = new LambdaQueryWrapper<>();
        queryBusResourceDataset.eq(BusResourceDatasetEntity::getCode, ContextUtil.currentUser().getUserId());
        queryBusResourceDataset.eq(BusResourceDatasetEntity::getFolderId,fileEntity.getFolderId());
        queryBusResourceDataset.eq(BusResourceDatasetEntity::getDeleted, false);
        // 获取知识库id
        BusResourceDatasetEntity busResourceDatasetEntity = datasetRepo.getOne(queryBusResourceDataset);
        // 如果没有知识库则创建
        String datesetId = "";
        if (null == busResourceDatasetEntity) {
            return RestResponse.fail(500, "获取知识库id失败");
        }else {
            datesetId = busResourceDatasetEntity.getDatasetsId();
        }
        System.out.println("datesetId:===>" + datesetId);
        // 设置抽取的规则
        List<SysRuleTagEntity> ruleTagList = new ArrayList<>();
        LambdaQueryWrapper<SysRuleTagEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 获取文件夹相关信息
        BusResourceFolderEntity folderEntity = busResourceFolderRepo.getById(fileEntity.getFolderId());
        // 知识化状态配置味空，无法获取到知识库id
        if (StrUtil.isEmpty(ragProcessDTO.getEmbeddingConfigCode())) {
            // 获取文件的配置规则
            if(StringUtils.isNotEmpty(fileEntity.getEmbeddingConfigCode())  && StringUtils.isNotEmpty(fileEntity.getEmbeddingConfigName())){
                queryWrapper.eq(SysRuleTagEntity::getRuleExtractId,fileEntity.getEmbeddingConfigCode());
                queryWrapper.orderBy(true, true, SysRuleTagEntity::getSort);
                ruleTagList = sysRuleTagRepo.list(queryWrapper);
            }else{
                // 获取文件所在文件夹的配置规则
                if(StringUtils.isNotEmpty(folderEntity.getEmbeddingConfigCode())  && StringUtils.isNotEmpty(folderEntity.getEmbeddingConfigName())){
                    queryWrapper.eq(SysRuleTagEntity::getRuleExtractId, folderEntity.getEmbeddingConfigCode());
                    queryWrapper.orderBy(true, true, SysRuleTagEntity::getSort);
                    ruleTagList = sysRuleTagRepo.list(queryWrapper);
                }else{
                    // 更新资源状态为上传失败
                    relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.RAG_CONFIG_ERROR.getIndexingStatus(), IndexingStatusEnum.UPLOAD_RAG_FAIL.getIndexingStatusName());
                    return RestResponse.fail(500, "上传ragflow失败");
                }
            }
        }else {
            queryWrapper.eq(SysRuleTagEntity::getRuleExtractId,ragProcessDTO.getEmbeddingConfigCode());
            queryWrapper.orderBy(true, true, SysRuleTagEntity::getSort);
            ruleTagList = sysRuleTagRepo.list(queryWrapper);
        }
        String changeParser = ragFlowProcessService.changeParser(ruleTagList, datesetId);
        System.out.println("changeParser:===>" + changeParser);

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
        System.out.println("将文件上传到ragflow:===>" + uploadFileId);
        if (StrUtil.isEmpty(uploadFileId)) {
            // 更新资源状态为上传失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.UPLOAD_RAG_FAIL.getIndexingStatus(), IndexingStatusEnum.UPLOAD_RAG_FAIL.getIndexingStatusName());
            return RestResponse.fail(500, "上传ragflow失败");
        } else {
            // 保存ragflow的文件id
            relUserResourceService.updateDocumentId(uploadFileId, ragProcessDTO.getResourceId(), ragProcessDTO.getFileId());
        }
        // 触发ragflow解析
        boolean parseBoolean = ragFlowProcessService.parseFile(datesetId, uploadFileId,fileEntity,folderEntity);
        System.out.println("触发ragflow解析:===>" + parseBoolean);
        if (!parseBoolean) {
            // 更新资源状态为解析失败
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.PARSE_ERROR.getIndexingStatus(), IndexingStatusEnum.PARSE_ERROR.getIndexingStatusName());
        } else {
            // 将进度更新为0
            BigDecimal progress = BigDecimal.ZERO;
            relUserResourceService.updateProgress(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(),progress);
            // 更新资源为解析中
            relUserResourceService.updateIndexStatus(ragProcessDTO.getResourceId(), ragProcessDTO.getFileId(), IndexingStatusEnum.PARSING.getIndexingStatus(), IndexingStatusEnum.PARSING.getIndexingStatusName());

        }
        return RestResponse.success("上传成功");
    }
}
