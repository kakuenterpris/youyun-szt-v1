package com.thtf.op.repo.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.thtf.emdedding.constants.CommonConstants;
import com.thtf.emdedding.dto.WonderfulPenSyncDTO;
import com.thtf.global.common.cache.RedisUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.op.entity.BusResourceFolderEntity;
import com.thtf.op.entity.RagflowEntity;
import com.thtf.op.mapper.BusResourceFolderMapper;
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

        String apiKey = "ragflow-RiZDg3ZjBjMzQ2MTExZjBiNTE0YmU3ZG"; //(String) redisUtil.get("ragflow:authHeader");  //ragFlowApiConfigProperties.getApiKey();
        if (StrUtil.isEmpty(apiKey)) {
            RagflowEntity ragflowEntity = new RagflowEntity();
            ragflowEntity.setEmail("M@M.test");
            ragflowEntity.setPassword("opGETT2FDaJyhPjwvQYQlg2TWUN2CXk92bUeFbNm8e/Z5n9c9N2/zJsAQzidMJKRnokG3I46wemCiBpFBHiPjZaJz9nJ+6lCP/d7t08H6zV/xq6bETAr1qjOR8gizvUDdm+RQIrql/VPt1YfHNlYYkmu4z4JPQjWKzZBUgbuC7EF75Zc3gpp60KKG0S+OP3MdPRmobwmN3JaSlAghOu9kuIBDQ8wO+rZQVgyjKYS722EBfehSNSCC/zkCg3YSbXSHd3j9z+eiBP2KOOq/rYNal2H53zEzbMdwpRvlyc4fj0osPF+og19gHQYzFE1o1xIrDky1+wkRRiDYdOm4FLF+Q==");
            apiKey = ragFlowProcessServiceImpl.loginRagFlow(ragflowEntity);
        }
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
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);

            return RestResponse.success(jsonString);

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
        return RestResponse.success("预览成功");
    }
}




