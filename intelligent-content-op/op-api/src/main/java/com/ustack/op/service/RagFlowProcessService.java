package com.ustack.op.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.BusResourceFileEntity;
import com.ustack.op.entity.BusResourceFolderEntity;
import com.ustack.op.entity.RagflowEntity;
import com.ustack.op.entity.SysRuleTagEntity;
import com.ustack.resource.dto.BusResourceManageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
public interface RagFlowProcessService {

    String uploadFile(String datasetId, File file);

    boolean parseFile(String datasetId, String uploadFileId, BusResourceFileEntity fileEntity, BusResourceFolderEntity folderEntity);

    String uploadFile(MultipartFile file);

    Map chunks(String datasetId, String uploadFileId, Integer page, Integer pageSize);

    String getChunksStatus(String datasetId, String uploadFileId);

    boolean delete(String documentId, String datasetId);

    boolean delete(List<String> documentIdList, String datasetId);

    String loginRagFlow(RagflowEntity ragflowEntity);

    String createRagFlow(String fileName);

    String changeParser(List<SysRuleTagEntity> ruleTagList,String docId);

    String getRagFlowMD(String docId);

    String getRagFlowPDF(String docId);

    Boolean updateDataset(String datasetId,String newName);

//    String getRagFlowStatus();
}
