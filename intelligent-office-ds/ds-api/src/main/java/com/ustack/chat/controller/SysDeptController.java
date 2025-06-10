package com.ustack.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.annotation.Log;
import com.ustack.chat.entity.SysDeptEntity;
import com.ustack.chat.repo.SysDeptRepo;
import com.ustack.chat.utils.StringUtils;
import com.ustack.enums.BusinessType;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sys/dept")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "部门接口", description = "部门相关操作")
public class SysDeptController {

    private final SysDeptRepo sysDeptRepo;

    /**
     * 查询部门列表（树形）
     * @param page
     * @param dictType
     * @return
     */
    @Log(title = "部门列表", businessType = BusinessType.QUERY)
    @GetMapping("/list")
    @Operation(summary = "部门列表", description = "部门列表")
    public RestResponse list(Page<SysDeptEntity> page, SysDeptEntity dictType){
        SystemUser systemUser = ContextUtil.currentUser();
        //判断是否是管理员
        // todo 后续修改
        String role = "admin";

        Page<SysDeptEntity> list = sysDeptRepo.pageList(page,dictType,role);
        return RestResponse.success(list);
    }

    /**
     * 查询部门列表（排除节点）
     * @param deptId
     * @return
     */
    @Log(title = "部门列表", businessType = BusinessType.QUERY)
    @GetMapping("/exclude/{deptId}")
    @Operation(summary = "部门列表", description = "部门列表")
    public RestResponse excludeChild(@PathVariable(value = "deptId", required = false) Long deptId){

        List<SysDeptEntity> depts = sysDeptRepo.selectDeptList(new SysDeptEntity());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return RestResponse.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     * @param deptId
     * @return
     */
    @Log(title = "部门详情", businessType = BusinessType.QUERY)
    @GetMapping(value = "/{deptId}")
    @Operation(summary = "部门详情", description = "部门详情")
    public RestResponse getInfo(@PathVariable Long deptId){
        Boolean flag =  sysDeptRepo.checkDeptDataScope(deptId);
        if (!flag){
            String error = "没有权限访问部门数据";
            return RestResponse.error(error);
        }
        SysDeptEntity sysDept =  sysDeptRepo.selectDeptById(deptId);
        return RestResponse.success(sysDept);
    }


    /**
     * 新增部门
     * @param deptEntity
     * @return
     */
    @Log(title = "新增部门", businessType = BusinessType.INSERT)
    @PostMapping
    @Operation(summary = "新增部门", description = "新增部门")
    public RestResponse add(@RequestBody SysDeptEntity deptEntity){
        if (!sysDeptRepo.checkDeptNameUnique(deptEntity))
        {
            String error = "新增部门'" + deptEntity.getDeptName() + "'失败，部门名称已存在";
            return RestResponse.error(error);
        }
        return RestResponse.success(sysDeptRepo.insertDept(deptEntity));
    }


    /**
     * 修改部门
     * @param deptEntity
     * @return
     */
    @Log(title = "修改部门", businessType = BusinessType.UPDATE)
    @PutMapping
    @Operation(summary = "修改部门", description = "修改部门")
    public RestResponse edit(@RequestBody SysDeptEntity deptEntity){

        Long deptId = deptEntity.getDeptId();
        sysDeptRepo.checkDeptDataScope(deptId);

        if (!sysDeptRepo.checkDeptNameUnique(deptEntity))
        {
            String error = "修改部门'" + deptEntity.getDeptName() + "'失败，部门名称已存在";
            return RestResponse.error(error);
        } else if (deptEntity.getParentId().equals(deptId)) {
            String error = "修改部门'" + deptEntity.getDeptName() + "'失败，上级部门不能选择自己";
            return RestResponse.error(error);
        } else if (StringUtils.equals("1", deptEntity.getStatus()) && sysDeptRepo.selectNormalChildrenDeptById(deptId) > 0) {
            String error = "该部门包含未停用的子部门！";
            return RestResponse.error(error);
        }
        deptEntity.setUpdateUserId(ContextUtil.getUserId());
        deptEntity.setUpdateUser(ContextUtil.getUserName());
        return RestResponse.success(sysDeptRepo.updateDept(deptEntity));
    }


    /**
     * 删除部门
     * @param deptId
     * @return
     */
    @Log(title = "删除部门", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    @Operation(summary = "删除部门", description = "删除部门")
    public RestResponse remove(@PathVariable Long deptId){
        if (sysDeptRepo.hasChildByDeptId(deptId))
        {
            String error = "存在子部门,不允许删除";
            return RestResponse.error(error);
        }
        if (sysDeptRepo.checkDeptExistUser(deptId))
        {
            String error = "部门存在用户,不允许删除";
            return RestResponse.error(error);
        }
        return RestResponse.success(sysDeptRepo.deleteDeptById(deptId));
    }


}
