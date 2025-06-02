package com.thtf.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.chat.annotation.RequiresPermission;
import com.thtf.chat.dto.UpdateUserInfoDto;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.repo.SysUserRoleRepo;
import com.thtf.chat.service.BusUserInfoService;
import com.thtf.global.common.dto.BusUserInfoDTO;
import com.thtf.global.common.rest.RestResponse;
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
@RequiresPermission(value="UserManage",authtype = 0)
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
    @Operation(summary = "获取用户列表接口")
    public RestResponse getUserList(Page<UserInfoDto> page,UserInfoVO vo) {
        Page<UserInfoVO> list=busUserInfoService.pageList(page,vo);
        return RestResponse.success(list);
    }


    @GetMapping("/getAllUserList")
    @Operation(summary = "获取所有用户接口")
    public RestResponse getAllUserList() {
        List<BusUserInfoEntity> list = busUserInfoService.list();
        return RestResponse.success(list);
    }


    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息接口")
    public RestResponse getUserInfo(Integer userId) {
        BusUserInfoEntity byId = busUserInfoService.getById(userId);
        return RestResponse.success(byId);
        // 实现获取用户信息的逻辑
    }


    @PostMapping("/updateUser")
    @Operation(summary = "更新用户信息（包括角色）接口")
    public RestResponse updateUser(@RequestBody UpdateUserInfoDto user) {
        return busUserInfoService.updateByUserId(user);
    }


    @GetMapping("/getUserPermissions")
    @Operation(summary = "获取用户权限接口")
    public RestResponse getUserPermissions(Integer userId) {
      return sysUserRoleRepo.getUserPermissions(userId);
    }


    @PostMapping("/lockOrUnlockUser")
    @Operation(summary = "获取用户权限接口")
    public RestResponse unlockUser(@RequestBody UserInfoDto dto) {

        return busUserInfoService.unlockUser(dto.getId(), dto.getUnlock());
    }


}
