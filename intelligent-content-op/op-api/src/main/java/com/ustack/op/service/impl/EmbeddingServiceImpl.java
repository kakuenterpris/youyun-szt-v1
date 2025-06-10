package com.ustack.op.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.EmbeddingDTO;
import com.ustack.emdedding.dto.ModelFileChatDTO;
import com.ustack.emdedding.dto.SliceDTO;
import com.ustack.emdedding.dto.SliceInputsDTO;
import com.ustack.global.common.exception.CustomException;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.properties.AiConfigProperties;
import com.ustack.op.properties.ApikeyConfigProperties;
import com.ustack.op.runnable.SliceAndEmbeddingRunnable;
import com.ustack.op.service.EmbeddingService;
import com.ustack.op.util.FileUtils;
import com.ustack.resource.dto.BusResourceManageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    @Autowired
    private AiConfigProperties aiConfigProperties;

    @Autowired
    private ApikeyConfigProperties apikeyConfigProperties;

    private static String userId = "abc-123";


    /**
     * 向量化接口
     * @param text
     * @return
     */
    @Override
    public String embedding(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        String apiKey = apikeyConfigProperties.getVector();
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        String url = aiConfigProperties.getEmbeddingApi();
        EmbeddingDTO embeddingDTO = new EmbeddingDTO();
        List<String> paramList = new ArrayList<>(2);
        paramList.add(text);
        embeddingDTO.setInput(paramList);
        Gson gson = new Gson();
        String json = gson.toJson(embeddingDTO);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求向量化处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
//            log.info("向量化接口响应耗时：{} ms", watch.getTime(TimeUnit.MILLISECONDS));
            if (StringUtil.isEmpty(jsonString)) {
                log.error("向量化数据结果为空，原文本内容为{}", jsonString);
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            List<Map> responseList = (List) map.get("data");
            if (ObjectUtils.isNotEmpty(responseList)) {
                Map responseMap = responseList.get(0);
                List<BigDecimal> embeddings = (List<BigDecimal>) responseMap.get("embedding");
                return FileUtils.listBigdecimalToString(embeddings);
            }
            return null;
        } catch (IOException e) {
            log.error("向量化数据失败，失败原因", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    /**
     * 切片接口
     *
     * @param fileId
     * @return
     */
    @Override
    public Map slice(String fileId, String fileType) {
        if (StringUtils.isEmpty(fileId)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String apiKey = apikeyConfigProperties.getChunks();
        if (StringUtil.isEmpty(apiKey)) {
            throw new CustomException(DefaultErrorCode.CHAT_ERROR.getCode(), "场景类型错误");
        }
        String url = aiConfigProperties.getEmbeddingApi();
        SliceDTO sliceDTO = new SliceDTO();
        if (null != ContextUtil.getUserId()) {
            userId = ContextUtil.getUserId();
        }
        sliceDTO.setUser(userId);
        SliceInputsDTO inputs = new SliceInputsDTO();
        inputs.setFile_type(fileType);
        ModelFileChatDTO file = new ModelFileChatDTO();
        file.setUpload_file_id(fileId);
        inputs.setFile(file);
        sliceDTO.setInputs(inputs);

        Gson gson = new Gson();
        String json = gson.toJson(sliceDTO);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求切片处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            log.info("切片处理接口响应耗时：{} ms", watch.getTime(TimeUnit.MILLISECONDS));
            if (StringUtil.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            List<Map> responseList = (List) map.get("data");
            if (ObjectUtils.isNotEmpty(responseList)) {
                Map responseMap = responseList.get(0);
                Map outputMap = (Map) responseMap.get("outputs");

                return outputMap;
            }
            return null;
        } catch (IOException e) {
            log.error("请求推荐接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    /**
     * 临时切片接口
     *
     * @param file
     * @return
     */
    @Override
    public Map sliceTemp(File file, String fileType) {
        if (null == file) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String url = aiConfigProperties.getSliceTempApi();
        RequestBody fileBody = RequestBody.create(MediaType.parse(fileType), file);
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求临时切片处理接口");
            watch.start();
            response = client.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
//            log.info("临时切片处理接口响应耗时：{} ms", watch.getTime(TimeUnit.MILLISECONDS));
            if (StringUtil.isEmpty(jsonString)) {
                log.error("文件名称为 " + file.getName() + "临时切片处理接口响应为空");
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("请求切片接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    /**
     * 单个文本向量化
     *
     * @param content
     * @return
     */
    @Override
    public String embeddingTemp(String content) {
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        String url = aiConfigProperties.getEmbeddingTempApi();
        Map paramMap = new HashMap(2);
        paramMap.put("text", content);
        paramMap.put("model_type", "local");

        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求临时向量化处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            log.info("临时向量化处理接口响应耗时：{} ms", watch.getTime(TimeUnit.MILLISECONDS));
            if (StringUtil.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null != map) {
                List<BigDecimal> list = (List<BigDecimal>) map.get("vector");
                // list转字符串
                return FileUtils.listBigdecimalToString(list);
            }
        } catch (IOException e) {
            log.error("请求推荐接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 多文本向量化
     *
     * @param listChildCotent
     * @return
     */
    @Override
    public String embeddingListTemp(List<String> listChildCotent) {
        if (ObjectUtils.isEmpty(listChildCotent)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        String url = aiConfigProperties.getEmbeddingTempApi();
        Map paramMap = new HashMap(2);
        paramMap.put("text", listChildCotent);
        paramMap.put("model_type", "local");

        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求临时向量化处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            log.info("临时向量化处理接口响应耗时：{} ms", watch.getTime(TimeUnit.MILLISECONDS));
            if (StringUtil.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null != map) {
                List<List<BigDecimal>> lists = (List<List<BigDecimal>>) map.get("vectors");
                StringBuffer embeddingBuffer = new StringBuffer();
                for (List<BigDecimal> list : lists) {
                    // list转字符串
                    String embeddingStr = FileUtils.listBigdecimalToString(list);
                    if (StringUtil.isEmpty(embeddingStr)) {
                        continue;
                    }
                    if (embeddingBuffer.length() > 0) {
                        embeddingBuffer.append(";");
                    }
                    embeddingBuffer.append(embeddingStr);
                }
                return embeddingBuffer.toString();
            }
        } catch (IOException e) {
            log.error("请求推荐接口失败，失败原因", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return null;
    }
}
