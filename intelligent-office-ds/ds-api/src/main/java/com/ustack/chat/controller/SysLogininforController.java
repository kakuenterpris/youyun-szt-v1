package com.ustack.chat.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.access.dto.UserInfoDto;
import com.ustack.access.vo.UserInfoVO;
import com.ustack.annotation.Log;
import com.ustack.chat.annotation.RequiresPermission;
import com.ustack.chat.dto.LogInfoDTO;
import com.ustack.chat.mapper.SysLogininforMapper;
import com.ustack.chat.repo.ISysLogininforRepo;
import com.ustack.chat.repo.SysOptLogRepo;
import com.ustack.enums.BusinessType;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/LoginInfo/info")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "审计日志", description = "审计日志相关操作")
public class SysLogininforController {

    @Autowired
    private ISysLogininforRepo logininforService;

    @Autowired
    private SysOptLogRepo sysOptLogRepo;

    @GetMapping("/auditLog")
    @RequiresPermission(value="AuditLogs",authtype = 0)
    @Operation(summary = "审计日志列表接口")
    public RestResponse getAuditLogs(Page<UserInfoDto> page, @RequestParam(required = false) String query,@RequestParam(required = false) String type) {
        try {
            return sysOptLogRepo.getAuditLogs(page,query,type);
        }catch (Exception e) {
            log.error("获取审计日志失败: {}", e.getMessage());
            return RestResponse.fail(RestResponse.ERROR_CODE,"获取审计日志失败");
        }

    }

}
