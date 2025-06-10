package com.ustack.chat.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.chat.entity.MessageSourceEntity;
import com.ustack.chat.enums.ChatApiKeyEnum;
import com.ustack.chat.properties.AiConfigProperties;
import com.ustack.chat.properties.ApikeyConfigProperties;
import com.ustack.chat.repo.LikeOrDislikeRepo;
import com.ustack.chat.repo.MessageSourceRepo;
import com.ustack.chat.service.HistoryChatService;
import com.ustack.chat.util.CheckUtil;
import com.ustack.chat.util.DateUtil;
import com.ustack.chat.util.HttpUtils;
import com.ustack.dto.HistoryChatDTO;
import com.ustack.dto.ModelChatDto;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import com.ustack.global.common.utils.Linq;
import com.ustack.resource.enums.ResourceErrorCode;
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

    @Autowired
    private LikeOrDislikeRepo likeOrDislikeRepo;

    @Autowired
    private ApikeyConfigProperties apikeyConfigProperties;

    private final CheckUtil checkUtil;


    @Override
    public RestResponse historyChatList(HistoryChatDTO historyChatDTO) {
        try {
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
                apiKey = checkUtil.getApiKey(apiType);
//                apiKey = chatApiKeyEnum.getKey();
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
                            Long timestamp = ((Double) map1.get("updated_at")).longValue() * 1000;
                            map1.put("timeRange",DateUtil.getTimeRangeNew(timestamp));
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

            // 最多返回300条数据
            int limits = Math.min(list.size(), 300);
            list = list.subList(0, limits);

            return RestResponse.success(list,limits);
        } catch (Exception e) {
            log.info("【历史会话列表】错误日志：【{}】，异常为：【{}】", JsonUtil.toJson(historyChatDTO), JsonUtil.toJson(e));
            throw new RuntimeException(e);
        }
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
        String apiKey = checkUtil.getApiKey(apiType);
//        if (apiType.equals(ChatApiKeyEnum.common.getType())) {
//            apiKey = apikeyConfigProperties.getCommon();
//        } else if (apiType.equals(ChatApiKeyEnum.intellcode.getType())) {
//            apiKey = apikeyConfigProperties.getIntellcode();
//        } else if (apiType.equals(ChatApiKeyEnum.pptoutline.getType())) {
//            apiKey = apikeyConfigProperties.getPptoutline();
//        } else if (apiType.equals(ChatApiKeyEnum.newsmanuscript.getType())) {
//            apiKey = apikeyConfigProperties.getNewsmanuscript();
//        } else if (apiType.equals(ChatApiKeyEnum.intelldoc.getType())) {
//            apiKey = apikeyConfigProperties.getIntelldoc();
//        } else if (apiType.equals(ChatApiKeyEnum.intellproofread.getType())) {
//            apiKey = apikeyConfigProperties.getIntellproofread();
//        } else if (apiType.equals(ChatApiKeyEnum.speechscript.getType())) {
//            apiKey = apikeyConfigProperties.getSpeechscript();
//        } else if (apiType.equals(ChatApiKeyEnum.meetingsammary.getType())) {
//            apiKey = apikeyConfigProperties.getMeetingsammary();
//        } else if (apiType.equals(ChatApiKeyEnum.dm.getType())) {
//            apiKey = apikeyConfigProperties.getDm();
//        } else if (apiType.equals(ChatApiKeyEnum.intellreport.getType())) {
//            apiKey = apikeyConfigProperties.getIntellreport();
//        } else if (apiType.equals(ChatApiKeyEnum.customvector.getType())) {
//            apiKey = apikeyConfigProperties.getCustomvector();
//        } else if (apiType.equals(ChatApiKeyEnum.netsearch.getType())) {
//            apiKey = apikeyConfigProperties.getNetsearch();
//        } else if (apiType.equals(ChatApiKeyEnum.dataCenter.getType())) {
//            apiKey = apikeyConfigProperties.getDataCenter();
//        } else if (apiType.equals(ChatApiKeyEnum.recommendList.getType())) {
//            apiKey = apikeyConfigProperties.getRecommendList();
//        }
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
                // 添加历史记录的点赞点踩状态
                LambdaQueryWrapper<LikeOrDislikeEntity> lambdaQuery = new LambdaQueryWrapper<>();
                lambdaQuery.eq(LikeOrDislikeEntity::getUserId, ContextUtil.getUserId());
                lambdaQuery.eq(LikeOrDislikeEntity::getConversationId, x.get("conversation_id"));
                lambdaQuery.eq(LikeOrDislikeEntity::getMessageId, x.get("id"));
                LikeOrDislikeEntity likeOrDislikeEntity = likeOrDislikeRepo.getOne(lambdaQuery);
                x.put("likeStatus", likeOrDislikeEntity==null?0:likeOrDislikeEntity.getLikeStatus());
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
        String sceneType = checkUtil.getApiKey(apiType);;
//        if (apiType.equals(ChatApiKeyEnum.common.getType())) {
//            sceneType = apikeyConfigProperties.getCommon();
//        } else if (apiType.equals(ChatApiKeyEnum.intellcode.getType())) {
//            sceneType = apikeyConfigProperties.getIntellcode();
//        } else if (apiType.equals(ChatApiKeyEnum.pptoutline.getType())) {
//            sceneType = apikeyConfigProperties.getPptoutline();
//        } else if (apiType.equals(ChatApiKeyEnum.newsmanuscript.getType())) {
//            sceneType = apikeyConfigProperties.getNewsmanuscript();
//        } else if (apiType.equals(ChatApiKeyEnum.intelldoc.getType())) {
//            sceneType = apikeyConfigProperties.getIntelldoc();
//        } else if (apiType.equals(ChatApiKeyEnum.intellproofread.getType())) {
//            sceneType = apikeyConfigProperties.getIntellproofread();
//        } else if (apiType.equals(ChatApiKeyEnum.speechscript.getType())) {
//            sceneType = apikeyConfigProperties.getSpeechscript();
//        } else if (apiType.equals(ChatApiKeyEnum.meetingsammary.getType())) {
//            sceneType = apikeyConfigProperties.getMeetingsammary();
//        } else if (apiType.equals(ChatApiKeyEnum.dm.getType())) {
//            sceneType = apikeyConfigProperties.getDm();
//        } else if (apiType.equals(ChatApiKeyEnum.intellreport.getType())) {
//            sceneType = apikeyConfigProperties.getIntellreport();
//        } else if (apiType.equals(ChatApiKeyEnum.customvector.getType())) {
//            sceneType = apikeyConfigProperties.getCustomvector();
//        } else if (apiType.equals(ChatApiKeyEnum.netsearch.getType())) {
//            sceneType = apikeyConfigProperties.getNetsearch();
//        } else if (apiType.equals(ChatApiKeyEnum.dataCenter.getType())) {
//            sceneType = apikeyConfigProperties.getDataCenter();
//        } else if (apiType.equals(ChatApiKeyEnum.recommendList.getType())) {
//            sceneType = apikeyConfigProperties.getRecommendList();
//        }
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
