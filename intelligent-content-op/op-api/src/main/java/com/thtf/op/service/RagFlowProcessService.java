package com.thtf.op.service;

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

    boolean parseFile(String datasetId, String uploadFileId);

    String uploadFile(MultipartFile file);

    Map chunks(String datasetId, String uploadFileId, Integer page, Integer pageSize);

    String getChunksStatus(String datasetId, String uploadFileId);

    boolean delete(String documentId, String datasetId);

    boolean delete(List<String> documentIdList, String datasetId);
}
