package com.thtf.op.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.emdedding.dto.RagProcessDTO;
import com.thtf.feign.client.KbaseApi;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.kbase.entity.KmVector;
import com.thtf.op.service.EmbeddingService;
import com.thtf.op.service.RagFlowProcessService;
import com.thtf.op.service.ResourceProcessService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
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
        return resourceProcessService.execute(fileIdList);
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

//    /**
//     * 查看向量化状态
//     * @param docId
//     * @return
//     */
//    @GetMapping("/getRagFlowStatus")
//    @Operation(summary = "查看向量化状态")
//    public RestResponse getRagFlowStatus() {
//        return RestResponse.success(ragFlowProcessService.getRagFlowStatus()) ;
//
//    }


    /**
     * 获取RagFlow的MD
     * @param docId
     * @return
     */
    @GetMapping("/getRagFlowMD")
    @Operation(summary = "获取RagFlow的MD")
    public ResponseEntity<byte[]> getRagFlowMD(@RequestParam(value = "docId") String docId) {
        String base64Md = ragFlowProcessService.getRagFlowMD(docId);
        byte[] pdfBytes = Base64.getDecoder().decode(base64Md);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_MARKDOWN)
                .body(pdfBytes);
    }


    /**
     * 获取RagFlow的PDF
     * @param docId
     * @return
     */
    @GetMapping("/getRagFlowPDF")
    @Operation(summary = "获取RagFlow的PDF")
    public ResponseEntity<byte[]> getRagFlowPDF(@RequestParam(value = "docId") String docId) {
        String base64Pdf = ragFlowProcessService.getRagFlowPDF(docId);
        byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
