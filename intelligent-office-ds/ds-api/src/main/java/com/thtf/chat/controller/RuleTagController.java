package com.thtf.chat.controller;

import com.thtf.access.dto.SysRuleTagDto;
import com.thtf.chat.entity.SysRuleTagEntity;
import com.thtf.chat.repo.SysRuleTagRepo;
import com.thtf.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public RestResponse createRuleTag(SysRuleTagEntity entity) {
        return sysRuleTagRepo.saveRuleTag(entity);
    }

    @PostMapping("/deleteRuleTag")
    @Operation(summary = "删除规则标签接口")
    public RestResponse deleteRuleTag(Integer id) {
        try {
            sysRuleTagRepo.removeById(id);
        } catch (Exception e) {
            log.error("删除规则标签失败", e);
            return RestResponse.error("删除规则标签失败");
        }
        return RestResponse.success("删除规则标签成功");
    }

    @PostMapping("/deleteBatchRuleTag")
    @Operation(summary = "批量删除规则标签接口")
    public RestResponse deleteBatchRuleTag(@RequestParam("ruleTagIds") List<Integer> id) {
        try {
            sysRuleTagRepo.removeBatchByIds(id);
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
        try {
            sysRuleTagRepo.updateById(entity);
            return RestResponse.success("更新规则标签成功");
        }catch (Exception e) {
            log.error("更新规则标签失败", e);
            return RestResponse.error("更新规则标签失败");
        }
    }

    @PostMapping("/sortRuleTag")
    @Operation(summary = "排序规则标签接口")
    public RestResponse sortRuleTag(@RequestParam("id") Long id, @RequestParam("isUp") Boolean isUp) {
        return sysRuleTagRepo.sortRuleTag(id, isUp);
    }




}
