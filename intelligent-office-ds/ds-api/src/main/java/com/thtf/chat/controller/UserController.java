package com.thtf.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.chat.dto.UpdateUserInfoDto;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.repo.SysUserRoleRepo;
import com.thtf.chat.service.BusUserInfoService;
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


    //    用户管理
    //todo 创建用户
    @PostMapping("/createUser")
    @Operation(summary = "创建用户接口")
    public RestResponse createUser(UserInfoDto user) {
        return busUserInfoService.addUser(user);
    }

    //todo 删除用户（逻辑删除）
    @PostMapping("/deleteUser")
    @Operation(summary = "删除用户接口")
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
    @GetMapping("/getUserList")
    @Operation(summary = "获取用户列表接口")
    public RestResponse getUserList(Page<UserInfoDto> page,UserInfoVO vo) {
        Page<UserInfoVO> list=busUserInfoService.pageList(page,vo);
        return RestResponse.success(list);
    }

    //todo 获取用户信息
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息接口")
    public RestResponse getUserInfo(Integer userId) {
        BusUserInfoEntity byId = busUserInfoService.getById(userId);
        return RestResponse.success(byId);
        // 实现获取用户信息的逻辑
    }

    //todo 更新用户信息（包括角色）
    @PostMapping("/updateUser")
    @Operation(summary = "更新用户信息（包括角色）接口")
    public RestResponse updateUser(@RequestBody UpdateUserInfoDto user) {
        return busUserInfoService.updateByUserId(user);
    }

    //todo 获取用户权限
    @GetMapping("/getUserPermissions")
    @Operation(summary = "获取用户权限接口")
    public RestResponse getUserPermissions(Integer userId) {
      return sysUserRoleRepo.getUserPermissions(userId);
    }


}
