package com.ustack.chat.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.page.PageParams;
import com.ustack.access.dto.UserInfoDto;
import com.ustack.access.vo.UserInfoVO;
import com.ustack.chat.dto.AssignRolesDTO;
import com.ustack.chat.dto.MenuTreeNode;
import com.ustack.chat.dto.UpdateRoleDto;
import com.ustack.chat.dto.UpdateUserInfoDto;
import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.SysUserRoleEntity;
import com.ustack.chat.repo.BusUserInfoRepo;
import com.ustack.chat.repo.SysMenuRepo;
import com.ustack.chat.repo.SysRoleMenuRepo;
import com.ustack.chat.repo.SysRoleRepo;
import com.ustack.chat.repo.SysUserRoleRepo;
import com.ustack.chat.service.BusUserInfoService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.login.dto.UserAccountDTO;

import com.ustack.access.dto.SysRoleDto;
import com.ustack.access.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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

    @Autowired
    private SysUserRoleRepo sysUserRoleRepo;

    @Autowired
    private SysRoleMenuRepo sysRoleMenuRepo;


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
    public RestResponse updateUser(@Validated UpdateUserInfoDto user) {
        return busUserInfoService.updateByUserId(user);
    }

    //todo 获取用户权限
    public RestResponse getUserPermissions(Integer userId) {
      return sysUserRoleRepo.getUserPermissions(userId);
    }

    // todo 角色管理
    //todo 创建角色
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
    public RestResponse getRoleList(Page<SysRoleEntity> page, SysRoleDto vo) {
        return sysRoleRepo.pageList(page,vo);
    }

    //todo 获取角色信息
    public RestResponse getRoleInfo(Integer roleId) {
        try {
            return RestResponse.success(sysRoleRepo.getById(roleId));
        }catch (Exception e) {
            log.error("获取角色信息失败", e);
            return RestResponse.error("获取角色信息失败");
        }
    }
    //todo 更新角色信息
    public RestResponse updateRole(UpdateRoleDto role) {
        try {
            sysRoleRepo.updateByRoleId(role);
            return RestResponse.success("更新角色信息成功");
        }catch (Exception e) {
            log.error("更新角色信息失败", e);
            return RestResponse.error("更新角色信息失败");
        }
    }
    //todo 获取角色菜单id
    public RestResponse getRoleMenus(Integer roleId) {
        return sysRoleMenuRepo.getByRoleId(roleId);
    }

//   todo 菜单管理
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


    /***
     * 获取所有菜单树
     * @return
     */
    public List<MenuTreeNode> getMenuTree(){
        List<MenuTreeNode> menuTree = sysMenuRepo.getMenuTree();
        return menuTree;
    }

}
