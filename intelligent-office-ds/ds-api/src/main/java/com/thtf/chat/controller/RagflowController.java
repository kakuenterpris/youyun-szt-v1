package com.thtf.chat.controller;


import com.thtf.chat.entity.RagflowEntity;
import com.thtf.chat.properties.RagflowConfigProperties;
import com.thtf.global.common.http.clients.OkHttpClientUtil;
import com.thtf.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ragflow")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "ragflow", description = "ragflow相关操作")
public class RagflowController {

    @Autowired
    private OkHttpClientUtil okHttpClientUtil;

    @Autowired
    private RagflowConfigProperties ragflowConfigProperties;
    /**
     * 注册用户
     */
    @PostMapping("/register")
    @Operation(summary = "注册用户", description = "注册用户")
    public RestResponse registerUser(RagflowEntity ragflowEntity) {
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        String url = ragflowConfigProperties.getCommonUrl() + "/user/register";
        okHttpClientUtil.doPost(url, params, headers);

        return RestResponse.success("注册成功");
    }

}
