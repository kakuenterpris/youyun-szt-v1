package com.thtf.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.chat.entity.BusUserInfoEntity;
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
}
