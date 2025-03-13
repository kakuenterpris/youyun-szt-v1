package com.thtf.chat.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cnki.maas.http.ApiClient;
import com.cnki.maas.model.ApiRequest;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.thtf.chat.entity.MessageSourceEntity;
import com.thtf.chat.enums.ChatApiKeyEnum;
import com.thtf.chat.properties.AiConfigProperties;
import com.thtf.chat.properties.DatasetsConfigProperties;
import com.thtf.chat.repo.MessageSourceRepo;
import com.thtf.chat.service.ChatService;
import com.thtf.chat.service.RelUserResourceService;
import com.thtf.dto.*;
import com.thtf.global.common.exception.CustomException;
import com.thtf.global.common.rest.ContextUtil;
import com.thtf.global.common.rest.DefaultErrorCode;
import com.thtf.global.common.rest.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author zhangwei
 * @date 2025年02月18日
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Value("${cnki.vector.access-key}")
    private String cnkiVectorAccessKey;
    @Value("${cnki.vector.secret-key}")
    private String cnkiVectorSecretKey;

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Autowired
    private RelUserResourceService relUserResourceService;

    @Autowired
    private MessageSourceRepo messageSourceRepo;

    @Autowired
    private DatasetsConfigProperties datasetsConfigProperties;


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
        // 根据场景类型获取api-key
        String apiKey = ChatApiKeyEnum.getKey(chatRequestDto.getSceneType());
        List<MessageSourceEntity> messageSourceEntities = new ArrayList<>();
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        // 查询向量库
        ModelInputChatDto modelInputChatDto = prepareModelInput(chatRequestDto,messageSourceEntities);
        // 上传文件处理
        List<ModelFileChatDto> modelFileChatDtoList = inputFileHandler(chatRequestDto.getFiles());

        SseEmitter emitter = new SseEmitter((long) Integer.MAX_VALUE); // 设置超时时间
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Response response = this.chatQuery(chatRequestDto, apiKey, modelInputChatDto, modelFileChatDtoList);
            BufferedSource source = response.body().source();
            try {
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
                        for (MessageSourceEntity messageSourceEntity : messageSourceEntities) {
                            messageSourceEntity.setMessageId(message_id);
                            messageSourceEntity.setConversationId(conversation_id);
                            messageSourceRepo.save(messageSourceEntity);
                        }
                        emitter.send(SseEmitter.event().data(messageSourceEntities));
                    }
                    //  发送来源
                    if (line != null && !line.isEmpty()) {

                        emitter.send(SseEmitter.event().data(line));
                    }
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
                throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "对话中断或错误");
            } finally {
                emitter.complete();
                response.close();
            }
        });

        return emitter;
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

        // 查询个人向量库
        if (chatRequestDto.getIsUseCustom()) {
            String customVectorContent = this.queryCustomVector(chatRequestDto.getQuestion(),messageSourceEntities,3);
            log.info("customVectorContent: {}", customVectorContent);
            modelInputChatDto.setPersonal_knowledge(customVectorContent);
        }
        // 查询cnki
        if (chatRequestDto.getIsUseCnki()) {
            String cnkiVectorContent = this.queryCnkiVector(chatRequestDto.getQuestion(),messageSourceEntities);
            log.info("cnkiVectorContent: {}", cnkiVectorContent);
            modelInputChatDto.setCnki_knowledge(cnkiVectorContent);
        }
        // 同方知识库--企业知识库
        if (chatRequestDto.getIsUseTtkn()) {
            modelInputChatDto.setThtf_knowledge(1);
        }
        // 联网搜索
        if (chatRequestDto.getNetworking()) {
            modelInputChatDto.setNetworking(1);
            String netWorkContent = queryNetWorkVector(chatRequestDto.getQuestion(),messageSourceEntities);
            log.info("netWorkContent: {}", netWorkContent);
            modelInputChatDto.setNetworking_knowledge(netWorkContent);
        }

//         查询机构知识库
        if (chatRequestDto.getIsUseOrg()) {

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
            log.info("OrganizationVectorContent: {}", customVectorContent);
        }
        // 查询部门知识库
        if (chatRequestDto.getIsUseDept()) {
            String customVectorContent = this.queryCustomVector(chatRequestDto.getQuestion(),messageSourceEntities,2);
            log.info("DepartmentVectorContent: {}", customVectorContent);
        }


        return modelInputChatDto;
    }

    /**
     * 检索知网向量库
     *
     * @param question
     * @return
     */
    private String queryCnkiVector(String question, List<MessageSourceEntity> messageSourceEntities) {
        // 查询知网向量库
        String url = aiConfigProperties.getCnkiVectorQueryApi();
        ApiRequest apiRequest = new ApiRequest(url);
        apiRequest.setCredential(cnkiVectorAccessKey, cnkiVectorSecretKey);
        apiRequest.addHeaders("Content-Type", "application/json;charset=utf-8");
        ApiClient apiClient = new ApiClient();
        CnkiVectorRequestDto cnkiRequestDto = new CnkiVectorRequestDto();
        cnkiRequestDto.setQuery(question);
        Gson gson = new Gson();
        String json = gson.toJson(cnkiRequestDto);
        apiRequest.setJsonBody(json);
        Response response = null;
        try {
            response = apiClient.sendRequest(apiRequest);
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用知网向量库响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            JSONArray jsonArray = JSONArray.parseArray(jsonString);
            StringBuffer textBuffer = new StringBuffer();
            for (int i = 0; i < jsonArray.size(); i++) {
                MessageSourceEntity messageSourceEntity = new MessageSourceEntity();
                if (textBuffer.length() > 2000) {
                    return textBuffer.toString();
                }
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String text = jsonObject.getString("text");
                messageSourceEntity.setContext(text);
                messageSourceEntity.setSource("CnkiVector");
                messageSourceEntity.setTitle(jsonObject.getString("title"));
                messageSourceEntities.add(messageSourceEntity);
                if (textBuffer.length() > 0) {
                    textBuffer.append("\\r\\n");
                }
                textBuffer.append(text);
                if (textBuffer.length() > 2000) {
                    String textBufferSubString = textBuffer.substring(0, textBuffer.lastIndexOf("\\r\\n"));
                    return textBufferSubString;
                }
            }
            return textBuffer.toString();
        } catch (Exception e) {
            log.error("请求知网向量库失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
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
                "        \"top_k\": 1," +
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
        // "机构"
        if (1==flag){
            dataset_id = datasetsConfigProperties.getUnitId();
            log.info("机构知识库id：{}", dataset_id);
        }
        // "部门"
        if (2==flag){
            dataset_id = datasetsConfigProperties.getDepId();
            log.info("部门知识库id：{}", dataset_id);
        }
        // "个人"
        if (3==flag){
            dataset_id = relUserResourceService.getDatasetIdByUserId(userId);
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
                .addHeader("Authorization", "Bearer " + ChatApiKeyEnum.customvector.getKey())
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
                .addHeader("Authorization", "Bearer " + ChatApiKeyEnum.netsearch.getKey())
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
        String apiKey = ChatApiKeyEnum.getKey(type);
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
        String apiKey = ChatApiKeyEnum.recommendList.getKey();
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
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("请求推荐接口响应：{}", jsonString);
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
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
        if (StringUtils.isNotEmpty(modelInputChatDto.getCnki_knowledge())) {
            urlBuffer.append(modelInputChatDto.getCnki_knowledge());
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
        } catch (IOException e) {
            log.error("请求规划接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
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
        String apiKey = ChatApiKeyEnum.getKey(sceneType);
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
        String apiKey = ChatApiKeyEnum.getKey(renameChatDto.getSceneType());
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
        String apiKey = ChatApiKeyEnum.getKey(stopChatDto.getSceneType());
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
            if (null != map && map.get("result").equals("success")) {
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
