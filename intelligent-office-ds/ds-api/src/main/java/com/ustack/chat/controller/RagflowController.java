package com.ustack.chat.controller;


import com.ustack.annotation.Log;
import com.ustack.chat.entity.RagflowEntity;
import com.ustack.chat.repo.RagflowRepo;
import com.ustack.enums.BusinessType;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ragflow")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "ragflow", description = "ragflow相关操作")
public class RagflowController {

    private final RagflowRepo ragflowRepo;

    /**
     * 注册用户
     */
    @PostMapping("/register")
    @Operation(summary = "注册用户", description = "注册用户")
    @Log(title = "注册用户", businessType = BusinessType.INSERT)
    public RestResponse registerUser(RagflowEntity ragflowEntity) {
        return ragflowRepo.registerUser(ragflowEntity);
    }


    /**
     * 邀请用户
     */
    @PostMapping("/invite")
    @Operation(summary = "邀请用户", description = "邀请用户")
    @Log(title = "邀请用户", businessType = BusinessType.INSERT)
    public RestResponse inviteUser(RagflowEntity ragflowEntity) {
        return ragflowRepo.inviteUser(ragflowEntity);
    }


    /**
     * 查看团队下所有用户
     */
    @GetMapping("/list")
    @Operation(summary = "查看团队下所有用户", description = "查看团队下所有用户")
    @Log(title = "查看团队下所有用户", businessType = BusinessType.QUERY)
    public RestResponse listUser() {
        return ragflowRepo.listUser();
    }

    /**
     * 查看加入的团队
     */
    @GetMapping("/teams")
    @Operation(summary = "查看加入的团队", description = "查看加入的团队")
    @Log(title = "查看加入的团队", businessType = BusinessType.QUERY)
    public RestResponse teams() {
        return ragflowRepo.teams();
    }


    /**
     * 接受邀请
     */
    @PutMapping("/accept")
    @Operation(summary = "接受邀请", description = "接受邀请")
    @Log(title = "接受邀请", businessType = BusinessType.INSERT)
    public RestResponse accept(@RequestParam("teamId") String teamId) {
        Map<String, String> params = new HashMap<>();
        params.put("teamId", teamId);
        return ragflowRepo.accept(params);
    }


    /**
     * 退出团队
     */
    @DeleteMapping("/delete")
    @Operation(summary = "退出团队", description = "退出团队")
    @Log(title = "退出团队", businessType = BusinessType.DELETE)
    public RestResponse delete(@RequestParam("teamId") String teamId) {
        return ragflowRepo.delete(teamId);
    }

}
