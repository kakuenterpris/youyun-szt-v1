package com.thtf.op.controller;

import com.thtf.emdedding.dto.RagProcessDTO;
import com.thtf.feign.client.KbaseApi;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.validation.ValidGroup;
import com.thtf.kbase.entity.KmVector;
import com.thtf.kbase.entity.PersonalVector;
import com.thtf.op.service.EmbeddingService;
import com.thtf.op.service.RagFlowProcessService;
import com.thtf.op.service.ResourceProcessService;
import com.thtf.resource.dto.BusResourceManageDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@RestController
@RequestMapping("/api/v1/ragflow")
@Slf4j
public class RagFlowController {

    @Autowired
    private RagFlowProcessService ragFlowProcessService;

    @Autowired
    private ResourceProcessService resourceProcessService;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private KbaseApi kbaseApi;

    @PostMapping("/upload")
    public RestResponse saveResource(@RequestParam(value = "file") MultipartFile file) {
        String uploadFileId = ragFlowProcessService.uploadFile(file);
        return RestResponse.success(uploadFileId);
    }

//    @PostMapping("/parse")
//    public RestResponse saveResource(@RequestParam(value = "uploadFileId") String uploadFileId) {
//        Boolean bool = ragFlowProcessService.parseFile(uploadFileId);
//        return RestResponse.success(bool);
//    }
//
//    @PostMapping("/chunks")
//    public RestResponse chunks(@RequestParam(value = "uploadFileId") String uploadFileId) {
//        List<Map> list = ragFlowProcessService.chunks(uploadFileId);
//        return RestResponse.success(list);
//    }

//    @PostMapping("/queryPersonal")
//    public RestResponse queryPersonal(@RequestParam(value = "content") String content, @RequestParam(value = "userId") String userId) {
//        List<Map> list = resourceProcessService.queryPersonal(content, userId);
//        return RestResponse.success(list);
//    }
//
//    @PostMapping("/queryDepartment")
//    public RestResponse queryDepartment(@RequestParam(value = "content") String content, @RequestParam(value = "depNum") String depNum) {
//        List<Map> list = resourceProcessService.queryDepartment(content, depNum);
//        return RestResponse.success(list);
//    }

    @PostMapping("/getEmbedding")
    public RestResponse getEmbedding(@RequestParam(value = "content") String content) {
        String embeddingStr = embeddingService.embedding(content);
        return RestResponse.success(embeddingStr);
    }


    /**
     * ragflow 将文件保存到ragflow上解析
     * @param fileIdList
     * @return
     */
    @PostMapping("/executeRag")
    @Operation(summary = "知识化提取")
    public RestResponse executeRag(@RequestBody List<RagProcessDTO> fileIdList) {
        resourceProcessService.execute(fileIdList);
        return RestResponse.SUCCESS;
    }

    @PostMapping("/saveKbase")
    public RestResponse saveKbase(@RequestBody KmVector kmVector) {
        return kbaseApi.insert(kmVector);
    }

    @PostMapping("/deleteRagByFileId")
    public RestResponse deleteRagByFileId(@RequestParam(value = "resourceId") Long resourceId, @RequestParam(value = "documentId") String documentId,
                                          @RequestParam(value = "fileId") String fileId, @RequestParam(value = "embeddingConfigCode") String embeddingConfigCode) {
        boolean bool = resourceProcessService.delete(resourceId, documentId, fileId, embeddingConfigCode);
        return RestResponse.success(bool);
    }

//    @PostMapping("/deleteRagByFolderId")
//    public RestResponse deleteRagByFolderId(@RequestParam(value = "folderId") String folderId,
//                                            @RequestParam(value = "embeddingConfigCode") String embeddingConfigCode) {
//        boolean bool = resourceProcessService.deleteByFolderId(folderId, embeddingConfigCode);
//        return RestResponse.success(bool);
//    }



}
