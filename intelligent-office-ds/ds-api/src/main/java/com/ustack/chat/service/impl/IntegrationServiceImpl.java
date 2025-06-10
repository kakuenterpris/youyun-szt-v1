package com.ustack.chat.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ustack.chat.properties.AiPPTProperties;
import com.ustack.chat.service.IntegrationService;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: TestService
 * @Date: 2025-02-17 23:57
 */
@Service
@RequiredArgsConstructor
public class IntegrationServiceImpl implements IntegrationService {
    private final AiPPTProperties aiPPTProperties;
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse getAiPPTToken() throws Exception {
        return RestResponse.success(this.callAiPPTApi(aiPPTProperties.getTokenStringToSign(), aiPPTProperties.getTokenApi()));
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse getAiPPTCode() throws Exception {
        return RestResponse.success(this.callAiPPTApi(aiPPTProperties.getCodeStringToSign(), aiPPTProperties.getCodeApi()));
    }

    private Object callAiPPTApi(String stringToSign, String api) throws Exception {
        SystemUser currentUser = ContextUtil.currentUser();

        long currentTimeMillis = System.currentTimeMillis();

        String data = stringToSign + currentTimeMillis;

        String signature = genHmac(data, aiPPTProperties.getSecretKey());

        HttpResponse res = HttpRequest.get(String.format(api, currentUser.getLoginId(), ""))
                .header("x-api-key", aiPPTProperties.getApiKey())
                .header("x-timestamp", String.valueOf(currentTimeMillis))
                .header("x-signature",signature)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)){
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()){
            return null;
        }
        return parse.getData();
    }

    private static String genHmac(String data, String key) throws Exception {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}
