package com.ustack.chat.util;

import com.ustack.chat.properties.ApikeyConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckUtil {

    @Autowired
    private ApikeyConfigProperties apikeyConfigProperties;

    /**
     * 获取api-key
     * @param apiType
     * @return
     */
    public String getApiKey(String apiType) {
        if (StringUtils.isBlank(apiType)) {
            return "";
        }
        String apiKey = "";
        if ("common".equals(apiType)) {
            apiKey = apikeyConfigProperties.getCommon();
        } else if ("intellcode".equals(apiType)) {
            apiKey = apikeyConfigProperties.getIntellcode();
        } else if ("pptoutline".equals(apiType)) {
            apiKey = apikeyConfigProperties.getPptoutline();
        } else if ("newsmanuscript".equals(apiType)) {
            apiKey = apikeyConfigProperties.getNewsmanuscript();
        } else if ("intelldoc".equals(apiType)) {
            apiKey = apikeyConfigProperties.getIntelldoc();
        } else if ("intellproofread".equals(apiType)) {
            apiKey = apikeyConfigProperties.getIntellproofread();
        } else if ("speechscript".equals(apiType)) {
            apiKey = apikeyConfigProperties.getSpeechscript();
        } else if ("meetingsammary".equals(apiType)) {
            apiKey = apikeyConfigProperties.getMeetingsammary();
        } else if ("dm".equals(apiType)) {
            apiKey = apikeyConfigProperties.getDm();
        } else if ("intellreport".equals(apiType)) {
            apiKey = apikeyConfigProperties.getIntellreport();
        } else if ("customvector".equals(apiType)) {
            apiKey = apikeyConfigProperties.getCustomvector();
        } else if ("netsearch".equals(apiType)) {
            apiKey = apikeyConfigProperties.getNetsearch();
        } else if ("dataCenter".equals(apiType)) {
            apiKey = apikeyConfigProperties.getDataCenter();
        } else if ("recommendList".equals(apiType)) {
            apiKey = apikeyConfigProperties.getRecommendList();

        }
        return apiKey;
    }


}
