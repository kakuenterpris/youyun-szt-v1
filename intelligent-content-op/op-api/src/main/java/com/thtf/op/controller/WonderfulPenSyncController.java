package com.thtf.op.controller;

import com.thtf.emdedding.dto.PushFileDTO;
import com.thtf.emdedding.dto.WonderfulPenSyncDTO;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.repo.WonderfulPenSyncRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Liyingzheng
 * @data 2025/4/23 11:03
 * @describe
 */
@RestController
@RequestMapping("/api/v1/wonderfulPenSync")
@RequiredArgsConstructor
@Slf4j
@Validated
public class WonderfulPenSyncController {

    @Autowired
    private WonderfulPenSyncRepo wonderfulPenSyncRepo;

    @PostMapping("/pushFile")
    @Operation(summary = "推送我的文档到知识库")
    public RestResponse pushFile(@RequestBody PushFileDTO pushFileDTO) {
        return wonderfulPenSyncRepo.pushFile(pushFileDTO);
    }

    @PostMapping("/getFileByUserId")
    @Operation(summary = "根据用户唯一标识检索")
    public RestResponse getFileByUserId(@RequestBody WonderfulPenSyncDTO dto) {
        return wonderfulPenSyncRepo.getFileByUserId(dto);
    }

    @PostMapping("/getKonwledgeByUserId")
    @Operation(summary = "向量检索")
    public RestResponse getKonwledgeByUserId(@RequestBody WonderfulPenSyncDTO dto) {
        return wonderfulPenSyncRepo.getKonwledgeByUserId(dto);
    }

    @PostMapping("/getFileInfo")
    @Operation(summary = "文档详情预览")
    public RestResponse getFileInfo(@RequestBody WonderfulPenSyncDTO dto) {
        return wonderfulPenSyncRepo.getFileInfo(dto);
    }

    @PostMapping("/selectFileByIds")
    @Operation(summary = "文档列表查询")
    public RestResponse selectFileByIds(@RequestBody WonderfulPenSyncDTO dto) {
        return wonderfulPenSyncRepo.selectFileByIds(dto);
    }


}
