package com.ustack.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.util.StringUtil;
import com.google.gson.Gson;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.QueryKmDTO;
import com.ustack.emdedding.dto.RagProcessDTO;
import com.ustack.feign.client.KbaseApi;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.dto.BusUserInfoDTO;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.RagflowEntity;
import com.ustack.op.entity.RelUserResourceEntity;
import com.ustack.op.mapper.FileEmbeddingConfigMapper;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.properties.RagFlowApiConfigProperties;
import com.ustack.op.repo.*;
import com.ustack.op.runnable.RagFlowProcessRunnable;
import com.ustack.op.runnable.SyncJoinStatusRunnable;
import com.ustack.op.service.EmbeddingService;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.RelUserResourceService;
import com.ustack.op.service.ResourceProcessService;
import com.ustack.op.util.OKHttpUtils;
import com.ustack.resource.enums.IndexingStatusEnum;
import com.ustack.resource.enums.ResourceErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
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
public class ResourceProcessServiceImpl implements ResourceProcessService {


    private final FileUploadRecordMapper fileUploadRecordMapper;

    private final EmbeddingService embeddingService;

    private final KbaseApi kbaseApi;

    private final RagFlowProcessService ragFlowProcessService;

    private final RelUserResourceService relUserResourceService;

    private final FileEmbeddingConfigMapper fileEmbeddingConfigMapper;

    private final BusUserInfoRepo userInfoRepo;
    private final BusResourceDatasetRepo datasetRepo;

    private final BusResourceFileRepo busResourceFileRepo;

    private final BusResourceFolderRepo busResourceFolderRepo;

    private final SysRuleTagRepo sysRuleTagRepo;


    @Value("${file.base.path}")
    private String fileBasePath;

    private final ObjectProvider<ResourceProcessService> resourceProcessServiceProvider;



    /**
     * 将资源上传到ragflow进行解析
     *
     * @param fileIdList
     */
    @Override
    public RestResponse execute(List<RagProcessDTO> fileIdList) {
        ThreadPoolExecutor singleThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000));
        try {
            RagFlowProcessRunnable ragFlowProcessRunnable = new RagFlowProcessRunnable(
                            fileIdList,
                            ragFlowProcessService,
                            relUserResourceService,
                            fileUploadRecordMapper,
                            fileEmbeddingConfigMapper,
                            fileBasePath,
                            datasetRepo,
                    resourceProcessServiceProvider.getIfAvailable(),
                    sysRuleTagRepo,
                    busResourceFileRepo,
                    busResourceFolderRepo
                        );
//            singleThreadExecutor.submit(ragFlowProcessRunnable);
            // 使用Future获取返回值
            // 提交任务并获取Future
            Future<RestResponse> future = singleThreadExecutor.submit(() -> {
//                ragFlowProcessRunnable.run();
//                return ragFlowProcessRunnable.getProcessResult(); // 新增结果获取方法
                return ragFlowProcessRunnable.handler(); // 直接调用业务处理方法

            });
            RestResponse result = future.get(60, TimeUnit.SECONDS);
            return RestResponse.success(result.getData());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("将资源上传到ragflow解析任务失败,失败原因:" + e);
            return RestResponse.fail(500, "任务处理超时");

        } finally {
            singleThreadExecutor.shutdown();
        }
    }

    /**
     * 根据内容向量化查询关联文档
     *
     * @param content
     * @return
     */
    private List<Map> queryPersonal(String content, String userId) {
        if (StrUtil.isEmpty(content) || StrUtil.isEmpty(userId)) {
            log.error("参数为空，无法进行个人向量查询");
            return null;
        }
        // 判断内容超过3000字符，截取前2000（后面可能会分词处理）
        if (content.length() > 3000) {
            content = content.substring(0, 2000);
        }
        try {
            // 将内容转为向量化
            String embedding = embeddingService.embedding(content);
            if (StringUtil.isEmpty(embedding)) {
                return null;
            }

            // 将向量化的内容作为条件查询关联文档
            RestResponse response = kbaseApi.queryPersonalByEmbedding(embedding, userId);
            if (null != response && response.getCode() == 200) {
                Map dataMap = (Map) response.getData();
                return (List<Map>) dataMap.get("data");
            }
        } catch (Exception e) {
            log.error("向量化查询失败，失败原因{}", e);
            return null;
        }
        return null;
    }

    private List<Map> queryDepartment(String content, String depNum) {
        if (StrUtil.isEmpty(content) || StrUtil.isEmpty(depNum)) {
            log.error("参数为空，无法进行部门向量查询");
            return null;
        }
        // 判断内容超过3000字符，截取前2000（后面可能会分词处理）
        if (content.length() > 3000) {
            content = content.substring(0, 2000);
        }
        try {
            // 将内容转为向量化
            String embedding = embeddingService.embedding(content);
            if (StringUtil.isEmpty(embedding)) {
                return null;
            }

            // 将向量化的内容作为条件查询关联文档
            RestResponse response = kbaseApi.queryDepartmentByEmbedding(embedding, depNum);
            if (null != response && response.getCode() == 200) {
                Map dataMap = (Map) response.getData();
                return (List<Map>) dataMap.get("data");
            }
        } catch (Exception e) {
            log.error("向量化查询失败，失败原因{}", e);
            return null;
        }
        return null;
    }

    private List<Map> queryCompany(String content, String companyNum) {
        if (StrUtil.isEmpty(content) || StrUtil.isEmpty(companyNum)) {
            log.error("参数为空，无法进行企业向量查询");
            return null;
        }
        // 判断内容超过3000字符，截取前2000（后面可能会分词处理）
        if (content.length() > 3000) {
            content = content.substring(0, 2000);
        }
        try {
            // 将内容转为向量化
            String embedding = embeddingService.embedding(content);
            if (StringUtil.isEmpty(embedding)) {
                return null;
            }

            // 将向量化的内容作为条件查询关联文档
            RestResponse response = kbaseApi.queryCompanyByEmbedding(embedding, companyNum);
            if (null != response && response.getCode() == 200) {
                Map dataMap = (Map) response.getData();
                return (List<Map>) dataMap.get("data");
            }
        } catch (Exception e) {
            log.error("向量化查询失败，失败原因{}", e);
            return null;
        }
        return null;
    }


    /**
     * 查询当前用户相关知识库
     *
     * @param queryKmDTO
     * @return
     */
    @Override
    public RestResponse query(QueryKmDTO queryKmDTO) {
        if (StrUtil.isEmpty(queryKmDTO.getContent()) || StrUtil.isEmpty(queryKmDTO.getUserId())) {
            return RestResponse.fail(ResourceErrorCode.PARAM_NULL.getCode(), "参数为空，无法进行向量查询");
        }
        // 查询用户是否存在
        BusUserInfoDTO busUserInfoDTO = userInfoRepo.getByUserId(queryKmDTO.getUserId());
        if (null == busUserInfoDTO) {
            return RestResponse.fail(ResourceErrorCode.PARAM_NULL.getCode(), "用户不存在，无法进行向量查询");
        }
//        // 查询当前用户能看到的所有文件id
//        List<String> fileIdList = relUserResourceService.getFileIdListByUserId(queryKmDTO.getUserId());
        // 查询当前用户能看到的所有文件夹id
        List<String> folderIdIdList = relUserResourceService.getFolderIdListByUserId(queryKmDTO.getUserId());
        // 根据内容向量查询相关文档
        List<Map> kmList = this.queryKm(queryKmDTO.getContent(), folderIdIdList);
        // 取相关度大于等于30%的文档
        List<Map> resultList = new ArrayList<>();
        if (CollUtil.isNotEmpty(kmList)) {
            for (Map kmMap : kmList) {
                String relevancePercentStr = kmMap.get("relevance").toString();
                String numStr = relevancePercentStr.replace("%", "");
                Double percent = Double.parseDouble(numStr);
                if (percent < CommonConstants.RELEVANCE_MIN) {
                    continue;
                }
                resultList.add(kmMap);
            }
        }
        return RestResponse.success(resultList);
    }

    private List<Map> queryKm(String content, List<String> folderIdIdList) {
        if (StrUtil.isEmpty(content) || CollUtil.isEmpty(folderIdIdList)) {
            log.error("参数为空，无法进行部门向量查询");
            return null;
        }
        // 判断内容超过3000字符，截取前2000（后面可能会分词处理）
        if (content.length() > 3000) {
            content = content.substring(0, 2000);
        }
        try {
            // 将内容转为向量化
            String embedding = embeddingService.embedding(content);
            if (StringUtil.isEmpty(embedding)) {
                return null;
            }

            // 将向量化的内容作为条件查询关联文档
            RestResponse response = kbaseApi.queryByEmbeddingAndFolderIds(folderIdIdList, embedding);
            if (null != response && response.getCode() == 200) {
                Map dataMap = (Map) response.getData();
                return (List<Map>) dataMap.get("data");
            }
        } catch (Exception e) {
            log.error("向量化查询失败，失败原因{}", e);
            return null;
        }
        return null;
    }

    /**
     * 删除切片及向量化内容（物理删除）
     *
     * @param documentId
     * @param fileId
     * @param embeddingConfigCode
     * @return
     */
    @Override
    public boolean delete(Long resourceId, String documentId, String fileId, String embeddingConfigCode) {
        if (StrUtil.isEmpty(documentId) || StrUtil.isEmpty(fileId) || StrUtil.isEmpty(embeddingConfigCode)) {
            log.error("参数为空，无法删除切片和向量化数据");
            return false;
        }
        // 删除ragflow中的文档及切片
        String datesetId = fileEmbeddingConfigMapper.getDataSetId(embeddingConfigCode);
        boolean ragFlowDelete = ragFlowProcessService.delete(documentId, datesetId);
        if (!ragFlowDelete) {
            relUserResourceService.updateIndexStatus(resourceId, fileId, IndexingStatusEnum.DELETE_CHUNKS_ERROR.getIndexingStatus(), IndexingStatusEnum.DELETE_CHUNKS_ERROR.getIndexingStatusName());
            log.error("ragflow文档删除失败");
        }
        // 删除kbase中存储的向量
        RestResponse deleteResponse = kbaseApi.deleteByFileId(fileId);
//        switch (category) {
//            case "个人":
//                deleteResponse = kbaseApi.deletePersonalByFileId(fileId);
//                break;
//            case "部门":
//                deleteResponse = kbaseApi.deleteDepartmentByFileId(fileId);
//                break;
//            case "机构":
//                deleteResponse = kbaseApi.deleteCompanyByFileId(fileId);
//                break;
//            default:
//                break;
//        }
        if (null != deleteResponse && deleteResponse.getCode() == 200) {
            boolean delete = (boolean) deleteResponse.getData();
            if (!delete) {
                relUserResourceService.updateIndexStatus(resourceId, fileId, IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatus(), IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatusName());
                log.error("删除kbase中的向量数据失败");
            } else {
                return true;
            }

        } else {
            relUserResourceService.updateIndexStatus(resourceId, fileId, IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatus(), IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatusName());
            log.error("删除kbase中的向量数据失败");
        }

        return false;
    }

    /**
     * 更新知识库是否参与问答状态
     *
     * @param joinQuery
     * @param fileIdList
     */
    @Override
    public void updateJoinQuery(Boolean joinQuery, List<String> fileIdList) {
        if (CollUtil.isEmpty(fileIdList) || null == joinQuery) {
            return;
        }
        ThreadPoolExecutor singleThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000));
        try {
            SyncJoinStatusRunnable syncJoinStatusRunnable = new SyncJoinStatusRunnable(fileIdList, joinQuery, kbaseApi, relUserResourceService);
            singleThreadExecutor.submit(syncJoinStatusRunnable);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("将资源上传到ragflow解析任务失败,失败原因:" + e);
        } finally {
            singleThreadExecutor.shutdown();
        }
    }

    /**
     * 删除文件夹下的所有切片及向量化内容（物理删除）
     *
     * @param folderId
     * @param embeddingConfigCode
     * @return
     */
//    @Override
//    public boolean deleteByFolderId(String folderId, String embeddingConfigCode) {
//        if (StrUtil.isEmpty(folderId) || StrUtil.isEmpty(embeddingConfigCode)) {
//            log.error("参数为空，无法删除切片和向量化数据");
//            return false;
//        }
//        // 删除ragflow中的文档及切片
//        String datesetId = fileEmbeddingConfigMapper.getDataSetId(embeddingConfigCode);
//        // 获取文件夹下得所有已上传到ragflow的documentId
//        List<String> documentIdList = relUserResourceService.getDocumentIdListByFolderId(folderId);
//        boolean ragFlowDelete = ragFlowProcessService.delete(documentIdList, datesetId);
//        if (!ragFlowDelete) {
//            relUserResourceService.updateIndexStatusByFolderId(folderId, IndexingStatusEnum.DELETE_CHUNKS_ERROR.getIndexingStatus(), IndexingStatusEnum.DELETE_CHUNKS_ERROR.getIndexingStatusName());
//            log.error("ragflow文档删除失败");
//        }
//        // 删除kbase中存储的向量
//        RestResponse deleteResponse = kbaseApi.deleteByFolderId(folderId);
//        if (null != deleteResponse && deleteResponse.getCode() == 200) {
//            boolean delete = (boolean) deleteResponse.getData();
//            if (!delete) {
//                relUserResourceService.updateIndexStatusByFolderId(folderId, IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatus(), IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatusName());
//                log.error("删除kbase中的向量数据失败");
//            } else {
//                return true;
//            }
//
//        } else {
//            relUserResourceService.updateIndexStatusByFolderId(folderId, IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatus(), IndexingStatusEnum.DELETE_EMBEDDING_ERROR.getIndexingStatusName());
//            log.error("删除kbase中的向量数据失败");
//        }
//
//        return false;
//    }
}
