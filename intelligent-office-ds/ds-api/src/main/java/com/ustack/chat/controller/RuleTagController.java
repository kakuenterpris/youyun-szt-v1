package com.ustack.chat.controller;

import com.ustack.access.dto.SysRuleTagDto;
import com.ustack.chat.entity.SysRuleTagEntity;
import com.ustack.chat.repo.SysRuleTagRepo;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ruleTag")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "知识规则提取配置标签", description = "知识规则提取配置标签相关接口")
public class RuleTagController {

    @Autowired
    private SysRuleTagRepo sysRuleTagRepo;

    @PostMapping("/createRuleTag")
    @Operation(summary = "创建规则标签接口")
    public RestResponse createRuleTag(@RequestBody SysRuleTagEntity entity) {
        return sysRuleTagRepo.saveRuleTag(entity);
    }

    @PostMapping("/deleteRuleTag")
    @Operation(summary = "删除规则标签接口")
    public RestResponse deleteRuleTag(@RequestBody SysRuleTagDto dto) {
        try {
            sysRuleTagRepo.removeById(dto.getId());
        } catch (Exception e) {
            log.error("删除规则标签失败", e);
            return RestResponse.error("删除规则标签失败");
        }
        return RestResponse.success("删除规则标签成功");
    }

    @PostMapping("/deleteBatchRuleTag")
    @Operation(summary = "批量删除规则标签接口")
    public RestResponse deleteBatchRuleTag(@RequestBody SysRuleTagDto dto) {
        try {
            sysRuleTagRepo.removeBatchByIds(dto.getIds());
            return RestResponse.success("删除规则标签成功");
        } catch (Exception e) {
            log.error("删除规则标签失败", e);
            return RestResponse.error("删除规则标签失败");
        }
    }

    @GetMapping("/getRuleTagList")
    @Operation(summary = "获取规则标签列表接口")
    public RestResponse getRuleTagList(SysRuleTagDto dto) {
        return sysRuleTagRepo.list(dto);
    }

    @PostMapping("/updateRuleTag")
    @Operation(summary = "更新规则标签接口")
    public RestResponse updateRuleTag(@RequestBody SysRuleTagEntity entity) {
        sysRuleTagRepo.updateRuleTag(entity);
        return RestResponse.success("更新规则标签成功");
    }

    @PostMapping("/sortRuleTag")
    @Operation(summary = "排序规则标签接口")
    public RestResponse sortRuleTag(@RequestBody SysRuleTagDto dto) {
        return sysRuleTagRepo.sortRuleTag(dto.getId(), dto.getIsUp());
    }

}
