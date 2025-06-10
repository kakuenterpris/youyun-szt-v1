package com.ustack.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.access.dto.SysRuleExtractDto;
import com.ustack.chat.entity.SysRuleExtractEntity;
import com.ustack.chat.repo.SysRuleExtractRepo;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ruleExtract")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "知识规则提取配置", description = "知识规则提取配置相关接口")
public class RuleExtractController {

    @Autowired
    private SysRuleExtractRepo sysRuleExtractRepo;

    @PostMapping("/createRuleExtract")
    @Operation(summary = "创建提取规则接口")
    public RestResponse createRuleExtract(@RequestBody SysRuleExtractEntity entity) {
        return sysRuleExtractRepo.saveRuleExtract(entity);
    }

    @PostMapping("/deleteRuleExtract")
    @Operation(summary = "删除提取规则接口")
    public RestResponse deleteRuleExtract(@RequestBody SysRuleExtractDto dto) {
        try {
            sysRuleExtractRepo.removeById(dto.getId());
        } catch (Exception e) {
            log.error("删除提取规则失败", e);
            return RestResponse.error("删除提取规则失败");
        }
        return RestResponse.success("删除提取规则成功");
    }

    @PostMapping("/deleteBatchRuleExtract")
    @Operation(summary = "批量删除提取规则接口")
    public RestResponse deleteBatchRuleExtract(List<Long> id) {
        try {
            sysRuleExtractRepo.removeBatchByIds(id);
            return RestResponse.success("删除提取规则成功");
        } catch (Exception e) {
            log.error("删除提取规则失败", e);
            return RestResponse.error("删除提取规则失败");
        }
    }

    @GetMapping("/getRuleExtractList")
    @Operation(summary = "获取提取规则列表接口")
    public RestResponse getRuleExtractList(SysRuleExtractDto dto) {
        return sysRuleExtractRepo.list(dto);
    }

    @PostMapping("/updateRuleExtract")
    @Operation(summary = "更新提取规则接口")
    public RestResponse updateRuleExtract(@RequestBody SysRuleExtractEntity entity) {
        try {
            sysRuleExtractRepo.updateById(entity);
            return RestResponse.success("更新提取规则成功");
        }catch (Exception e) {
            log.error("更新提取规则失败", e);
            return RestResponse.error("更新提取规则失败");
        }
    }


    @PostMapping("/setRuleExtract")
    @Operation(summary = "设置提取规则接口")
    public RestResponse setRuleExtract(@RequestBody SysRuleExtractDto dto) {
        try {
            sysRuleExtractRepo.setRuleExtract(dto);
            return RestResponse.success("设置提取规则成功");
        } catch (Exception e) {
            log.error("设置提取规则失败", e);
            return RestResponse.error("设置提取规则失败");
        }


    }


}
