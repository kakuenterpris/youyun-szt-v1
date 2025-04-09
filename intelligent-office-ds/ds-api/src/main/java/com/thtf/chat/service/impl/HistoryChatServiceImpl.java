package com.thtf.chat.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thtf.chat.entity.MessageSourceEntity;
import com.thtf.chat.enums.ChatApiKeyEnum;
import com.thtf.chat.properties.AiConfigProperties;
import com.thtf.chat.repo.MessageSourceRepo;
import com.thtf.chat.service.HistoryChatService;
import com.thtf.chat.util.HttpUtils;
import com.thtf.dto.HistoryChatDTO;
import com.thtf.dto.ModelChatDto;
import com.thtf.global.common.rest.ContextUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.utils.Linq;
import com.thtf.resource.enums.ResourceErrorCode;
import io.micrometer.core.ipc.http.HttpSender;
import jdk.jfr.Experimental;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author PingY
 * @Classname HistoryChatServiceImpl
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HistoryChatServiceImpl implements HistoryChatService {
    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Autowired
    private MessageSourceRepo messageSourceRepo;

    @Override
    public RestResponse historyChatList(HistoryChatDTO historyChatDTO) {
//        String conversationId = "2d6894a2-a1c2-4214-8a26-d722ce8d9f9f";
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        String lastId = StringUtils.isBlank(historyChatDTO.getLastId()) ? "" : historyChatDTO.getLastId();
        String sortBy = StringUtils.isBlank(historyChatDTO.getSortBy()) ? "-updated_at" : historyChatDTO.getSortBy();
        Integer limit = null == historyChatDTO.getLimit() || 0 == historyChatDTO.getLimit() ? 100 : historyChatDTO.getLimit();
        String url = aiConfigProperties.getConversationsChatApi();
        String param = "user=" + userId + "&sort_by=" + sortBy + "&last_id=" + lastId + "&limit=" + limit;
        url = url + "?" + param;
        String apiKey = "";
        List<Map> list = new ArrayList<>();
        for (ChatApiKeyEnum chatApiKeyEnum : ChatApiKeyEnum.values()) {
            String apiType = chatApiKeyEnum.getType();
            apiKey = chatApiKeyEnum.getKey();
            if (!apiType.equals("recommendList")){
                log.info("【历史会话列表】请求地址：【{}】，api-key为：【{}】", url, apiKey);
                String response = HttpUtils.doGet(url, apiKey);
    //            log.info("【{}】：会话列表响应结果：{}", apiType, response);
                if (StringUtils.isNotBlank(response)) {
                    Gson gson = new Gson();
                    Map map = gson.fromJson(response, Map.class);
                    List<Map> dataList = map.get("data") != null ? (List<Map>) map.get("data") : null;
                    if (null == dataList || dataList.isEmpty()) {
                        continue;
                    }
                    // 新增key为场景字段
                    for (Map map1 : dataList) {
                        map1.put("sceneType", apiType);
                    }
                    list.addAll(dataList);
                }
            }
        }
        // 按照修改时间降序排列
        list.sort(new Comparator<Map>() {
            @Override
            public int compare(Map map1, Map map2) {
                Long time1 = ((Double) map1.get("updated_at")).longValue();
                Long time2 = ((Double) map2.get("updated_at")).longValue();
                return time2.compareTo(time1); // 降序排列
            }
        });
        if (StringUtils.isNotEmpty(historyChatDTO.getQuery())){
            list = Linq.find(list, x -> String.valueOf(x.get("name")).contains(historyChatDTO.getQuery()));
        }
        return RestResponse.success(list);
    }

    @Override
    public RestResponse historyChatListDetail(HistoryChatDTO historyChatDTO) {
        if (StringUtils.isBlank(historyChatDTO.getSceneType())) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "场景类型必传");
        }
        if (StringUtils.isBlank(historyChatDTO.getConversationId())) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "会话ID必传");
        }
        String apiType = historyChatDTO.getSceneType();
        String apiKey = "";
        if (apiType.equals(ChatApiKeyEnum.common.getType())) {
            apiKey = ChatApiKeyEnum.common.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intellcode.getType())) {
            apiKey = ChatApiKeyEnum.intellcode.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.pptoutline.getType())) {
            apiKey = ChatApiKeyEnum.pptoutline.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.newsmanuscript.getType())) {
            apiKey = ChatApiKeyEnum.newsmanuscript.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intelldoc.getType())) {
            apiKey = ChatApiKeyEnum.intelldoc.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intellproofread.getType())) {
            apiKey = ChatApiKeyEnum.intellproofread.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.speechscript.getType())) {
            apiKey = ChatApiKeyEnum.speechscript.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.meetingsammary.getType())) {
            apiKey = ChatApiKeyEnum.meetingsammary.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.dm.getType())) {
            apiKey = ChatApiKeyEnum.dm.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intellreport.getType())) {
            apiKey = ChatApiKeyEnum.intellreport.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.customvector.getType())) {
            apiKey = ChatApiKeyEnum.customvector.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.netsearch.getType())) {
            apiKey = ChatApiKeyEnum.netsearch.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.dataCenter.getType())) {
            apiKey = ChatApiKeyEnum.dataCenter.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.recommendList.getType())) {
            apiKey = ChatApiKeyEnum.recommendList.getKey();
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        String conversationId = StringUtils.isBlank(historyChatDTO.getConversationId()) ? "2d6894a2-a1c2-4214-8a26-d722ce8d9f9f" : historyChatDTO.getConversationId();
        Integer limit = historyChatDTO.getLimit() == null ? 20 : historyChatDTO.getLimit();
        String url = aiConfigProperties.getHistoryChatApi();
        String firstId = "";
        String param = "user=" + userId + "&conversation_id=" + conversationId +
                "&first_id=" + firstId + "&limit=" + limit;
        url = url + "?" + param;
        log.info("【历史会话详情】请求地址：【{}】", url);
        String response = HttpUtils.doGet(url, apiKey);
//        log.info("历史会话列表响应结果：" + response);
        if (StringUtils.isNotBlank(response)) {
            Gson gson = new Gson();
            Map map = gson.fromJson(response, Map.class);
            List<Map<String, Object>> list = (List) map.get("data");
            list.forEach(x -> {
                LambdaQueryWrapper<MessageSourceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(MessageSourceEntity::getConversationId, x.get("conversation_id"));
                lambdaQueryWrapper.eq(MessageSourceEntity::getMessageId, x.get("id"));
                x.put("tracing", messageSourceRepo.list(lambdaQueryWrapper));
            });
            map.put("data", list);
            return RestResponse.success(map);
        }
        return RestResponse.fail(1001, "暂无数据");
    }

    @Override
    public RestResponse deleteConversations(HistoryChatDTO historyChatDTO) {
        if (StringUtils.isBlank(historyChatDTO.getSceneType())) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "场景类型必传");
        }
        if (StringUtils.isBlank(historyChatDTO.getConversationId())) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "会话ID必传");
        }
        String apiType = historyChatDTO.getSceneType();
        String sceneType = "";
        if (apiType.equals(ChatApiKeyEnum.common.getType())) {
            sceneType = ChatApiKeyEnum.common.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intellcode.getType())) {
            sceneType = ChatApiKeyEnum.intellcode.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.pptoutline.getType())) {
            sceneType = ChatApiKeyEnum.pptoutline.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.newsmanuscript.getType())) {
            sceneType = ChatApiKeyEnum.newsmanuscript.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intelldoc.getType())) {
            sceneType = ChatApiKeyEnum.intelldoc.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intellproofread.getType())) {
            sceneType = ChatApiKeyEnum.intellproofread.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.speechscript.getType())) {
            sceneType = ChatApiKeyEnum.speechscript.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.meetingsammary.getType())) {
            sceneType = ChatApiKeyEnum.meetingsammary.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.dm.getType())) {
            sceneType = ChatApiKeyEnum.dm.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.intellreport.getType())) {
            sceneType = ChatApiKeyEnum.intellreport.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.customvector.getType())) {
            sceneType = ChatApiKeyEnum.customvector.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.netsearch.getType())) {
            sceneType = ChatApiKeyEnum.netsearch.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.dataCenter.getType())) {
            sceneType = ChatApiKeyEnum.dataCenter.getKey();
        } else if (apiType.equals(ChatApiKeyEnum.recommendList.getType())) {
            sceneType = ChatApiKeyEnum.recommendList.getKey();
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? "abc-123" : ContextUtil.getUserId();
        String conversationId = historyChatDTO.getConversationId();
        String url = aiConfigProperties.getDeleteConversationsApi();
        Map map = new HashMap();
        map.put("user", userId);
        log.info("【历史会话删除】请求地址：【{}】", url);
        String response = HttpUtils.doDelete(url, conversationId, map, sceneType);
        log.info("历史会话删除响应结果：" + response);
        if (StringUtils.isNotBlank(response)) {
            Gson gson = new Gson();
            Map map1 = gson.fromJson(response, Map.class);
            return RestResponse.success(map1);
        }
        return RestResponse.success("");
    }
}
