package com.ustack.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.access.dto.SysMenuDto;
import com.ustack.access.dto.SysRoleDto;
import com.ustack.chat.dto.MenuTreeNode;
import com.ustack.chat.entity.SysMenuEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.repo.SysMenuRepo;
import com.ustack.chat.repo.SysRoleMenuRepo;
import com.ustack.chat.repo.impl.SysMenuRepoImpl;
import com.ustack.global.common.rest.RestResponse;
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
@RequestMapping("/api/v1/menu")
@Slf4j
@RequiredArgsConstructor
@Validated
public class MenuController {

    @Autowired
    private SysMenuRepo sysMenuRepo;

    @Autowired
    private SysMenuRepoImpl sysMenuRepoImpl;

    @Autowired
    private SysRoleMenuRepo sysRoleMenuRepo;


    @PostMapping("/createMenu")
    @Operation(summary = "创建菜单接口")
    public RestResponse createMenu(SysMenuEntity menu) {
        try {
            sysMenuRepo.save(menu);
            return RestResponse.success("创建菜单成功");
        }catch (Exception e) {
            log.error("创建菜单失败", e);
            return RestResponse.error("创建菜单失败");
        }
    }

    @PostMapping("/deleteMenu")
    @Operation(summary = "删除菜单接口")
    public RestResponse deleteMenu(@RequestBody Integer menuId) {
        return sysMenuRepoImpl.deleteMenu(menuId);
    }


    @GetMapping("/getMenuList")
    @Operation(summary = "获取菜单列表接口")
    public RestResponse getMenuList(Page<SysMenuEntity> page, SysMenuDto vo) {
        return sysMenuRepo.pageList(page,vo);
    }


    @GetMapping("/getMenuInfo")
    @Operation(summary = "获取菜单信息接口")
    public RestResponse getMenuInfo(Integer menuId) {
        try {
            return RestResponse.success(sysMenuRepo.getById(menuId));
        }catch (Exception e) {
            log.error("获取菜单信息失败", e);
            return RestResponse.error("获取菜单信息失败");
        }

    }


    @PostMapping("/updateMenu")
    @Operation(summary = "更新菜单信息接口")
    public RestResponse updateMenu(SysMenuEntity menu) {
        try {
            sysMenuRepo.updateByRoleId(menu);
            return RestResponse.success("更新菜单信息成功");
        }catch (Exception e) {
            log.error("更新菜单信息失败", e);
            return RestResponse.error("更新菜单信息失败");
        }
    }


    @GetMapping("/getMenuTree")
    @Operation(summary = "获取所有菜单树接口")
    public List<MenuTreeNode> getMenuTree(){
        List<MenuTreeNode> menuTree = sysMenuRepo.getMenuTree();
        return menuTree;
    }

    @GetMapping("/getMenuByRoleId")
    @Operation(summary = "通过角色ID获取菜单接口")
    public List<SysMenuEntity> getMenuByRoleId(Long roleId){
        List<SysMenuEntity> menuTree = sysMenuRepo.getMenuByRoleId(roleId);
        return menuTree;
    }


}
