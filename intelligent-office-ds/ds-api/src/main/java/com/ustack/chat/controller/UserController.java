package com.ustack.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.access.dto.UserInfoDto;
import com.ustack.access.vo.UserInfoVO;
import com.ustack.chat.annotation.RequiresPermission;
import com.ustack.chat.dto.UpdateUserInfoDto;
import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.chat.repo.SysUserRoleRepo;
import com.ustack.chat.service.BusUserInfoService;
import com.ustack.global.common.dto.BusUserInfoDTO;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

    @Autowired
    private BusUserInfoService busUserInfoService;

    @Autowired
    private SysUserRoleRepo sysUserRoleRepo;

    @PostMapping("/createUser")
    @Operation(summary = "创建用户接口")
    public RestResponse createUser(UserInfoDto user) {
        return busUserInfoService.addUser(user);
    }


    @PostMapping("/deleteUser")
    @RequiresPermission(value="UserManage",authtype = 1)
    @Operation(summary = "删除用户接口")
    public RestResponse deleteUser(@RequestBody Integer userId) {
        try {
            busUserInfoService.removeById(userId);
        }catch (Exception e) {
            log.error("删除用户失败", e);
            return RestResponse.error("删除用户失败");
        }
        return RestResponse.success("删除用户成功");
    }


    @GetMapping("/getUserList")
    @RequiresPermission(value="UserManage",authtype = 0)
    @Operation(summary = "获取用户列表接口")
    public RestResponse getUserList(Page<UserInfoDto> page,UserInfoVO vo) {
        Page<UserInfoVO> list=busUserInfoService.pageList(page,vo);
        return RestResponse.success(list);
    }


    @GetMapping("/getAllUserList")
    @RequiresPermission(value="UserManage",authtype = 0)
    @Operation(summary = "获取所有用户接口")
    public RestResponse getAllUserList() {
        List<BusUserInfoEntity> list = busUserInfoService.list();
        return RestResponse.success(list);
    }


    @GetMapping("/getUserInfo")
    @RequiresPermission(value="UserManage",authtype = 0)
    @Operation(summary = "获取用户信息接口")
    public RestResponse getUserInfo(Integer userId) {
        BusUserInfoEntity byId = busUserInfoService.getById(userId);
        return RestResponse.success(byId);
        // 实现获取用户信息的逻辑
    }


    @PostMapping("/updateUser")
    @RequiresPermission(value="UserManage",authtype = 1)
    @Operation(summary = "更新用户信息（包括角色）接口")
    public RestResponse updateUser(@RequestBody UpdateUserInfoDto user) {
        return busUserInfoService.updateByUserId(user);
    }


    @GetMapping("/getUserPermissions")
    @RequiresPermission(value="UserManage",authtype = 0)
    @Operation(summary = "获取用户权限接口")
    public RestResponse getUserPermissions(Integer userId) {
      return sysUserRoleRepo.getUserPermissions(userId);
    }


    @PostMapping("/lockOrUnlockUser")
    @RequiresPermission(value="UserManage",authtype = 0)
    @Operation(summary = "获取用户权限接口")
    public RestResponse unlockUser(@RequestBody UserInfoDto dto) {

        return busUserInfoService.unlockUser(dto.getId(), dto.getUnlock());
    }


}
