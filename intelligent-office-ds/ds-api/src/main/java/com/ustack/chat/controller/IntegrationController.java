package com.ustack.chat.controller;

import com.ustack.chat.service.IntegrationService;
import com.ustack.global.common.rest.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: DemoController
 * @Date: 2025-02-18 12:28
 */
@RestController
@RequestMapping("/api/v1/chat/integration")
@Slf4j
@RequiredArgsConstructor
@Validated
public class IntegrationController {

    private final IntegrationService service;

    @PostMapping("/getAiPPTToken")
    public RestResponse getAiPPTToken() throws Exception {
        return service.getAiPPTToken();
    }

    @PostMapping("/getAiPPTCode")
    public RestResponse getAiPPTCode() throws Exception {
        return service.getAiPPTCode();
    }
}
