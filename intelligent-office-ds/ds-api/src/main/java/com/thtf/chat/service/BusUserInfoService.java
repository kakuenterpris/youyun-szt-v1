package com.thtf.chat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.access.vo.UserInfoVO;
import com.thtf.chat.dto.UpdateUserInfoDto;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.login.dto.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface BusUserInfoService extends IService<BusUserInfoEntity> {

    /**
     * 登录认证
     * @param loginDTO 登录信息
     */
    RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO loginDTO);

    RestResponse addUser(UserInfoDto user);

    Page<UserInfoVO> pageList(Page<UserInfoDto> pages, UserInfoVO dto);

    RestResponse updateByUserId(UpdateUserInfoDto user);

    RestResponse unlockUser(Integer userId, Boolean unlock);

    public SysRoleEntity getRoleByUserId();
}
