package com.thtf.op.repo.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.thtf.emdedding.constants.CommonConstants;
import com.thtf.emdedding.dto.WonderfulPenSyncDTO;
import com.thtf.global.common.cache.RedisUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.BusResourceFolderEntity;
import com.thtf.op.entity.RagflowEntity;
import com.thtf.op.mapper.BusResourceDatasetMapper;
import com.thtf.op.mapper.BusResourceFolderMapper;
import com.thtf.op.mapper.RelUserResourceMapper;
import com.thtf.op.properties.RagFlowApiConfigProperties;
import com.thtf.op.repo.WonderfulPenSyncRepo;
import com.thtf.op.service.impl.KmServiceImpl;
import com.thtf.op.service.impl.RagFlowProcessServiceImpl;
import com.thtf.op.service.impl.TreeNodeServiceImpl;
import com.thtf.resource.dto.BusResourceManageListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class WonderfulPenSyncRepoImpl extends ServiceImpl<BusResourceFolderMapper, BusResourceFolderEntity>
        implements WonderfulPenSyncRepo {

    @Autowired
    private KmServiceImpl kmService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RagFlowProcessServiceImpl ragFlowProcessServiceImpl;

    @Autowired
    RagFlowApiConfigProperties ragFlowApiConfigProperties;

    @Autowired
    BusResourceDatasetMapper busResourceDatasetMapper;

    @Autowired
    RelUserResourceMapper relUserResourceMapper;

    @Override
    public RestResponse pushFile(WonderfulPenSyncDTO dto) {


        return RestResponse.success("推送成功");
    }

    @Override
    public RestResponse getFileByUserId(WonderfulPenSyncDTO dto) {

        List<BusResourceManageListDTO> busResourceManageListDTOS = TreeNodeServiceImpl.assembleTree(kmService.getResourceListLeft("wonderfulPen", dto.getType()));
        return RestResponse.success(busResourceManageListDTOS);
    }

    @Override
    public RestResponse getKonwledgeByUserId(WonderfulPenSyncDTO dto) {


        String userId = dto.getUserId();
        String query = dto.getQuery();

        String datasetId = "51b9b08c361711f0a8f03e04d146f0ba";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        String apiKey = ragFlowApiConfigProperties.getApiKey();

        // String datasetId = ragFlowApiConfigProperties.getDatasetId();
        String url = ragFlowApiConfigProperties.getRagflowUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return RestResponse.error("上传文档得url或key为空");
        }

        Gson gson = new Gson();


        HashMap<Object, Object> params = new HashMap<>();
        List<String> list1 = new ArrayList<>();
        list1.add(datasetId);
        params.put("dataset_ids", list1);
        params.put("question", "物质");

        //文件id集合
        List<String> list = new ArrayList<>();

        list.add("ddb0407c36d711f0b0020a7f8ad6111b");
        list.add("ec2aa81e36bd11f09635ce6a55565431");


        params.put("document_ids", list);


        String json = gson.toJson(params);
        RequestBody requestBody = RequestBody.create(CommonConstants.JSON_MEDIA_TYPE, json);

        Request request = new Request.Builder()
                .url(url + "/api/v1/retrieval")
                .addHeader("Authorization", "Beare " + apiKey)
                .post(requestBody)
                .build();
        Response response = null;
        try {

            response = client.newCall(request).execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
//            byte[] responseBytes = response.body().bytes();
//            String jsonString = new String(responseBytes);
            if (jsonObject.get("data") != null) {
                JSONObject data = jsonObject.getJSONObject("data");
            }

            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            JSONArray chunks = jsonObject1.getJSONArray("chunks");
            List<Map<String, Object>> list2 = new ArrayList();
            for (int i = 0; i < chunks.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                JSONObject chunk = chunks.getJSONObject(i);
                map.put("content", chunk.get("content"));
                map.put("image_id", chunk.get("image_id"));
                map.put("term_similarity", chunk.get("term_similarity"));
                map.put("vector_similarity", chunk.get("vector_similarity"));
                map.put("similarity", chunk.get("similarity"));
                String documentId = relUserResourceMapper.getFileIdByDocumentId(chunk.get("document_id").toString());
                map.put("document_id", documentId);
                map.put("document_keyword", chunk.get("document_keyword"));
                list2.add(map);
            }

            return RestResponse.success(list2);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("ragflowragflow向量检索接口失败，失败原因:", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }

    }

    @Override
    public RestResponse getFileInfo(WonderfulPenSyncDTO dto) {


        String fileId = dto.getFileId();
        String userId = dto.getUserId();

        //通过userid和fileid查询ragflow表
//        String datasetId = busResourceDatasetMapper.listDatasetsIdByCreateUserId(userId);
        String datasetId = "51b9b08c361711f0a8f03e04d146f0ba";

//        String documentId = relUserResourceMapper.getDocumentIdByFileId(fileId);
        String documentId = "ea8b4cb836e811f0aaa00a7f8ad6111b";

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        String apiKey = ragFlowApiConfigProperties.getApiKey();

        String url = ragFlowApiConfigProperties.getRagflowUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url) || StrUtil.isEmpty(datasetId)) {
            log.error("上传文档得url或key为空");
            return RestResponse.error("上传文档得url或key为空");
        }

        String fullUrl = url + "/api/v1/datasets/" + datasetId + "/documents/" + documentId + "/chunks?page=1&page_size=100";


        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", "Beare " + apiKey)
                .get()
                .build();
        Response response = null;
        try {

            response = client.newCall(request).execute();
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            if (jsonObject.get("data") != null) {
                JSONObject data = jsonObject.getJSONObject("data");
            }

            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            JSONArray chunks = jsonObject1.getJSONArray("chunks");
            List<Map<String, Object>> list2 = new ArrayList();
            for (int i = 0; i < chunks.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                JSONObject chunk = chunks.getJSONObject(i);
                map.put("content", chunk.get("content"));
                map.put("available", chunk.get("available"));
                map.put("id", chunk.get("id"));
                String documentId1 = relUserResourceMapper.getFileIdByDocumentId(chunk.get("document_id").toString());
                map.put("document_id", documentId1);
                map.put("document_keyword", chunk.get("document_keyword"));
                list2.add(map);
            }

            return RestResponse.success(list2);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("ragflowragflow向量检索接口失败，失败原因:", e.getMessage());
            return null;
        } finally {
            if (null != response) {
                response.close();
            }
        }


//        return RestResponse.success("预览成功");
    }
}




