package com.thtf.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.access.dto.SysRoleDto;
import com.thtf.chat.dto.UpdateRoleDto;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.repo.SysRoleMenuRepo;
import com.thtf.chat.repo.SysRoleRepo;
import com.thtf.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RoleController {
    @Autowired
    private SysRoleRepo sysRoleRepo;

    @Autowired
    private SysRoleMenuRepo sysRoleMenuRepo;


    // todo 角色管理
    //todo 创建角色
    @PostMapping("/createRole")
    @Operation(summary = "创建角色接口")
    public RestResponse createRole(SysRoleEntity role) {
        try {
            sysRoleRepo.save(role);
            return RestResponse.success("创建角色成功");
        }catch (Exception e) {
            log.error("创建角色失败", e);
            return RestResponse.error("创建角色失败");
        }
    }
    //todo 删除角色（逻辑删除）
    @PostMapping("/deleteRole")
    @Operation(summary = "删除角色接口")
    public RestResponse deleteRole(List<Integer> roleId) {
        try {
            sysRoleRepo.removeBatchByIds(roleId);
            return RestResponse.success("删除角色成功");
        }catch (Exception e) {
            log.error("删除角色失败", e);
            return RestResponse.error("删除角色失败");
        }
    }

    //todo 获取角色列表
    @GetMapping("/getRoleList")
    @Operation(summary = "获取角色列表接口")
    public RestResponse getRoleList(Page<SysRoleEntity> page, SysRoleDto vo) {
        return sysRoleRepo.pageList(page,vo);
    }

    //todo 获取所有角色列表
    @GetMapping("/getRoleAllList")
    @Operation(summary = "获取所有角色列表接口")
    public RestResponse getRoleAllList() {
        List<SysRoleEntity> list;
        try {
            list = sysRoleRepo.list();
        }catch (Exception e){
            log.error("获取角色列表失败", e);
            return RestResponse.error("获取角色列表失败");
        }
        return RestResponse.success(list);
    }

    //todo 获取角色信息
    @GetMapping("/getRoleInfo")
    @Operation(summary = "获取角色信息接口")
    public RestResponse getRoleInfo(Integer roleId) {
        try {
            return RestResponse.success(sysRoleRepo.getById(roleId));
        }catch (Exception e) {
            log.error("获取角色信息失败", e);
            return RestResponse.error("获取角色信息失败");
        }
    }
    //todo 更新角色信息
    @PostMapping("/updateRole")
    @Operation(summary = "更新角色信息接口")
    public RestResponse updateRole(@RequestBody UpdateRoleDto role) {
        try {
            sysRoleRepo.updateByRoleId(role);
            return RestResponse.success("更新角色信息成功");
        }catch (Exception e) {
            log.error("更新角色信息失败", e);
            return RestResponse.error("更新角色信息失败");
        }
    }
    //todo 获取角色菜单id
    @GetMapping("/getRoleMenus")
    @Operation(summary = "获取角色菜单id接口")
    public RestResponse getRoleMenus(Integer roleId) {
        return sysRoleMenuRepo.getByRoleId(roleId);
    }


}
