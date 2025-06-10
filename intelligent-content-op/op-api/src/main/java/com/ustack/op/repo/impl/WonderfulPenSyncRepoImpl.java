package com.ustack.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.PushFileDTO;
import com.ustack.emdedding.dto.RagProcessDTO;
import com.ustack.emdedding.dto.WonderfulPenSyncDTO;
import com.ustack.feign.client.FileApi;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.BusResourceFileEntity;
import com.ustack.op.entity.BusResourceFolderEntity;
import com.ustack.op.entity.RelUserResourceEntity;
import com.ustack.op.mapper.*;
import com.ustack.op.properties.RagFlowApiConfigProperties;
import com.ustack.op.repo.RelUserResourceRepo;
import com.ustack.op.repo.WonderfulPenSyncRepo;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.ResourceProcessService;
import com.ustack.op.service.impl.KmServiceImpl;
import com.ustack.op.service.impl.TreeNodeServiceImpl;
import com.ustack.resource.dto.BusResourceFileDTO;
import com.ustack.resource.dto.BusResourceManageListDTO;
import com.ustack.resource.param.SaveFileParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class WonderfulPenSyncRepoImpl extends ServiceImpl<BusResourceFolderMapper, BusResourceFolderEntity>
        implements WonderfulPenSyncRepo {

    @Autowired
    private KmServiceImpl kmService;

    @Autowired
    RagFlowApiConfigProperties ragFlowApiConfigProperties;

    @Autowired
    BusResourceDatasetMapper busResourceDatasetMapper;

    @Autowired
    RelUserResourceMapper relUserResourceMapper;

    private final FileApi fileApi;

    @Autowired
    BusResourceFileMapper busResourceFileMapper;

    @Autowired
    RagFlowProcessService ragFlowProcessService;

    @Autowired
    BusResourceFolderMapper busResourceFolderMapper;

    @Autowired
    BusUserInfoMapper busUserInfoMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse pushFile(PushFileDTO pushFileDTO) {
        List<WonderfulPenSyncDTO> dtoList = pushFileDTO.getPushList();
        // 向量化所需list
        List<RagProcessDTO> fileIdList = new ArrayList<>();

        for (WonderfulPenSyncDTO dto : dtoList) {
            String url = dto.getUrl();
            String[] split = url.split("\\?");
            String Filetype = split[0].substring(split[0].lastIndexOf(".") + 1);
            if (StringUtil.isEmpty(url)) {
                return RestResponse.error("请求路径为空!");
            }
            String fileName = dto.getFileName();
            //        获取文件名（不包含后缀）
            String subName = fileName;
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .build();
            Response response = null;
            try {
                //            妙笔请求获取文件
                response = client.newCall(request).execute();
                InputStream inputStream = new ByteArrayInputStream(response.body().bytes());
                float size = ((float) response.body().contentLength()) / 1024;
                MultipartFile file = new MockMultipartFile("file", fileName + "." + Filetype, ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
                //          上传文件
                RestResponse updateResponse = fileApi.updateFile(file, null, null, null);
                SaveFileParam saveFileParam = new SaveFileParam();
                //响应数据
                LinkedHashMap data = (LinkedHashMap) updateResponse.getData();
                List<BusResourceFileDTO> fileList = new ArrayList<>();
                BusResourceFileDTO busResourceFileDTO = new BusResourceFileDTO();
                busResourceFileDTO.setFileId(data.get("guid").toString());
                busResourceFileDTO.setName(subName);
                busResourceFileDTO.setFileType(Filetype);
                busResourceFileDTO.setSize(String.valueOf(size));
                String userId = dto.getUserId();
                busResourceFileDTO.setCreateUserId(userId);
                //默认配置项目
                busResourceFileDTO.setLevel(Integer.valueOf(dto.getSecurityLevel()));
                busResourceFileDTO.setScopeRule("ONLY_ME");
                //构造参数
                fileList.add(busResourceFileDTO);
                saveFileParam.setFileList(fileList);
                saveFileParam.setFolderId(304);
                // 保存文件
                RestResponse saveResponse = kmService.saveFile(saveFileParam);
                if (saveResponse.getCode() != 200 || saveResponse.getCode() != 200) {
                    return RestResponse.error("推送失败");
                }

                LambdaQueryWrapper<BusResourceFileEntity> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(BusResourceFileEntity::getFileId, data.get("guid").toString());
                queryWrapper.eq(BusResourceFileEntity::getDeleted, 0);
                BusResourceFileEntity busResourceFileEntity = busResourceFileMapper.selectOne(queryWrapper);
                if (busResourceFileEntity == null) {
                    log.error("文件不存在或已被删除");
                    return RestResponse.error("文件不存在或已被删除");
                }
                RagProcessDTO ragProcessDTO = new RagProcessDTO();
                ragProcessDTO.setFileId(data.get("guid").toString());
                ragProcessDTO.setResourceId(busResourceFileEntity.getId());
                fileIdList.add(ragProcessDTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return RestResponse.success(fileIdList);
    }

    @Override
    public RestResponse getFileByUserId(WonderfulPenSyncDTO dto) {

        //测试 写死
        SystemUser currentUser = ContextUtil.currentUser();

        currentUser.setUserId("798");

        List<BusResourceManageListDTO> busResourceManageListDTOS = TreeNodeServiceImpl.assembleTree(kmService.getResourceListLeft("wonderfulPen", Integer.parseInt(dto.getType()), currentUser));
        return RestResponse.success(busResourceManageListDTOS);
    }

    @Override
    public RestResponse getKonwledgeByUserId(WonderfulPenSyncDTO dto) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        String apiKey = ragFlowApiConfigProperties.getApiKey();

        String url = ragFlowApiConfigProperties.getRagflowUrl();
        if (StrUtil.isEmpty(apiKey) || StrUtil.isEmpty(url)) {
            log.error("上传文档得url或key为空");
            return RestResponse.error("上传文档得url或key为空");
        }

        Gson gson = new Gson();
        HashMap<Object, Object> params = new HashMap<>();
        params.put("question", dto.getQuery()); //query

        //文件id集合
        List<String> list = new ArrayList<>();

        ArrayList<String> folderIdList = new ArrayList<>();
        //通过folderId和type获取文件id
        List<String> fileIds = new ArrayList<>();
        for (Map<String, Object> map : dto.getFolderIds()) {
            String type = map.get("type").toString();
            String folderIdStr = map.get("folderId").toString();
            if (StrUtil.isBlank(folderIdStr)) {
                continue;
            }
            String[] folderIds = folderIdStr.split(",");
            for (String folderId : folderIds) {
                folderIdList.add(folderId);
                fileIds.addAll(getSubFilesRecursively(Integer.valueOf(folderId), Integer.parseInt(type)));
            }
        }

        List<String> datasetIds = busResourceDatasetMapper.listDatasetsIdByFolderIds(folderIdList);
        params.put("dataset_ids", datasetIds);

        LambdaQueryWrapper<RelUserResourceEntity> queryWrapper = Wrappers.lambdaQuery();
        if (!CollUtil.isEmpty(fileIds)) {
            queryWrapper.in(RelUserResourceEntity::getFileId, fileIds);
        }
        queryWrapper.eq(RelUserResourceEntity::getIndexingStatus, "2");
        List<RelUserResourceEntity> entitys = relUserResourceMapper.selectList(queryWrapper);
        entitys.forEach(entity -> {
            list.add(entity.getDocumentId());
        });

        //测试 写死
//        list.add("ddb0407c36d711f0b0020a7f8ad6111b");
//        list.add("ec2aa81e36bd11f09635ce6a55565431");

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
//                documentId = RandomUtil.randomString(10);
                //测试随机成成id
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

    //根据文件夹id递归获取全部子文件
    private List<String> getSubFilesRecursively(Integer folderId, Integer type) {
        List<String> subFiles = new ArrayList<>();

        // 获取当前文件夹下的直接子文件
        List<String> directSubFiles = busResourceFileMapper.selectFileIdsByFolderId(folderId);
        subFiles.addAll(directSubFiles);

        // 获取当前文件夹下的直接子文件夹
        List<BusResourceFolderEntity> subFolders = busResourceFolderMapper.listByParentIdAndTypeFolder(folderId, type);

        if (subFiles.isEmpty()) {
            return subFiles;
        }

        for (BusResourceFolderEntity subFolder : subFolders) {
            // 递归调用获取子文件夹下的子文件
            List<String> subFolderFiles = getSubFilesRecursively(Integer.parseInt(subFolder.getId().toString()), type);
            subFiles.addAll(subFolderFiles);
        }

        return subFiles;
    }


    @Override
    public RestResponse getFileInfo(WonderfulPenSyncDTO dto) {
        String base64Md = ragFlowProcessService.getRagFlowMD(dto.getFileId());
        byte[] mdBytes = Base64.getDecoder().decode(base64Md);

        Integer level = busResourceFileMapper.selectLevelByFileId(dto.getFileId());

        try (ByteArrayInputStream bais = new ByteArrayInputStream(mdBytes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             InputStreamReader isr = new InputStreamReader(bais, StandardCharsets.UTF_8);
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {

            char[] buffer = new char[1024];
            int length;
            while ((length = isr.read(buffer)) != -1) {
                osw.write(buffer, 0, length);
            }
            osw.flush();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", baos.toString());
            jsonObject.put("level", levelTransfer(level));
            return RestResponse.success(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return RestResponse.error("文件不存在");
    }


    public RestResponse selectFileByIds(WonderfulPenSyncDTO dto) {


        List<BusResourceFileEntity> busResourceFileEntities = busResourceFileMapper.selectFileByIds(dto.getFileIds());

        for (BusResourceFileEntity busResourceFileEntity : busResourceFileEntities) {
            busResourceFileEntity.setLevelWonderPen(levelTransfer(busResourceFileEntity.getLevel()));
        }
        return RestResponse.success(busResourceFileEntities);
    }


    public String levelTransfer(Integer level) {
        switch (level) {
            case 1:
                return "GK";
            case 2:
                return "NB";
            case 3:
                return "JM";
            case 4:
                return "MM";
            default:
                return "未知";
        }
    }
}




