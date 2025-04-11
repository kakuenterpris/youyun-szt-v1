package com.thtf.chat.service;

import com.thtf.global.common.rest.RestResponse;
import com.thtf.login.dto.FwoaLoginInfoDTO;
import com.thtf.login.dto.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: TestService
 * @Date: 2025-02-17 23:57
 */
public interface LoginService {

    RestResponse syncUserInfo(String id);

    RestResponse syncDepInfo(String id);

    RestResponse syncSubCompanyInfo(String id);

    RestResponse encryptLoginId(String id);

    RestResponse login(HttpServletRequest request, HttpServletResponse response, LoginDTO dto);

    RestResponse getUserInfo(HttpServletRequest request);

    void verifyFromTfoa(HttpServletRequest request, HttpServletResponse response, String userName) throws IOException;

    RestResponse verifyFromTfoaPc(HttpServletRequest request, HttpServletResponse response, String userName) throws IOException;

    RestResponse verifyFromTfoaMobile(HttpServletRequest request, HttpServletResponse response, FwoaLoginInfoDTO param) throws Exception;
}
