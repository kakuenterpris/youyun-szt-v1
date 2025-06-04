package com.thtf.chat.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.annotation.Log;
import com.thtf.chat.annotation.RequiresPermission;
import com.thtf.chat.dto.LogInfoDTO;
import com.thtf.chat.mapper.SysLogininforMapper;
import com.thtf.chat.repo.ISysLogininforRepo;
import com.thtf.chat.repo.SysOptLogRepo;
import com.thtf.enums.BusinessType;
import com.thtf.global.common.rest.RestResponse;
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
    public RestResponse getAuditLogs(Page<UserInfoDto> page, @RequestParam(required = false) String query) {
        try {
            return sysOptLogRepo.getAuditLogs(page,query);
        }catch (Exception e) {
            log.error("获取审计日志失败: {}", e.getMessage());
            return RestResponse.fail(RestResponse.ERROR_CODE,"获取审计日志失败");
        }

    }

}
