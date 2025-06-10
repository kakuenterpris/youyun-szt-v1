package com.ustack.op.controller;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.service.SysOptLogService;
import com.ustack.resource.dto.SystemLogDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liyingzheng
 * @data 2025/4/22 17:32
 * @describe 操作日志
 */
@RestController
@RequestMapping("/api/v1/log")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "操作日志", description = "操作日志相关接口")
public class OperateLogController {

    private final SysOptLogService sysOptLogService;

    /**
     * 查询操作日志
     */
    @PostMapping("/get")
    @Operation(summary = "查询操作日志")
    public RestResponse get(@RequestBody SystemLogDTO dto) {
        return sysOptLogService.get(dto);
    }
}
