package com.ustack.chat.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.chat.entity.MessageSourceEntity;
import com.ustack.chat.enums.ChatApiKeyEnum;
import com.ustack.chat.properties.AiConfigProperties;
import com.ustack.chat.properties.ApikeyConfigProperties;
import com.ustack.chat.properties.DatasetsConfigProperties;
import com.ustack.chat.repo.LikeOrDislikeRepo;
import com.ustack.chat.repo.BusResourceDatasetRepo;
import com.ustack.chat.repo.MessageSourceRepo;
import com.ustack.chat.service.ChatService;
import com.ustack.chat.service.HistoryChatService;
import com.ustack.chat.service.RelUserResourceService;
import com.ustack.chat.util.CheckUtil;
import com.ustack.dto.*;
import com.ustack.global.common.exception.CustomException;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import com.ustack.resource.dto.BusResourceDatasetDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2025年02月18日
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Autowired
    private RelUserResourceService relUserResourceService;

    @Autowired
    private MessageSourceRepo messageSourceRepo;

    @Autowired
    private DatasetsConfigProperties datasetsConfigProperties;

    @Autowired
    @Qualifier("queryExecutorService")
    private ExecutorService queryExecutorService;
    private final BusResourceDatasetRepo datasetRepo;

    @Autowired
    private LikeOrDislikeRepo likeOrDislikeRepo;

    @Autowired
    private ApikeyConfigProperties apikeyConfigProperties;

    @Autowired
    private HistoryChatService historyChatService;

    private final CheckUtil checkUtil;

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    private static String userId = "abc-123"; // Consider moving to configuration

    /**
     * 问答接口
     *
     * @param chatRequestDto
     * @return
     */
    @Override
    public SseEmitter common(ChatRequestDto chatRequestDto) {
        if (chatRequestDto.getQuestion() == null||chatRequestDto.getQuestion().trim().isEmpty()) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "参数错误");
        }
        // 根据场景类型获取api-key
        String apiKey = checkUtil.getApiKey(chatRequestDto.getSceneType());
        //String apiKey = ChatApiKeyEnum.getKey(chatRequestDto.getSceneType());
        List<MessageSourceEntity> messageSourceEntities = new ArrayList<>();
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }

        String question = chatRequestDto.getQuestion();

        // 查询向量库
        ModelInputChatDto modelInputChatDto = prepareModelInput(chatRequestDto,messageSourceEntities);
        // 上传文件处理
        List<ModelFileChatDto> modelFileChatDtoList = inputFileHandler(chatRequestDto.getFiles());
        chatRequestDto.setQuestion(question);
        SseEmitter emitter = new SseEmitter((long) Integer.MAX_VALUE); // 设置超时时间
        CompletableFuture.runAsync(() -> {
            Response response = null;
            try {
                response = this.chatQuery(chatRequestDto, apiKey, modelInputChatDto, modelFileChatDtoList);
                if (response != null) {

                    BufferedSource source = response.body().source();

                    Boolean available = true;
                    while (!source.exhausted()) {
                        String line = source.readUtf8Line();
                        if(available){
                            available= false;
                            Map<String, Object> linemap = JSONUtil.toBean( "{"+line+"}", Map.class);
                            Map linemap1 = (Map) linemap.get("data");
//                        Map<String, String> linemap2 = JSONUtil.toBean( linemap1, Map.class);
                            String conversation_id = (String) linemap1.get("conversation_id");
                            String message_id = (String) linemap1.get("message_id");
                            // 将会话id和消息id存入数据库，用于点赞点踩的初始化
                            LikeOrDislikeEntity likeOrDislikeEntity = new LikeOrDislikeEntity();
                            likeOrDislikeEntity.setConversationId(conversation_id);
                            likeOrDislikeEntity.setMessageId(message_id);
                            likeOrDislikeEntity.setUserId(ContextUtil.getUserId());
                            likeOrDislikeEntity.setLikeStatus(0);
                            likeOrDislikeRepo.save(likeOrDislikeEntity);
                            for (MessageSourceEntity messageSourceEntity : messageSourceEntities) {
                                messageSourceEntity.setMessageId(message_id);
                                messageSourceEntity.setConversationId(conversation_id);
                            }
                            // 批量插入
                            messageSourceRepo.batchInsert(messageSourceEntities);
                            // 合并相同messageId的context（新增合并逻辑）
                            List<MessageSourceEntity> mergedList = messageSourceEntities.stream()
                                    .filter(e -> e.getMessageId() != null)
                                    .collect(Collectors.groupingBy(
                                            e -> "netVector".equals(e.getSource()) ?
                                                    UUID.randomUUID().toString() : // 为 netVector 生成唯一分组键
                                                    e.getTitle(),
                                            Collectors.collectingAndThen(
                                                    Collectors.toList(),
                                                    group -> {
                                                        // 当来源是 netVector 时直接返回第一个元素（不合并）
                                                        if ("netVector".equals(group.get(0).getSource())) {
                                                            return group.get(0);
                                                        }
                                                        MessageSourceEntity merged = new MessageSourceEntity();
                                                        // 复制第一个实体的基础信息
                                                        MessageSourceEntity first = group.get(0);
                                                        merged.setMessageId(first.getMessageId());
                                                        merged.setConversationId(first.getConversationId());
                                                        merged.setSource(first.getSource());
                                                        merged.setSegmentId(first.getSegmentId());
                                                        merged.setDocumentId(first.getDocumentId());
                                                        merged.setKeyword(first.getKeyword());
                                                        merged.setTitle(first.getTitle());

                                                        // 拼接上下文内容
                                                        String combinedContext = group.stream()
                                                                .map(MessageSourceEntity::getContext)
                                                                .filter(Objects::nonNull)
                                                                .collect(Collectors.joining("=##="));
                                                        merged.setContext(combinedContext);
                                                        return merged;
                                                    }
                                            )
                                    ))
                                    .values().stream().collect(Collectors.toList());
                            // 发送来源
                            emitter.send(SseEmitter.event().data(convertChineseToUnicode(JsonUtil.toJson(mergedList))));
                        }
                        //  发送对话内容
                        if (line != null && !line.isEmpty()) {
                            emitter.send(SseEmitter.event().data(line));
                        }
                    }
                }else {
                    String errorMessage = "大模型未响应!!!";
                    emitter.send(SseEmitter.event().data(errorMessage));
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
                throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "对话中断或错误");
            } finally {
                emitter.complete();
                if (response!= null) {
                    response.close();
                }
            }
        }, queryExecutorService);

//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            Response response = this.chatQuery(chatRequestDto, apiKey, modelInputChatDto, modelFileChatDtoList);
//            BufferedSource source = response.body().source();
//            try {
//                Boolean available = true;
//                while (!source.exhausted()) {
//                    String line = source.readUtf8Line();
//                    if(available){
//                        available= false;
//                        Map<String, Object> linemap = JSONUtil.toBean( "{"+line+"}", Map.class);
//                        Map linemap1 = (Map) linemap.get("data");
////                        Map<String, String> linemap2 = JSONUtil.toBean( linemap1, Map.class);
//                        String conversation_id = (String) linemap1.get("conversation_id");
//                        String message_id = (String) linemap1.get("message_id");
//                        for (MessageSourceEntity messageSourceEntity : messageSourceEntities) {
//                            messageSourceEntity.setMessageId(message_id);
//                            messageSourceEntity.setConversationId(conversation_id);
//                            messageSourceRepo.save(messageSourceEntity);
//                        }
//                        emitter.send(SseEmitter.event().data(messageSourceEntities));
//                    }
//                    //  发送来源
//                    if (line != null && !line.isEmpty()) {
//
//                        emitter.send(SseEmitter.event().data(line));
//                    }
//                }
//            } catch (IOException e) {
//                emitter.completeWithError(e);
//                throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "对话中断或错误");
//            } finally {
//                emitter.complete();
//                response.close();
//            }
//        });

        return emitter;
    }

    private String convertChineseToUnicode(String toString) {
        StringBuilder resultBuilder = new StringBuilder();
        for (char c : toString.toCharArray()) {
            if (isChinese(c)) {
                // 如果是中文字符，转换为 Unicode 转义序列
                resultBuilder.append(String.format("\\u%04x", (int) c));
            } else {
                // 否则直接添加字符
                resultBuilder.append(c);
            }
        }
        return resultBuilder.toString();
    }

    public static boolean isChinese(char c) {
        // 中文字符通常在 \u4E00 到 \u9FFF 范围内
        return c >= '\u4E00' && c <= '\u9FFF';
    }

    /**
     * 文件入参处理
     *
     * @param files
     * @return
     */
    private List<ModelFileChatDto> inputFileHandler(List<ChatFileRequestDto> files) {
        List<ModelFileChatDto> modelFileChatDtoList = new ArrayList<>();
        if (files == null || files.size() == 0) {
            return modelFileChatDtoList;
        }
        ModelFileChatDto modelFileChatDto = null;
        for (ChatFileRequestDto chatFileRequestDto : files) {
            modelFileChatDto = new ModelFileChatDto();
            modelFileChatDto.setType(chatFileRequestDto.getFileType());
            modelFileChatDto.setUpload_file_id(chatFileRequestDto.getFileId());
            modelFileChatDtoList.add(modelFileChatDto);
        }
        return modelFileChatDtoList;
    }

    /**
     * 查询向量库
     *
     * @param chatRequestDto
     * @return
     */
    private ModelInputChatDto prepareModelInput(ChatRequestDto chatRequestDto, List<MessageSourceEntity> messageSourceEntities) {
        ModelInputChatDto modelInputChatDto = new ModelInputChatDto();

        // 新增意图识别工作流
        String query = resultIntentionWorkflow(chatRequestDto, messageSourceEntities);
        // 新增意图识别核心
        if (StringUtils.isEmpty(query)) {
            query = resultIntentionCore(chatRequestDto, messageSourceEntities);
            if (StringUtils.isNotEmpty(query)) {
                chatRequestDto.setQuestion(query);
            }
        }
//        // 新增意图识别主体
//        String resultMain =  resultIntentionRecognitionMain(chatRequestDto, messageSourceEntities);
//        log.info("意图识别主体==>: {}", resultMain);
//        if (StringUtils.isNotEmpty(resultMain)) {
//            chatRequestDto.setQuestion(resultMain);
//        }
//
//        // 新增意图识别方法
//        String result = resultIntentionRecognition(chatRequestDto, messageSourceEntities);
//        log.info("意图识别方法返回值: {}", result);
//        if (StringUtils.isNotEmpty(result)) {
//            chatRequestDto.setQuestion(result);
//        }

        // 查询个人向量库
        if (chatRequestDto.getIsUseCustom()) {
            String customVectorContent = this.queryCustomVector(chatRequestDto.getQuestion(),messageSourceEntities,3);
            log.info("customVectorContent: {}", customVectorContent);
            modelInputChatDto.setPersonal_knowledge(customVectorContent);
        }
        // 企业知识库
        if (chatRequestDto.getIsUseTtkn()) {
            modelInputChatDto.setThtf_knowledge(1);
        }
        // 联网搜索
        if (chatRequestDto.getNetworking()) {
            modelInputChatDto.setNetworking(1);
//            String netWorkContent = queryNetWorkVector(chatRequestDto.getQuestion(),messageSourceEntities);
//            log.info("netWorkContent: {}", netWorkContent);
//            modelInputChatDto.setNetworking_knowledge(netWorkContent);
            Map resultMap = queryNetWorkVector1(chatRequestDto.getQuestion(), messageSourceEntities);
            if (resultMap != null ) {
                if (200 != (Integer) resultMap.get("code")){
//                    String netWorkContent = queryNetWorkVector(chatRequestDto.getQuestion(),messageSourceEntities);
//                    log.info("netWorkContent: {}", netWorkContent);
//                    modelInputChatDto.setNetworking_knowledge(netWorkContent);
                }
                log.info("netWorkContent-新联网: {}", resultMap.get("message"));
                modelInputChatDto.setNetworking_knowledge((String) resultMap.get("message"));
            }
        }

//         查询机构知识库
        if (chatRequestDto.getIsUseTtkn()) {

//            // 先去查询数据中台的向量库
//            String queryDataCenterVector = this.queryDataCenterVector(chatRequestDto.getQuestion(),
//                    messageSourceEntities, ChatApiKeyEnum.dataCenter.getKey());
//
//            log.info("queryDataCenterVector: {}", queryDataCenterVector);
//
//            String[] split = queryDataCenterVector.split("data:");
//            StringBuffer stringBuffer = new StringBuffer();
//            if (split.length > 1) {
//                for (int i = 1; i < split.length; i++) {
//                    Map<String, Object> map = JSONUtil.toBean(split[i], Map.class);
//                    String answer = (String) map.get("answer");
//                    if (StringUtils.isNotEmpty(answer)) {
//                        stringBuffer.append(answer);
//                    }
//                }
//            }
//            // 处理返回数据
//            log.info("数据中台返回结果: {}", stringBuffer);
//
//            if (StringUtils.isNotEmpty(stringBuffer.toString())) {
//                modelInputChatDto.setKnowledge(stringBuffer.toString());
//            }

            String customVectorContent = this.queryCustomVector(chatRequestDto.getQuestion(),messageSourceEntities,1);
            modelInputChatDto.setKnowledge(customVectorContent);
            log.info("TtknVectorContent: {}", customVectorContent);
        }
        // 查询部门知识库
        if (chatRequestDto.getIsUseDept()) {
            String customVectorContent = this.queryCustomVector(chatRequestDto.getQuestion(),messageSourceEntities,2);
            modelInputChatDto.setKnowledge(customVectorContent);
            log.info("DepartmentVectorContent: {}", customVectorContent);
        }


        return modelInputChatDto;
    }


    /**
     * 新的联网查询
     * @param question
     * @param messageSourceEntities
     * @return
     */
    private Map queryNetWorkVector1(String question, List<MessageSourceEntity> messageSourceEntities) {
        // 查询联网向量库
        String url = aiConfigProperties.getNewNetworkSearchApi();

        Map paramMap = new HashMap<>();
        paramMap.put("query", question);
        paramMap.put("freshness", "noLimit");
        paramMap.put("summary", true);
        paramMap.put("count", 10);
        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer "+apikeyConfigProperties.getNewNetSearch())
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;

        Map resultMap = new HashMap();

        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用联网响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                resultMap.put("code", 500);
                resultMap.put("message", "调用联网响应为空");
                return resultMap;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            Integer code = (Integer) map.get("code");
            resultMap.put("code", code);
            if (200 != code) {
                resultMap.put("code", code);
                resultMap.put("message", map.get("msg").toString());
                return resultMap;
            }
            Map data = (Map) map.get("data");
            Map jsonArray = (Map) data.get("webPages");
            List<Map> jsonArray1 = (List) jsonArray.get("value");

            StringBuffer textBuffer = new StringBuffer();

            // 先将数据记录到数据库
            for (int i = 0; i < jsonArray1.size(); i++) {
                MessageSourceEntity messageSourceEntity = new MessageSourceEntity();
                messageSourceEntity.setTitle((String) jsonArray1.get(i).get("name"));
                messageSourceEntity.setSource("netVector");
                String url1 = (String) jsonArray1.get(i).get("url");
                messageSourceEntity.setContext(url1);
                messageSourceEntities.add(messageSourceEntity);
            }
            // 如果数据量较大的化，只展示2000个字符
            for (int i = 0; i < jsonArray1.size(); i++) {
                if (textBuffer.length() > 2000) {
                    resultMap.put("code", 200);
                    resultMap.put("message", textBuffer.toString());
                    break;
                }
                String text = (String) jsonArray1.get(i).get("summary");
                if (textBuffer.length() > 0) {
                    textBuffer.append("\\r\\n");
                }
                textBuffer.append(text);
                if (textBuffer.length() > 2000) {
                    String textBufferSubString = textBuffer.substring(0, textBuffer.lastIndexOf("\\r\\n"));
                    resultMap.put("code", 200);
                    resultMap.put("message", textBufferSubString);
                    break;
                }
            }
            return resultMap;
        } catch (Exception e) {
            log.error("请求联网失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }


    /**
     * 查询意图识别集成
     *
     * @param
     * @return
     */
    private String queryIntentionRecognition(String url,String apiKey,Map paramMap, List<MessageSourceEntity> messageSourceEntities) {
        //  查询意图识别集成
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        log.info("调用查询意图识别集成请求：{}", json);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer "+apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用查询意图识别集成响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            String data = (String) map.get("answer");
            if (StringUtils.isEmpty(data)) {
                return null;
            }
            return data;
        } catch (Exception e) {
            log.error("请求查询意图识别集成失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }


    /**
     * 意图识别方法
     * @param chatRequestDto
     * @param messageSourceEntities
     * @return
     */
    private String resultIntentionRecognition(ChatRequestDto chatRequestDto,List<MessageSourceEntity> messageSourceEntities) {

        // 第一次问答不走意图识别
        if(chatRequestDto !=null && StringUtils.isNotEmpty(chatRequestDto.getConversationId())){
            // 查询历史会话
            HistoryChatDTO historyChatDTO = new HistoryChatDTO();
            historyChatDTO.setConversationId(chatRequestDto.getConversationId());
            historyChatDTO.setSceneType(chatRequestDto.getSceneType());
            RestResponse restResponse = historyChatService.historyChatListDetail(historyChatDTO);
            Map data = (Map) restResponse.getData();
            List data1 = (List) data.get("data");
            StringBuffer sb = new StringBuffer();
            // 取历史会话最新的4条问题和当前问题问题拼接
            if (data1.size() > 4) {
                int j =1;
                for (int i = data1.size()-4; i < data1.size(); i++) {
                    Map map = (Map) data1.get(i);
                    sb.append(j++).append(": ").append(map.get("query")).append("\n");
                }
                sb.append("最新输入:").append(chatRequestDto.getQuestion());
            }else{
                for (int i = 0; i < data1.size(); i++) {
                    Map map = (Map) data1.get(i);
                    sb.append(i+1).append(": ").append(map.get("query")).append("\n");
                }
                sb.append("最新输入:").append(chatRequestDto.getQuestion());
            }
            String newQuestion = sb.toString();
            log.info("newQuestion: {}", newQuestion);
            // 意图识别
            Boolean checkField = checkField(chatRequestDto);
            String queryData = "{}";
            String type ="";
            String query = "";
            if (checkField){
                String url = aiConfigProperties.getAnswerApi();
                Map paramMap = new HashMap<>();
                paramMap.put("inputs", new HashMap<>());
                paramMap.put("query", newQuestion);
                paramMap.put("response_mode", "blocking");
                paramMap.put("conversation_id", "");
                paramMap.put("user", userId);
                queryData = queryIntentionRecognition(url,apikeyConfigProperties.getIntent(), paramMap, messageSourceEntities);
            }
            Map<String, Object> queryMap = JSONUtil.toBean(queryData, Map.class);
            if (queryMap!= null && queryMap.size()>0 ) {
                log.info("queried: {}", queryMap.toString());
                type = (String) queryMap.get("type");
                query = (String) queryMap.get("query");
            }
            // 根据返回的type进行判断
            if("查询".equals(type)){
                if (StringUtils.isNotEmpty(query) && query.length() < 20){
                    return query;
                }else {
                    return chatRequestDto.getQuestion();
                }
            }
        }
        return null;
    }


    /**
     * 意图识别-主体
     * @param chatRequestDto
     * @param messageSourceEntities
     * @return
     */
    private String resultIntentionRecognitionMain(ChatRequestDto chatRequestDto,List<MessageSourceEntity> messageSourceEntities) {
        // 第一次问答不走意图识别
        if(chatRequestDto !=null && StringUtils.isEmpty(chatRequestDto.getConversationId())){
            // 意图识别-主题
            Boolean checkField = checkField(chatRequestDto);
            if (checkField){
                String url = aiConfigProperties.getAnswerApi();
                Map paramMap = new HashMap<>();
                paramMap.put("inputs", new HashMap<>());
                paramMap.put("query", chatRequestDto.getQuestion());
                paramMap.put("response_mode", "blocking");
                paramMap.put("conversation_id", "");
                paramMap.put("user", userId);
                String queryData = queryIntentionRecognition(url,apikeyConfigProperties.getIntentMain(),paramMap, messageSourceEntities);
                if (StringUtils.isNotEmpty(queryData)) {
                    String result = queryData.split("content:")[1];
                    return result;
                }
            }
        }
        return null;
    }


    /**
     * 意图识别-工作流版本
     * @param chatRequestDto
     * @param messageSourceEntities
     * @return
     */
    private String resultIntentionWorkflow(ChatRequestDto chatRequestDto,List<MessageSourceEntity> messageSourceEntities) {
        Boolean checkField = checkField(chatRequestDto);
        if (checkField){
            String url = aiConfigProperties.getChatNetSearchApi();
            Map paramMap = new HashMap<>();
            Map inputs = new HashMap<>();
            inputs.put("query", chatRequestDto.getQuestion());
            paramMap.put("inputs", inputs);
            paramMap.put("response_mode", "blocking");
            paramMap.put("conversation_id", "");
            paramMap.put("user", userId);
            Map map = queryCommonMethod(url,apikeyConfigProperties.getIntentWorkflowVersion(),paramMap, messageSourceEntities);
            Map data = (Map) map.get("data");
            Map outputs = (Map) data.get("outputs");

            if (outputs!= null && outputs.size()>0 ) {
                log.info("queried: {}", outputs.toString());
                String type = "";
                String query = "";
                String jsonString = outputs.get("text").toString();
                if (jsonString.contains("context:")) {
                    query = jsonString.split("context:")[1];
                    return query;
                }else if(jsonString.contains("type")){
                    Map<String, Object> textMap = JSONUtil.toBean(jsonString, Map.class);
                    if (textMap != null) {
                        type = textMap.get("type").toString();
                        query = textMap.get("query").toString();
                        log.info("解析后的type: {}, query: {}", type, query);
                    }
                    // 根据返回的type进行判断
                    if("查询".equals(type)){
                        if (StringUtils.isNotEmpty(query) && query.length() < 20){
                            return query;
                        }else {
                            return chatRequestDto.getQuestion();
                        }
                    }
                }
            }
        }
        return null;
    }


    /**
     * 意图识别-核心
     * @param chatRequestDto
     * @param messageSourceEntities
     * @return
     */
    private String resultIntentionCore(ChatRequestDto chatRequestDto,List<MessageSourceEntity> messageSourceEntities) {
        Boolean checkField = checkField(chatRequestDto);
        if (checkField){
            String url = aiConfigProperties.getAnswerApi();
            Map paramMap = new HashMap<>();
            paramMap.put("inputs", new HashMap<>());
            paramMap.put("query", chatRequestDto.getQuestion());
            paramMap.put("response_mode", "blocking");
            paramMap.put("conversation_id", "");
            paramMap.put("user", userId);
            Map map = queryCommonMethod(url,apikeyConfigProperties.getIntentCore(),paramMap, messageSourceEntities);
            String data = (String) map.get("answer");
            if (StringUtils.isNotEmpty(data) && data.contains("context:")) {
                return data.split("context:")[1];
            }
        }
        return null;
    }


    /**
     * 通用方法
     * @param url
     * @param apiKey
     * @param paramMap
     * @param messageSourceEntities
     * @return
     */
    private Map queryCommonMethod(String url,String apiKey,Map paramMap, List<MessageSourceEntity> messageSourceEntities) {
        //  查询意图识别集成
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        log.info("调用查询意图识别集成请求：{}", json);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer "+apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用查询意图识别集成响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            return map;
        } catch (Exception e) {
            log.error("请求查询意图识别集成失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }

    /**
     * 检查是否有字段为true
     * @param chatRequestDto
     * @return
     */
    private Boolean checkField(ChatRequestDto chatRequestDto){

        if (chatRequestDto.getIsUseCustom()){
            return true;
        }
        if (chatRequestDto.getIsUseDept()){
            return true;
        }
        if (chatRequestDto.getIsUseOrg()){
            return true;
        }
        if (chatRequestDto.getIsUseTtkn()){
            return true;
        }
        if (chatRequestDto.getNetworking()){
            return true;
        }
        return false;
    }

    /**
     * 增加了一个参数flag，进行方法复用，用于区分机构、部门、个人
     * 检索个人向量库
     *
     * @param question
     * @param flag
     *  (1, "机构"),
     *  (2, "部门"),
     *  (3, "个人");
     * @return
     */
    private String queryCustomVector(String question, List<MessageSourceEntity> messageSourceEntities, Integer flag) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        StringBuffer contentBuffer = new StringBuffer();
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, "{" +
                "    \"query\": \"" + question + "\"," +
                "    \"retrieval_model\": {" +
                "        \"search_method\": \"hybrid_search\"," +
                "        \"reranking_enable\": false," +
                "        \"reranking_mode\": null," +
                "        \"reranking_model\": {" +
                "            \"reranking_provider_name\": \"\"," +
                "            \"reranking_model_name\": \"\"" +
                "        }," +
                "        \"weights\": null," +
                "        \"top_k\": 5," +
                "        \"score_threshold_enabled\": false," +
                "        \"score_threshold\": null" +
                "    } " +
                "}");
        String url = aiConfigProperties.getVectorQueryApi();
        // 查询个人向量库ip（暂用，后期改成从页面传过来）
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        String dataset_id = "";
        // "企业"
        if (1==flag){
            dataset_id = datasetsConfigProperties.getUnitId();
            log.info("企业知识库id：{}", dataset_id);
        }
        // "部门"
        if (2==flag){
            BusResourceDatasetDTO datasetDTO = datasetRepo.getByCode(ContextUtil.currentUser().getDepNum());
            dataset_id = null == datasetDTO ? null : datasetDTO.getDatasetsId();
            log.info("部门知识库id：{}", dataset_id);
        }
        // "个人"
        if (3==flag){
            BusResourceDatasetDTO datasetDTO = datasetRepo.getByCode(userId);
            dataset_id = null == datasetDTO ? null : datasetDTO.getDatasetsId();
            log.info("个人知识库id：{}", dataset_id);
        }

        if (StringUtils.isEmpty(dataset_id)) {
            log.error("个人向量库id查询为空");
            return null;
        }
        url = String.format(url, dataset_id);
        log.info("查询个人知识库URL: {}", url);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + apikeyConfigProperties.getCustomvector())
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("查询个人知识库响应：{}", jsonString);
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            List<Map> records = (List<Map>) map.get("records");
            // 将内容拼到一起作为入参
            if (null != records) {
                for (Map recodeMap : records) {
                    MessageSourceEntity messageSourceEntity = new MessageSourceEntity();
                    Map segmentMap = (Map) recodeMap.get("segment");
                    String fileName = (String) ((Map)segmentMap.get("document")).get("name");
                    messageSourceEntity.setDocumentId((String) ((Map)segmentMap.get("document")).get("id"));
                    messageSourceEntity.setKeyword(segmentMap.get("keywords").toString());
                    messageSourceEntity.setSegmentId(segmentMap.get("id").toString());
                    messageSourceEntity.setTitle(fileName);
                    messageSourceEntity.setSource("customVector"+flag);
                    if (null != segmentMap) {
                        String content = (String) segmentMap.get("content");
                        if (contentBuffer.length() > 0) {
                            contentBuffer.append("\\r\\n");
                        }
                        messageSourceEntity.setContext(content);
                        contentBuffer.append(content);
                    }
                    messageSourceEntities.add(messageSourceEntity);
                }

            }
        } catch (IOException e) {
            log.error("查询个人向量库失败，失败原因:" + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            response.close();
        }
        return contentBuffer.toString();
    }


    /**
     * 联网检索向量库
     *
     * @param question
     * @return
     */
    private String queryNetWorkVector(String question, List<MessageSourceEntity> messageSourceEntities) {
        // 查询联网向量库
        String url = aiConfigProperties.getChatNetSearchApi();
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, "{\n" +
                "    \"inputs\": {\n" +
                "       \"question\": \""+question+"\"\n" +
                "    },\n" +
                "    \"response_mode\": \"blocking\",\n" +
                "    \"conversation_id\": \"\",\n" +
                "    \"user\": \""+userId+"\"\n" +
                "}\n"
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + apikeyConfigProperties.getNetsearch())
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用联网响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            Map data = (Map) map.get("data");
            Map jsonArray = (Map) data.get("outputs");
            List<Map> jsonArray1 = (List) jsonArray.get("text");

            StringBuffer textBuffer = new StringBuffer();
            for (int i = 0; i < jsonArray1.size(); i++) {
                MessageSourceEntity messageSourceEntity = new MessageSourceEntity();
                messageSourceEntity.setTitle((String) jsonArray1.get(i).get("title"));
                messageSourceEntity.setSource("netVector");
                if (textBuffer.length() > 2000) {
                    return textBuffer.toString();
                }
                String text = (String) jsonArray1.get(i).get("url");
                messageSourceEntity.setContext(text);
                if (textBuffer.length() > 0) {
                    textBuffer.append("\\r\\n");
                }
                textBuffer.append(text);
                if (textBuffer.length() > 2000) {
                    String textBufferSubString = textBuffer.substring(0, textBuffer.lastIndexOf("\\r\\n"));
                    return textBufferSubString;
                }
                messageSourceEntities.add(messageSourceEntity);
            }
            return textBuffer.toString();
        } catch (Exception e) {
            log.error("请求联网失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }


    /**
     * 查询数据中台的向量库
     * @param question
     * @param messageSourceEntities
     * @param apiKey
     * @return
     */
    public String  queryDataCenterVector(String question, List<MessageSourceEntity> messageSourceEntities,String apiKey) {
        String url = aiConfigProperties.getDataCenterChatApi();

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("{\n" +
                "    \"inputs\": {\n" +
                "    },\n" +
                "    \"query\": \""+ question +"\"\n,\n" +
                "    \"response_mode\": \"streaming\",\n" +
                "    \"conversation_id\": \"\",\n" +
                "    \"user\": \""+userId+"\"\n" +
                "}\n");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(body)
                .header("Authorization", "Bearer "+apiKey)
                .header("Content-Type", "application/json;charset=utf-8")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        StringBuilder result = new StringBuilder();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(response -> {
                    try (InputStream inputStream = response.body()) {
                        // 逐块读取流式数据
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            result.append(new String(buffer, 0, bytesRead));
                        }
                        log.info("调用数据中台的响应：{}", result.toString());
                        return result.toString(); // 最终聚合结果
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .thenAccept(System.out::println)
                .join(); // 阻塞等待所有数据处理完成

        return result.toString();
    }

    /**
     * 创建会话
     */
    @Override
    public RestResponse createConversation(String type) {
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        String apiKey = checkUtil.getApiKey(type);
//        String apiKey = ChatApiKeyEnum.getKey(type);
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)    // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时时间
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        ModelChatDto modelChatDto = new ModelChatDto();
        modelChatDto.setInputs(new Object());
        modelChatDto.setQuery("获取会话");
        modelChatDto.setResponse_mode("blocking");
        modelChatDto.setConversation_id("");
        modelChatDto.setUser(userId);

        Gson gson = new Gson();
        String json = gson.toJson(modelChatDto);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(aiConfigProperties.getAnswerApi())
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
//            判断回答是否符合规范
            String answer = map.get("answer").toString();
            if (!isCorrectFormat(answer)){
                map=null;
            }
            return RestResponse.success(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param recommendChatDto
     * @return
     */
    @Override
    public RestResponse recommendList(RecommendChatDto recommendChatDto) {
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        String apiKey = apikeyConfigProperties.getRecommendList();
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        String url = aiConfigProperties.getAnswerApi();
        url = String.format(url, recommendChatDto.getMessageId());
        StringBuffer urlBuffer = new StringBuffer(url)
                .append("?user=").append(userId);
        ModelChatDto modelChatDto = new ModelChatDto();
        modelChatDto.setInputs(new Object());
        modelChatDto.setQuery(recommendChatDto.getQuestion());
        modelChatDto.setResponse_mode("blocking");
        modelChatDto.setUser(userId);
        modelChatDto.setInputs(new Object());
        Gson gson = new Gson();
        String json = gson.toJson(modelChatDto);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(urlBuffer.toString())
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch();
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            log.info("请求推荐接口响应：{}, 耗时：{} ms", jsonString, watch.getTime(TimeUnit.MILLISECONDS));
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            //判断回答是否符合规范
            if (!isCorrectFormat(map.get("answer").toString())) {
                map=null;
            }
            return RestResponse.success(map);
        } catch (IOException e) {
            log.error("请求推荐接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    // 检查字符串是否符合正确的格式
    private Boolean isCorrectFormat(String answer) {
        String[] lines = answer.trim().split("\n");
        if (lines.length > 3||answer.length()>108) {
            return false;
        }
        String patternString = "^\\d+\\. .+$";
        Pattern pattern = Pattern.compile(patternString);
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()||line.length()>36) {
                return false;
            }
        }
        return true;
    }

    private Response chatQuery(ChatRequestDto chatRequestDto, String apiKey, ModelInputChatDto modelInputChatDto, List<ModelFileChatDto> files) {
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        ModelChatDto modelChatDto = new ModelChatDto();
        StringBuffer urlBuffer = new StringBuffer();
        if (StringUtils.isNotEmpty(modelInputChatDto.getPersonal_knowledge())) {
            urlBuffer.append(modelInputChatDto.getPersonal_knowledge());
        }
        if (StringUtils.isNotEmpty(modelInputChatDto.getNetworking_knowledge())) {
            urlBuffer.append(modelInputChatDto.getNetworking_knowledge());
        }
        if (StringUtils.isNotEmpty(modelInputChatDto.getKnowledge())) {
            urlBuffer.append(modelInputChatDto.getKnowledge());
        }
        modelInputChatDto.setKnowledge(urlBuffer.toString());
        modelChatDto.setInputs(modelInputChatDto);
        modelChatDto.setQuery(chatRequestDto.getQuestion());
        modelChatDto.setResponse_mode("streaming");
        modelChatDto.setConversation_id(chatRequestDto.getConversationId());
        modelChatDto.setUser(userId);

//        List<ModelFileChatDto> files = new ArrayList<>(10);
//        ModelFileChatDto file = new ModelFileChatDto();
//        file.setType("image");
//        file.setTransfer_method("remote_url");
//        file.setUrl("https://cloud.dify.ai/logo/logo-site.png");
        modelChatDto.setFiles(files);
        Gson gson = new Gson();
        String json = gson.toJson(modelChatDto);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        log.info("会话请求参数：{}", json);

        Request request = new Request.Builder()
                .url(aiConfigProperties.getAnswerApi())
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                // 提取错误信息
                String errorBody = response.body().string();
                Map<String, Object> errorMap = gson.fromJson(errorBody, HashMap.class);
                String status = String.valueOf(errorMap.get("status"));
                String outputs = String.valueOf(errorMap.get("outputs"));
                String error = String.valueOf(errorMap.get("error"));

                // 构建错误响应
                String errorMessage = "Dify 工作流运行出错，状态码：" + status + "，输出信息：" + outputs + "，错误信息：" + error;
                log.error(errorMessage);
                // 这里可以根据具体情况返回自定义的错误响应给用户
                throw new RuntimeException(errorMessage);
            }
        } catch (IOException e) {
            log.error("请求规划接口失败，失败原因", e.getMessage());
//            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * 上传文件到ai上
     *
     * @param sceneType
     * @return
     */
    @Override
    public RestResponse uploadFileToAi(MultipartFile file, String sceneType) {
        String apiKey = checkUtil.getApiKey(sceneType);
//        String apiKey = ChatApiKeyEnum.getKey(sceneType);
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        // 上传文件到有云
        Map uploadMap = this.uploadHandler(file, client, apiKey);
        if (null != uploadMap) {
            ChatFileRequestDto chatFileRequestDto = new ChatFileRequestDto();
            chatFileRequestDto.setFileId((String) uploadMap.get("id"));
            chatFileRequestDto.setFileType("document");
            return RestResponse.success(chatFileRequestDto);
        } else {
            return RestResponse.error("上传失败");
        }

    }

    /**
     * 重命名会话名称
     *
     * @param renameChatDto
     * @return
     */
    @Override
    public RestResponse renameConversation(RenameChatDto renameChatDto) {
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        String apiKey = checkUtil.getApiKey(renameChatDto.getSceneType());
//        String apiKey = ChatApiKeyEnum.getKey(renameChatDto.getSceneType());
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        String url = aiConfigProperties.getRenameConversationApi();
        url = String.format(url, renameChatDto.getConversationId());
        Map paramMap = new HashMap<>(2);
        paramMap.put("user", userId);
        paramMap.put("auto_generate", false);
        paramMap.put("name", renameChatDto.getConversationName());
        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null != map) {
                return RestResponse.SUCCESS;
            } else {
                return RestResponse.fail(DefaultErrorCode.CHAT_ERROR.getCode(), "会话命名失败");
            }
        } catch (IOException e) {
            log.error("请求重命名会话接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    /**
     * 停止会话
     *
     * @param stopChatDto
     * @return
     */
    @Override
    public RestResponse stopConversation(StopChatDto stopChatDto) {
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        String apiKey = checkUtil.getApiKey(stopChatDto.getSceneType());
//        String apiKey = ChatApiKeyEnum.getKey(stopChatDto.getSceneType());
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        String url = aiConfigProperties.getStopConversationApi();
        url = String.format(url, stopChatDto.getTaskId());
        Map paramMap = new HashMap<>(2);
        paramMap.put("user", userId);
        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null != map && "success".equals((String) map.get("result"))) {
                return RestResponse.SUCCESS;
            } else {
                return RestResponse.fail(DefaultErrorCode.CHAT_ERROR.getCode(), "停止会话失败");
            }
        } catch (IOException e) {
            log.error("请求重命名会话接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    private Map uploadHandler(MultipartFile multipartFile, OkHttpClient client, String apiKey) {
        String fileType = multipartFile.getContentType();
        Response response = null;
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        try {
            RequestBody fileBody = RequestBody.create(MediaType.parse(fileType), multipartFile.getBytes());
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user", userId)
                    .addFormDataPart("file", multipartFile.getOriginalFilename(), fileBody)
                    .build();
            Request request = new Request.Builder()
                    .url(aiConfigProperties.getUploadFileApi())
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            if (!StringUtils.isEmpty(jsonString)) {
                Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
                return map;
            }

        } catch (IOException e) {
            log.error("上传文件失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }
}
