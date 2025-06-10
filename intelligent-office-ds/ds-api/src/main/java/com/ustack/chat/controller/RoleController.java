package com.ustack.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.access.dto.SysRoleDto;
import com.ustack.chat.annotation.RequiresPermission;
import com.ustack.chat.dto.UpdateRoleDto;
import com.ustack.chat.entity.FolderAuthEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.SysRoleMenuEntity;
import com.ustack.chat.repo.FolderAuthRepo;
import com.ustack.chat.repo.SysRoleMenuRepo;
import com.ustack.chat.repo.SysRoleRepo;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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

    @Autowired
    private FolderAuthRepo folderAuthRepo;


    @PostMapping("/createRole")
    @RequiresPermission(value="RoleManage",authtype = 1)
    @Operation(summary = "创建角色接口")
    @Transactional(rollbackFor = Exception.class)
    public RestResponse createRole(@RequestBody SysRoleEntity role) {
        try {
                role.setCreateTime(new Date());
                sysRoleRepo.save(role);
                System.out.println(role);
                List<FolderAuthEntity> folderAuthList = role.getFolderAuthList();
                if (folderAuthList != null && !folderAuthList.isEmpty()) {
                for (FolderAuthEntity folderAuthEntity : folderAuthList) {
                    folderAuthEntity.setRoleId(Math.toIntExact(role.getRoleId()));
                    folderAuthEntity.setId(null); // 清空ID，避免更新
                }
                folderAuthRepo.saveBatch(folderAuthList);
            }

            if (role.getMenuAuth() != null&&!role.getMenuAuth().isEmpty()) {
                    List<SysRoleMenuEntity> menuAuth = role.getMenuAuth();
                    for (SysRoleMenuEntity sysRoleMenu : menuAuth) {
                        sysRoleMenu.setRoleId(role.getRoleId());
                    }
                    sysRoleMenuRepo.saveBatch(role.getMenuAuth());
                }
            return RestResponse.success("创建角色成功");
        }catch (Exception e) {
            log.error("创建角色失败", e);
            return RestResponse.error("创建角色失败");
        }
    }

    @PostMapping("/deleteRole")
    @RequiresPermission(value="RoleManage",authtype = 1)
    @Operation(summary = "删除角色接口")
    public RestResponse deleteRole(@RequestBody SysRoleDto dto) {
        try {
            sysRoleRepo.removeBatchByIds(dto.getRoleIds());
            return RestResponse.success("删除角色成功");
        }catch (Exception e) {
            log.error("删除角色失败", e);
            return RestResponse.error("删除角色失败");
        }
    }


    @GetMapping("/getRoleList")
    @RequiresPermission(value="RoleManage",authtype = 0)
    @Operation(summary = "获取角色列表接口")
    public RestResponse getRoleList(Page<SysRoleEntity> page, SysRoleDto vo) {
        return sysRoleRepo.pageList(page,vo);
    }


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

    @PostMapping("/updateRole")
    @RequiresPermission(value="RoleManage",authtype = 1)
    @Operation(summary = "更新角色信息接口")
    public RestResponse updateRole(@RequestBody UpdateRoleDto role) {
        try {
            return sysRoleRepo.updateByRoleId(role);
        }catch (Exception e) {
            log.error("更新角色信息失败", e);
            return RestResponse.error("更新角色信息失败");
        }
    }

    @GetMapping("/getRoleMenus")
    @RequiresPermission(value="UserManage",authtype = 0)
    @Operation(summary = "获取角色菜单id接口")
    public RestResponse getRoleMenus(Integer roleId) {
        return sysRoleMenuRepo.getByRoleId(roleId);
    }


}
