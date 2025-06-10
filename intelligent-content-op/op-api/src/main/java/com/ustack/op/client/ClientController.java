package com.ustack.op.client;

import com.ustack.emdedding.dto.QueryKmDTO;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.service.ResourceProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangwei
 * @date 2025年04月18日
 */
@RestController
@RequestMapping("/api/v1/op/client")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "对外接口", description = "对外接口相关操作")
public class ClientController {

    @Autowired
    private ResourceProcessService resourceProcessService;

    @PostMapping("/queryKm")
    @Operation(summary = "查询知识库接口")
    public RestResponse queryKm(@RequestBody QueryKmDTO queryKmDTO) {
        return resourceProcessService.query(queryKmDTO);

    }
}
