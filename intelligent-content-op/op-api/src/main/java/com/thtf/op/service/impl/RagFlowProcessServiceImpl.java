package com.thtf.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.thtf.emdedding.constants.CommonConstants;
import com.thtf.op.properties.RagFlowApiConfigProperties;
import com.thtf.op.service.RagFlowProcessService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@Service
@Slf4j
public class RagFlowProcessServiceImpl implements RagFlowProcessService {

    @Autowired
    RagFlowApiConfigProperties ragFlowApiConfigProperties;


    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @Override
    public String uploadFile(String datasetId, File file) {
        if (null == file || StrUtil.isEmpty(datasetId)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        // 根据资源id,查询对应的ragflow知识库id

        String apiKey = ragFlowApiConfigProperties.getApiKey();
        // String datasetId = ragFlowApiConfigProperties.getDatasetId();
        String url = ragFlowApiConfigProperties.getUploadUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return null;
        }
        url = String.format(url, datasetId);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM) // 表单类型
                .addFormDataPart(
                        "file",
                        file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file)
                )
                //.addFormDataPart("otherParam", "value") // 可选：添加其他表单参数
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求临时切片处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null != map && (Integer) map.get("code") == 0) {
                JSONArray jsonArray = (JSONArray) map.get("data");
                if (CollectionUtil.isNotEmpty(jsonArray)) {
                    Map dataMap = (Map) jsonArray.get(0);
                    return (String) dataMap.get("id");
                }
            } else {
                log.error("ragflow上传接口失败，失败原因{}", map.get("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ragflow上传接口失败，失败原因:", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 解析文件
     *
     * @param uploadFileId
     */
    @Override
    public boolean parseFile(String datasetId, String uploadFileId) {
        if (StrUtil.isEmpty(uploadFileId)) {
            log.error("上传文件id为空,无法解析");
            return false;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String apiKey = ragFlowApiConfigProperties.getApiKey();
        // String datasetId = ragFlowApiConfigProperties.getDatasetId();
        String url = ragFlowApiConfigProperties.getParseUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return false;
        }
        url = String.format(url, datasetId);
        Map paramMap = new HashMap(2);
        List<String> fileIdList = new ArrayList<>();
        fileIdList.add(uploadFileId);
        paramMap.put("document_ids", fileIdList);

        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求ragflow解析文档处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null == map || (Integer) map.get("code") != 0) {
                String errorMessage = (String) map.get("message");
                log.error("ragflow解析文档处理接口失败，失败原因{}", errorMessage);
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error("ragflow解析文档处理接口，失败原因{}", e.getMessage());
            return false;
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        if (null == multipartFile) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String apiKey = ragFlowApiConfigProperties.getApiKey();
        String datasetId = ragFlowApiConfigProperties.getDatasetId();
        String url = ragFlowApiConfigProperties.getUploadUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return null;
        }
        url = String.format(url, datasetId);
        File tempFile = null;
        RequestBody requestBody = null;
        try {
            tempFile = File.createTempFile("upload-", ".tmp");
            multipartFile.transferTo(tempFile); // Spring 提供的方法
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                            "file",
                            multipartFile.getOriginalFilename(),
                            RequestBody.create(MediaType.parse(multipartFile.getContentType()), tempFile)).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求临时切片处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null != map && (Integer) map.get("code") == 0) {
                JSONArray jsonArray = (JSONArray) map.get("data");
                if (CollectionUtil.isNotEmpty(jsonArray)) {
                    Map dataMap = (Map) jsonArray.get(0);
                    return (String) dataMap.get("id");
                }
            } else {
                log.error("ragflow上传文件失败，失败原因{}", map.get("message"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ragflow上传文件失败，失败原因:", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 获取切片内容
     *
     * @param uploadFileId
     * @return
     */
    @Override
    public Map chunks(String datasetId, String uploadFileId, Integer page, Integer pageSize) {
        if (StrUtil.isEmpty(uploadFileId)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String apiKey = ragFlowApiConfigProperties.getApiKey();
        // String datasetId = ragFlowApiConfigProperties.getDatasetId();
        String url = ragFlowApiConfigProperties.getChunksUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return null;
        }
        if (null != page && null != pageSize) {
            url = url + "?page=" + page + "&page_size=" + pageSize;
        }
        url = String.format(url, datasetId, uploadFileId);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求ragflow解析文档处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null == map || (Integer) map.get("code") != 0) {
                String errorMessage = (String) map.get("message");
                log.error("ragflow获取切片失败，失败原因{}", errorMessage);
                return null;
            }
            Map dataMap = (Map) map.get("data");
            Integer total = (Integer) dataMap.get("total");
            List<Map> chunks = (List<Map>) dataMap.get("chunks");
            Map resultMap = new HashMap(2);
            resultMap.put("total", total);
            resultMap.put("chunks", chunks);
            return resultMap;
        } catch (IOException e) {
            log.error("ragflow获取切片失败，失败原因{}", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }

    /**
     * 获取切片状态
     *
     * @param datasetId
     * @param uploadFileId
     * @return
     */
    @Override
    public String getChunksStatus(String datasetId, String uploadFileId) {
        if (StrUtil.isEmpty(uploadFileId)) {
            return null;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String apiKey = ragFlowApiConfigProperties.getApiKey();
        String url = ragFlowApiConfigProperties.getChunksStatusUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("获取切片状态得url或key为空");
            return null;
        }
        url = String.format(url, datasetId);
        url = url + "?id=" + uploadFileId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求ragflow解析文档处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null == map || (Integer) map.get("code") != 0) {
                String errorMessage = (String) map.get("message");
                log.error("ragflow获取切片状态失败，失败原因{}", errorMessage);
                return null;
            }
            Map dataMap = (Map) map.get("data");
            List<Map> docList = (List<Map>) dataMap.get("docs");
            if (CollectionUtil.isNotEmpty(docList)) {
                Map docMap = docList.get(0);
                return (String) docMap.get("run");
            }
        } catch (IOException e) {
            log.error("ragflow获取切片状态失败，失败原因{}", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 删除文档
     *
     * @param documentId
     * @return
     */
    @Override
    public boolean delete(String documentId, String datasetId) {
        List<String> documentIdList = new ArrayList<>();
        documentIdList.add(documentId);
        return this.delete(documentIdList, datasetId);

    }

    /**
     * 删除多个切片文档
     *
     * @param documentIdList
     * @param datasetId
     * @return
     */
    @Override
    public boolean delete(List<String> documentIdList, String datasetId) {
        if (CollUtil.isEmpty(documentIdList)) {
            log.error("上传文件id为空,无法删除");
            return false;
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();
        String apiKey = ragFlowApiConfigProperties.getApiKey();
        // String datasetId = ragFlowApiConfigProperties.getDatasetId();
        String url = ragFlowApiConfigProperties.getDeleteUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("删除文档得url或key为空");
            return false;
        }
        url = String.format(url, datasetId);
        Map paramMap = new HashMap(2);
        paramMap.put("ids", documentIdList);

        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .delete(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求ragflow删除文档处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            if (null == map || (Integer) map.get("code") != 0) {
                String errorMessage = (String) map.get("message");
                log.error("ragflow删除文档处理接口失败，失败原因{}", errorMessage);
                return false;
            }
            return true;
        } catch (IOException e) {
            log.error("ragflow删除文档处理接口，失败原因{}", e.getMessage());
            return false;
        } finally {
            if (null != response) {
                response.close();
            }
        }
    }
}
