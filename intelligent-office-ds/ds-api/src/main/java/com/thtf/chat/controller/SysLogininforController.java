package com.thtf.chat.controller;


import com.thtf.chat.repo.ISysLogininforRepo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/LoginInfo/info")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "登录日志", description = "登录日志相关操作")
public class SysLogininforController {

    @Autowired
    private ISysLogininforRepo logininforService;

}
