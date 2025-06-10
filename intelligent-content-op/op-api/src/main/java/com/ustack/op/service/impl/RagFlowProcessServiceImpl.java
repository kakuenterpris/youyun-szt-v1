package com.ustack.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.op.entity.*;
import com.ustack.op.enums.RagFlowStatusEnum;
import com.ustack.op.properties.RagFlowApiConfigProperties;
import com.ustack.op.repo.BusUserInfoRepo;
import com.ustack.op.repo.SysOptLogRepo;
import com.ustack.op.repo.impl.RelUserResourceRepoImpl;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.util.OKHttpUtils;
import com.ustack.resource.enums.IndexingStatusEnum;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@Service
@Slf4j
public class RagFlowProcessServiceImpl implements RagFlowProcessService {

    @Autowired
    RagFlowApiConfigProperties ragFlowApiConfigProperties;

    @Autowired
    private RedisUtil redisUtil;

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    @Autowired
    private RelUserResourceRepoImpl relUserResourceRepoImpl;

    @Autowired
    private BusUserInfoRepo busUserInfoRepo;

    @Autowired
    private OKHttpUtils okHttpUtil;

    // 操作日志
    @Autowired
    private SysOptLogRepo sysOptLogRepo;


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
        String url = ragFlowApiConfigProperties.getApiHost()+"/api/v1/datasets/"+datasetId+"/documents";
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return null;
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer "+ apiKey)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            System.out.println("ragflow返回值:===>" + jsonString);
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
    public boolean parseFile(String datasetId, String uploadFileId, BusResourceFileEntity fileEntity, BusResourceFolderEntity folderEntity) {
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
        String url = ragFlowApiConfigProperties.getApiHost()+"/api/v1/datasets/"+datasetId+"/chunks";
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return false;
        }
        Map paramMap = new HashMap();
        List<String> fileIdList = new ArrayList<>();
        fileIdList.add(uploadFileId);
        paramMap.put("document_ids", fileIdList);
        Gson gson = new Gson();
        String json = gson.toJson(paramMap);
        RequestBody body = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", "Bearer "+ apiKey)
                .post(body)
                .build();
        Response response = null;
        try {
            StopWatch watch = new StopWatch("请求ragflow解析文档处理接口");
            watch.start();
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            System.out.println("请求ragflow解析文档处理接口返回值:===>" + jsonString);
            watch.stop();
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            // 记录操作日志
            String operateContent = RagFlowStatusEnum.PRASE_FILE.getTypeName() + fileEntity.getName();
            sysOptLogRepo.saveLog(operateContent, Long.valueOf(
                            fileEntity.getFolderId()),
                    Long.valueOf(folderEntity.getParentId()),
                    RagFlowStatusEnum.UPLOAD_RAG.getTypeName(),
                    1);
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

    @Override
    public String loginRagFlow(RagflowEntity ragflowEntity) {
        Map<String, String> params = new HashMap<>();
        String url = ragFlowApiConfigProperties.getLoginUrl() + "/user/login";
        params.put("email", ragflowEntity.getEmail());
        params.put("password", ragflowEntity.getPassword());

        Gson gson = new Gson();
        String json = gson.toJson(params);
        log.info("调用ragflow方法post请求参数：{}", json);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;
        String authHeader = "";
        try {
            response = client.newCall(request).execute();
            Headers headers = response.headers();
            authHeader = headers.get("Authorization");
            redisUtil.set("ragflow:authHeader", authHeader, 1800);
        }catch (Exception e) {
            log.error("调用ragflow方法post失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }

        return authHeader;
    }

    @Override
    public String createRagFlow(String filenName) {

        Map params = new HashMap();
        params.put("name", filenName);
        String url = ragFlowApiConfigProperties.getApiHost() +"/api/v1/datasets";
        Gson gson = new Gson();
        String json = gson.toJson(params);
        log.info("调用ragflow方法post请求参数：{}", json);
        String apiKey = ragFlowApiConfigProperties.getApiKey();
        log.info("调用ragflow方法apiKey：{}", apiKey);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        Map map = okHttpUtil.doPost(request);
        if (null!= map && (Integer) map.get("code") == 0) {
            Map<String, Object> data = (Map<String, Object>) map.get("data");
            return (String) data.get("id");
        }
        return "";
    }


    /**
     * 调用ragflow方法，修改解析器
     * @param ruleTagList
     * @param docId
     * @return
     */
    @Override
    public String changeParser(List<SysRuleTagEntity> ruleTagList,String docId) {
        Map<String, String> params = new HashMap<>();
        Map<String, Object> parserConfig = new HashMap<>();
        Map<String, Object> extractor = new HashMap<>();
        List<Map<String, Object>> keyValues = new ArrayList<>();
        String url = ragFlowApiConfigProperties.getLoginUrl() + "/document/change_parser";

        params.put("parser_id","naive");
        params.put("doc_id", docId);

        parserConfig.put("auto_keywords", 0);
        parserConfig.put("auto_questions", 0);
        for (SysRuleTagEntity ruleTagEntity : ruleTagList) {
            Map<String, Object> kv = new HashMap<>();
            kv.put("code", ruleTagEntity.getTagCode());
            kv.put("id", ruleTagEntity.getId());
            kv.put("must_exist", true);  // 根据业务需求设置
            kv.put("name", ruleTagEntity.getTagName());
            kv.put("type", ruleTagEntity.getTagType());
            keyValues.add(kv);
        }

        extractor.put("keyvalues", keyValues);

        parserConfig.put("extractor", extractor);

        Map<String, Object> raptor = new HashMap<>();
        raptor.put("use_raptor", false);
        parserConfig.put("raptor", raptor);

        parserConfig.put("chunk_token_num", 128);
        parserConfig.put("delimiter", "\n");
        parserConfig.put("pages", new HashMap<>());

        params.put("parser config", parserConfig.toString());
        Gson gson = new Gson();
        String json = gson.toJson(params);
        log.info("调用ragflow方法post请求参数：{}", json);

        String apiKey = "";// (String) redisUtil.get("ragflow:authHeader");
        if (StrUtil.isEmpty(apiKey)) {
            RagflowEntity ragflowEntity = new RagflowEntity();
            ragflowEntity.setEmail("M@M.test");
            ragflowEntity.setPassword("opGETT2FDaJyhPjwvQYQlg2TWUN2CXk92bUeFbNm8e/Z5n9c9N2/zJsAQzidMJKRnokG3I46wemCiBpFBHiPjZaJz9nJ+6lCP/d7t08H6zV/xq6bETAr1qjOR8gizvUDdm+RQIrql/VPt1YfHNlYYkmu4z4JPQjWKzZBUgbuC7EF75Zc3gpp60KKG0S+OP3MdPRmobwmN3JaSlAghOu9kuIBDQ8wO+rZQVgyjKYS722EBfehSNSCC/zkCg3YSbXSHd3j9z+eiBP2KOOq/rYNal2H53zEzbMdwpRvlyc4fj0osPF+og19gHQYzFE1o1xIrDky1+wkRRiDYdOm4FLF+Q==");
            apiKey = loginRagFlow(ragflowEntity);
//            redisUtil.set("ragflow:authHeader", apiKey, 60*60*6);
        }
        log.info("调用ragflow方法apiKey：{}", apiKey);


        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", apiKey)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;
        String jsonString = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            jsonString = new String(responseBytes);
            log.info("调用ragflow修改配置响应：{}", jsonString);
        }catch (Exception e) {
            log.error("调用ragflow方法修改配置失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
        return jsonString;

    }



    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public String getRagFlowStatus() {
        LambdaQueryWrapper<BusUserInfoEntity> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(BusUserInfoEntity::getIsDeleted, 0);
        List<BusUserInfoEntity> userInfoEntityList = busUserInfoRepo.list(lambdaQuery);
        for (BusUserInfoEntity userInfoEntity : userInfoEntityList) {
            String userId = userInfoEntity.getUserId();
            LambdaQueryWrapper<RelUserResourceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RelUserResourceEntity::getUserId, userId);
            lambdaQueryWrapper.eq(RelUserResourceEntity::getDeleted, 0);
            lambdaQueryWrapper.eq(RelUserResourceEntity::getIndexingStatus, IndexingStatusEnum.PARSING.getIndexingStatus());
            List<RelUserResourceEntity> userResourceEntityList = relUserResourceRepoImpl.list(lambdaQueryWrapper);

            if (CollUtil.isEmpty(userResourceEntityList)){
                return "全部解析完成！";
            }

            List<String> documentIds = userResourceEntityList.stream()
                    .map(RelUserResourceEntity::getDocumentId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

    //        if (StrUtil.isEmpty(docId)){
    //            return RestResponse.success("未选中数据");
    //        }
    //        LambdaQueryWrapper<RelUserResourceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
    //        lambdaQueryWrapper.eq(RelUserResourceEntity::getFileId, docId);
    //        RelUserResourceEntity userResourceEntity = relUserResourceRepoImpl.getOne(lambdaQueryWrapper);
    //        if (StrUtil.isEmpty(userResourceEntity.getDocumentId())){
    //            log.error("还没做知识化提取操作");
    //            return RestResponse.error("还没做知识化提取操作");
    //        }
            String apiKey = "";// (String) redisUtil.get("ragflow:authHeader");
            if (StrUtil.isEmpty(apiKey)) {
                RagflowEntity ragflowEntity = new RagflowEntity();
                ragflowEntity.setEmail("M@M.test");
                ragflowEntity.setPassword("opGETT2FDaJyhPjwvQYQlg2TWUN2CXk92bUeFbNm8e/Z5n9c9N2/zJsAQzidMJKRnokG3I46wemCiBpFBHiPjZaJz9nJ+6lCP/d7t08H6zV/xq6bETAr1qjOR8gizvUDdm+RQIrql/VPt1YfHNlYYkmu4z4JPQjWKzZBUgbuC7EF75Zc3gpp60KKG0S+OP3MdPRmobwmN3JaSlAghOu9kuIBDQ8wO+rZQVgyjKYS722EBfehSNSCC/zkCg3YSbXSHd3j9z+eiBP2KOOq/rYNal2H53zEzbMdwpRvlyc4fj0osPF+og19gHQYzFE1o1xIrDky1+wkRRiDYdOm4FLF+Q==");
                apiKey = loginRagFlow(ragflowEntity);
//                redisUtil.set("ragflow:authHeader", apiKey, 60*60*6);
            }

            Map<String, Object> params = new HashMap<>();
            params.put("doc_ids", documentIds);
    //        params.put("doc_id", userResourceEntity.getDocumentId());
            params.put("page", "1");
            params.put("page_size", "100");

            Gson gson = new Gson();
            String json = gson.toJson(params);
            log.info("调用ragflow方法post请求参数：{}", json);
            RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);

            String url = ragFlowApiConfigProperties.getLoginUrl() +"/document/infos";
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            String finalUrl = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .post(body)
                    .addHeader("Authorization", apiKey)
                    .addHeader("Content-Type","application/json")
                    .addHeader("Connection", "keep-alive ")
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
                log.info("调用ragflow方法get响应：{}", jsonString);
                if (StringUtils.isEmpty(jsonString)) {
                    return null;
                }
                Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
                int proNum=0;
                int failNum=0;
                int successNum=0;
                for (Map<String, Object> data : (List<Map<String, Object>>) map.get("data")) {
                    String fileId = (String) data.get("id");
                    BigDecimal progress = (BigDecimal) data.get("progress");
                    if (StrUtil.isNotEmpty(fileId)) {
                        for (RelUserResourceEntity userResourceEntity : userResourceEntityList) {
                            if (fileId.equals(userResourceEntity.getDocumentId())) {
                                userResourceEntity.setProgress(progress);
                                // progress ==-1 ,解析异常
                                if (progress.compareTo(new BigDecimal(-1)) == 0){
                                    failNum++;
                                    userResourceEntity.setIndexingStatus(IndexingStatusEnum.PARSE_ERROR.getIndexingStatus());
                                    userResourceEntity.setIndexingStatusName(IndexingStatusEnum.PARSE_ERROR.getIndexingStatusName());
                                }else {
                                    // progress < 1.0 ,解析中
                                    if (progress.compareTo(BigDecimal.ONE) < 0) {
                                        proNum++;
                                        userResourceEntity.setIndexingStatus(IndexingStatusEnum.PARSING.getIndexingStatus());
                                        userResourceEntity.setIndexingStatusName(IndexingStatusEnum.PARSING.getIndexingStatusName());
                                    }else{
                                        successNum++;
                                        // progress == 1.0 ,解析完成
                                        userResourceEntity.setIndexingStatus(IndexingStatusEnum.COMPLETED.getIndexingStatus());
                                        userResourceEntity.setIndexingStatusName(IndexingStatusEnum.COMPLETED.getIndexingStatusName());
                                    }
                                }
                                relUserResourceRepoImpl.updateById(userResourceEntity);
                            }
                        }
                    }
                }
                return "本次执行结果：==========>>>"+"解析中"+proNum+"个， ### "+"解析完成"+successNum+"个， ### "+"解析失败"+failNum+"个";
            } catch (Exception e) {
                log.error("调用ragflow方法get，失败原因：" + e.getMessage());
                e.getMessage();
                return e.getMessage();
            } finally {
                response.close();
            }
        }
        return "解析完成";
    }


    @Override
    public String getRagFlowMD(String docId) {
        LambdaQueryWrapper<RelUserResourceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RelUserResourceEntity::getFileId, docId);
        RelUserResourceEntity userResourceEntity = relUserResourceRepoImpl.getOne(lambdaQueryWrapper);
        if (StrUtil.isEmpty(userResourceEntity.getDocumentId())){
            log.error("获取MD失败，文档ID为空");
            return "获取MD失败，文档ID为空";
        }
        String url = ragFlowApiConfigProperties.getLoginUrl() + "/document/get_md/"+userResourceEntity.getDocumentId();
        String apiKey = "";// (String) redisUtil.get("ragflow:authHeader");
        if (StrUtil.isEmpty(apiKey)) {
            RagflowEntity ragflowEntity = new RagflowEntity();
            ragflowEntity.setEmail("M@M.test");
            ragflowEntity.setPassword("opGETT2FDaJyhPjwvQYQlg2TWUN2CXk92bUeFbNm8e/Z5n9c9N2/zJsAQzidMJKRnokG3I46wemCiBpFBHiPjZaJz9nJ+6lCP/d7t08H6zV/xq6bETAr1qjOR8gizvUDdm+RQIrql/VPt1YfHNlYYkmu4z4JPQjWKzZBUgbuC7EF75Zc3gpp60KKG0S+OP3MdPRmobwmN3JaSlAghOu9kuIBDQ8wO+rZQVgyjKYS722EBfehSNSCC/zkCg3YSbXSHd3j9z+eiBP2KOOq/rYNal2H53zEzbMdwpRvlyc4fj0osPF+og19gHQYzFE1o1xIrDky1+wkRRiDYdOm4FLF+Q==");
            apiKey = loginRagFlow(ragflowEntity);
//            redisUtil.set("ragflow:authHeader", apiKey, 60*60*6);
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization",  apiKey)
                .get()
                .build();
        // 执行请求
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("MD请求失败，状态码：{}", response.code());
                return response.message();
            }

            // 处理二进制PDF数据
            byte[] mDBytes = response.body().bytes();
            return Base64.getEncoder().encodeToString(mDBytes); // 返回Base64编码字符串
        } catch (IOException e) {
            log.error("获取MD失败，原因：{}", e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public String getRagFlowPDF(String docId) {
        LambdaQueryWrapper<RelUserResourceEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RelUserResourceEntity::getFileId, docId);
        RelUserResourceEntity userResourceEntity = relUserResourceRepoImpl.getOne(lambdaQueryWrapper);
        if (StrUtil.isEmpty(userResourceEntity.getDocumentId())){
            log.error("获取MD失败，文档ID为空");
            return "获取PDF失败，文档ID为空";
        }
        String url = ragFlowApiConfigProperties.getLoginUrl() + "/document/get_layout/"+userResourceEntity.getDocumentId();
        String apiKey = "";// (String) redisUtil.get("ragflow:authHeader");
        if (StrUtil.isEmpty(apiKey)) {
            RagflowEntity ragflowEntity = new RagflowEntity();
            ragflowEntity.setEmail("M@M.test");
            ragflowEntity.setPassword("opGETT2FDaJyhPjwvQYQlg2TWUN2CXk92bUeFbNm8e/Z5n9c9N2/zJsAQzidMJKRnokG3I46wemCiBpFBHiPjZaJz9nJ+6lCP/d7t08H6zV/xq6bETAr1qjOR8gizvUDdm+RQIrql/VPt1YfHNlYYkmu4z4JPQjWKzZBUgbuC7EF75Zc3gpp60KKG0S+OP3MdPRmobwmN3JaSlAghOu9kuIBDQ8wO+rZQVgyjKYS722EBfehSNSCC/zkCg3YSbXSHd3j9z+eiBP2KOOq/rYNal2H53zEzbMdwpRvlyc4fj0osPF+og19gHQYzFE1o1xIrDky1+wkRRiDYdOm4FLF+Q==");
            apiKey = loginRagFlow(ragflowEntity);
//            redisUtil.set("ragflow:authHeader", apiKey, 60*60*6);
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization",  apiKey)
                .get()
                .build();
        // 执行请求
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("PDF请求失败，状态码：{}", response.code());
                return response.message();
            }

            // 处理二进制PDF数据
            byte[] pdfBytes = response.body().bytes();
            return Base64.getEncoder().encodeToString(pdfBytes); // 返回Base64编码字符串
        } catch (IOException e) {
            log.error("获取PDF失败，原因：{}", e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public Boolean updateDataset(String datasetId, String newName) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        String apiKey = ragFlowApiConfigProperties.getApiKey();
        String url = ragFlowApiConfigProperties.getApiHost() + "/api/v1/datasets/" + datasetId;

        // 构建请求体
        Map<String, Object> params = new HashMap<>();
        params.put("name", newName);
        Gson gson = new Gson();
        String json = gson.toJson(params);

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("更新数据集失败，状态码：{}，响应：{}", response.code(), response.body().string());
                return false;
            }

            // 解析响应
            String responseBody = response.body().string();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);
            return (Integer) result.get("code") == 0;
        } catch (IOException e) {
            log.error("更新数据集请求异常：{}", e.getMessage());
            return false;
        }
    }

}
