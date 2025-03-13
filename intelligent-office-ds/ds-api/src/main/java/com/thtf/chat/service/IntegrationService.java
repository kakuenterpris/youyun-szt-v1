package com.thtf.chat.service;

import com.thtf.global.common.rest.RestResponse;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: TestService
 * @Date: 2025-02-17 23:57
 */
public interface IntegrationService {

    RestResponse getAiPPTToken() throws Exception;

    RestResponse getAiPPTCode() throws Exception;
}
