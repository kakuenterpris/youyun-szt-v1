package com.thtf.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.page.PageParams;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.repo.BusUserInfoRepo;
import com.thtf.chat.repo.SysMenuRepo;
import com.thtf.chat.repo.SysRoleRepo;
import com.thtf.chat.service.BusUserInfoService;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.login.dto.UserAccountDTO;

import com.thtf.access.dto.SysRoleDto;
import com.thtf.access.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/access")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AccessController {
    @Autowired
    private SysRoleRepo sysRoleRepo;
    @Autowired
    private SysMenuRepo sysMenuRepo;

    @Autowired
    private BusUserInfoService busUserInfoService;


//    用户管理
    //todo 创建用户
    public RestResponse createUser(UserInfoDto user) {
        return busUserInfoService.addUser(user);
    }

    //todo 删除用户（逻辑删除）
    public RestResponse deleteUser(Integer userId) {
        try {
            busUserInfoService.removeById(userId);
        }catch (Exception e) {
            log.error("删除用户失败", e);
            return RestResponse.error("删除用户失败");
        }
        return RestResponse.success("删除用户成功");
    }

    //todo 获取用户列表
    public RestResponse getUserList(Page<UserInfoDto> page,UserInfoVO vo) {
        Page<UserInfoVO> list=busUserInfoService.pageList(page,vo);
        return RestResponse.success(list);
    }

    //todo 获取用户信息
    public RestResponse getUserInfo(Integer userId) {
        BusUserInfoEntity byId = busUserInfoService.getById(userId);
        return RestResponse.success(byId);
        // 实现获取用户信息的逻辑
    }
    //todo 更新用户信息（包括角色）
    public RestResponse updateUser(@Validated UserInfoDto user) {
        return RestResponse.success("更新用户信息成功");
        // 实现更新用户信息的逻辑
    }
    //todo 获取用户权限
    public RestResponse getUserPermissions(Integer userId) {
        return RestResponse.success("获取用户权限成功");
        // 实现获取用户权限的逻辑
    }
    //todo 更新用户权限
    public RestResponse updateUserPermissions(Integer userId, List<SysRoleDto> permissionIds) {
        return RestResponse.success("更新用户权限成功");
        // 实现更新用户权限的逻辑
    }

//    角色管理
    //todo 创建角色
    public RestResponse createRole(SysRoleEntity role) {
        return RestResponse.success("创建角色成功");
        // 实现创建角色的逻辑
    }
    //todo 删除角色（逻辑删除）
    public RestResponse deleteRole(Integer roleId) {
        return RestResponse.success("删除角色成功");
        // 实现删除角色的逻辑
    }
    //todo 获取角色列表
    public RestResponse getRoleList() {
        return RestResponse.success("获取角色列表成功");
        // 实现获取角色列表的逻辑
    }
    //todo 获取角色信息
    public RestResponse getRoleInfo(Integer roleId) {
        return RestResponse.success("获取角色信息成功");
        // 实现获取角色信息的逻辑
    }
    //todo 更新角色信息
    public RestResponse updateRole(SysRoleEntity role) {
        return RestResponse.success("更新角色信息成功");
        // 实现更新角色信息的逻辑
    }
    //todo 获取角色菜单
    public RestResponse getRoleMenus(Integer roleId) {
        return RestResponse.success("获取角色菜单成功");
        // 实现获取角色菜单的逻辑
    }
    //todo 更新角色权限
    public RestResponse updateRolePermissions(Integer roleId, List<SysRoleDto> permissionIds) {
        return RestResponse.success("更新角色权限成功");
        // 实现更新角色权限的逻辑
    }
//    菜单管理
    //todo 创建菜单
    public RestResponse createMenu(SysRoleEntity menu) {
        return RestResponse.success("创建菜单成功");
        // 实现创建菜单的逻辑
    }
    //todo 删除菜单（逻辑删除）
    public RestResponse deleteMenu(Integer menuId) {
        return RestResponse.success("删除菜单成功");
        // 实现删除菜单的逻辑
    }
    //todo 获取菜单列表
    public RestResponse getMenuList() {
        return RestResponse.success("获取菜单列表成功");
        // 实现获取菜单列表的逻辑
    }
    //todo 获取菜单信息
    public RestResponse getMenuInfo(Integer menuId) {
        return RestResponse.success("获取菜单信息成功");
        // 实现获取菜单信息的逻辑
    }
    //todo 更新菜单信息
    public RestResponse updateMenu(SysRoleEntity menu) {
        return RestResponse.success("更新菜单信息成功");
        // 实现更新菜单信息的逻辑
    }

}
