package com.ustack.op.controller;

import cn.hutool.core.collection.CollUtil;
import com.ustack.emdedding.dto.PushFileDTO;
import com.ustack.emdedding.dto.RagProcessDTO;
import com.ustack.emdedding.dto.WonderfulPenSyncDTO;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.repo.WonderfulPenSyncRepo;
import com.ustack.op.service.ResourceProcessService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Autowired
    private ResourceProcessService resourceProcessService;


    @PostMapping("/pushFile")
    @Operation(summary = "推送我的文档到知识库")
    public RestResponse pushFile(@RequestBody PushFileDTO pushFileDTO) {
        RestResponse restResponse = wonderfulPenSyncRepo.pushFile(pushFileDTO);
        if(restResponse.getCode() != 200) {
            return RestResponse.error(restResponse.getMsg());
        }
        List<RagProcessDTO> fileIdList = (List<RagProcessDTO>) restResponse.getData();
        if (!CollUtil.isEmpty(fileIdList)){
            // 直接向量化
            RestResponse response = resourceProcessService.execute(fileIdList);
            if (response.getCode() != 200) {
                log.error("向量化失败");
                return RestResponse.error(restResponse.getMsg());
            }
        }
        return RestResponse.success(restResponse.getMsg());
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
