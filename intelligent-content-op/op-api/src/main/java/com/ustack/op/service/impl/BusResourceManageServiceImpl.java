package com.ustack.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.ustack.emdedding.dto.FileUploadRecordDTO;
import com.ustack.emdedding.dto.RagProcessDTO;
import com.ustack.feign.client.FileApi;
import com.ustack.file.dto.SyncFileDTO;
import com.ustack.op.entity.*;
import com.ustack.op.mapper.FileEmbeddingConfigMapper;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.mappings.*;
import com.ustack.op.properties.AiConfigProperties;
import com.ustack.op.properties.ApikeyConfigProperties;
import com.ustack.op.properties.DatasetsConfigProperties;
import com.ustack.op.repo.*;
import com.ustack.op.service.BusResourceManageService;
import com.ustack.op.service.KmService;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.ResourceProcessService;
import com.ustack.op.util.HttpUtils;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.Linq;
import com.ustack.login.dto.BusDepInfoDTO;
import com.ustack.login.enums.UserSpecialAuthEnum;
import com.ustack.resource.constants.ServiceConstants;
import com.ustack.resource.dto.*;
import com.ustack.resource.enums.*;
import com.ustack.resource.vo.BusResourceFavoriteVO;
import com.ustack.resource.vo.BusResourceFileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Service实现
 * @createDate 2025-02-18 17:57:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusResourceManageServiceImpl implements BusResourceManageService {
    private final AiConfigProperties aiConfigProperties;
    private final ApikeyConfigProperties apikeyConfigProperties;
    private final DatasetsConfigProperties datasetsConfigProperties;
    private final FileApi fileApi;

    private final BusResourceManageRepo busResourceManageRepo;
    private final RelUserResourceRepo relUserResourceRepo;
    private final BusDepInfoRepo depInfoRepo;
    private final FileEmbeddingConfigRepo embeddingConfigRepo;
    private final BusResourceEmbeddingRepo embeddingRepo;
    private final BusResourceDatasetRepo datasetRepo;
    private final BusResourceManageMapping busResourceManageMapping;
    private final BusResourceEmbeddingMapping embeddingMapping;
    private final ResourceProcessService resourceProcessService;
    private final FileEmbeddingConfigMapper fileEmbeddingConfigMapper;
    private final RagFlowProcessService ragFlowProcessService;
    private final BusResourceFolderRepo folderRepo;
    private final FileUploadRecordMapper fileUploadRecordMapper;
    private final BusResourceFileRepo busResourceFileRepo;
    private final SysOptLogRepo sysOptLogRepo;
    private final BusResourceFavoriteRepo busResourceFavoriteRepo;
    private final BusResourceFileMapping busResourceFileMapping;
    private final BusResourceFavoriteMapping busResourceFavoriteMapping;
    private final KmService kmService;
    private final BusResourceFolderMapping folderMapping;
    private final BusResourceMemberRepo memberRepo;
    private final BusUserInfoRepo userInfoRepo;
    @Value("${file.base.path}")
    private String fileBasePath;
    private static final String[] toMarkdownWhiteList = {"PDF", "PPT", "PPTX", "DOC", "PNG", "JPG",
            "pdf", "ppt", "pptx", "doc", "docx", "png", "jpg"};

    @Override
    public RestResponse resourceTreeListLeft() {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = StringUtils.isBlank(currentUser.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : currentUser.getUserId();
        List<BusResourceManageDTO> fixedList = busResourceManageRepo.listFixed();
        Boolean editUnitAuth = this.checkEditUnitAuth();
        List<BusResourceManageDTO> list = busResourceManageRepo.list(userId);
        list.addAll(fixedList);
        list.addAll(busResourceManageRepo.listUnit(ResourceTypeEnum.RESOURCE_FOLDER.getCode()));
        List<String> depNumList = Linq.select(depInfoRepo.listAllSup(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
        depNumList.add(currentUser.getDepNum());
        depNumList.addAll(Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum));
        list.addAll(busResourceManageRepo.listDep(ResourceTypeEnum.RESOURCE_FOLDER.getCode(), depNumList));
        for (BusResourceManageDTO dto : list) {
            boolean editAuth = ResourceCategoryEnum.USER.getName().equals(dto.getCategory())
                    || (ResourceCategoryEnum.UNIT.getName().equals(dto.getCategory()) && editUnitAuth)
                    || (ResourceCategoryEnum.DEP.getName().equals(dto.getCategory()) && currentUser.getDepNum().equals(dto.getDepNum()));
            dto.setEditAuth(editAuth);
        }
        List<BusResourceManageDTO> busResourceManageDTOS = TreeNodeServiceImpl.assembleTree(list);
        return RestResponse.success(busResourceManageDTOS);
    }

    @Override
    public RestResponse resourceSingleTree(String category) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = StringUtils.isBlank(currentUser.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : currentUser.getUserId();
        List<BusResourceManageDTO> fixedList = busResourceManageRepo.listFixed();
        BusResourceManageDTO first = Linq.first(fixedList, x -> category.equals(x.getCategory()));
        List<BusResourceManageDTO> list = new ArrayList<>();
        list.add(first);
        Boolean editUnitAuth = this.checkEditUnitAuth();

        if (ResourceCategoryEnum.USER.getName().equals(category)) {
            list.addAll(busResourceManageRepo.list(userId));
            first.setEditAuth(true);
        }
        if (ResourceCategoryEnum.DEP.getName().equals(category)) {
            List<String> depNumList = Linq.select(depInfoRepo.listAllSup(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            depNumList.add(currentUser.getDepNum());
            depNumList.addAll(Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum));
            list.addAll(busResourceManageRepo.listDep(ResourceTypeEnum.RESOURCE_FOLDER.getCode(), depNumList));
            first.setEditAuth(false);
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(category)) {
            list.addAll(busResourceManageRepo.listUnit(ResourceTypeEnum.RESOURCE_FOLDER.getCode()));
            first.setEditAuth(editUnitAuth);
        }
        for (BusResourceManageDTO dto : list) {
            boolean editAuth = ResourceCategoryEnum.USER.getName().equals(dto.getCategory())
                    || (ResourceCategoryEnum.UNIT.getName().equals(dto.getCategory()) && editUnitAuth)
                    || (ResourceCategoryEnum.DEP.getName().equals(dto.getCategory()) && currentUser.getDepNum().equals(dto.getDepNum()));
            dto.setEditAuth(editAuth);
        }
        TreeNodeServiceImpl.assembleTree(first, list);
        return RestResponse.success(Collections.singletonList(first));
    }

    @Override
    public RestResponse resourceListRight(QueryDTO query) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity entity = busResourceManageRepo.getById(query.getParentId());
        if (null == entity) {
            return RestResponse.fail(ResourceErrorCode.P_NULL);
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : ContextUtil.getUserId();

        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory())) {
            List<String> supDepNumList = Linq.select(depInfoRepo.listAllSup(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            List<String> authDepNumList = Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            authDepNumList.add(currentUser.getDepNum());
            if (!supDepNumList.contains(entity.getDepNum()) && !authDepNumList.contains(entity.getDepNum()) && !entity.getFixed()) {
                return RestResponse.fail(ResourceErrorCode.NO_VIEW_AUTH.getCode(), "无查看权限");
            }
            query.setAuthDepNumList(authDepNumList);
        }
        List<BusResourceManageDTO> busResourceManageDTOS = busResourceManageRepo.resourceListRight(userId, entity.getCategory(), query);
        Integer count = busResourceManageRepo.resourceListRightCount(userId, entity.getCategory(), query);
        return RestResponse.success(busResourceManageDTOS, count);
    }

    @Override
    public RestResponse updateJoinQuery(QueryDTO query) {
        SystemUser currentUser = ContextUtil.currentUser();
        busResourceManageRepo.updateJoinQuery(query.getIdList(), query.getJoinQuery());
        List<BusResourceManageDTO> list = busResourceManageRepo.listByIdList(query.getIdList(), !query.getJoinQuery());
        return RestResponse.SUCCESS;
    }

    private Boolean checkOperateAuth(SystemUser currentUser, BusResourceManageEntity entity) {
        if (null == entity) {
            return false;
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(entity.getCategory())) {
            return this.checkEditUnitAuth();
        }
        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory())) {
            List<String> authDepNumList = new ArrayList<>();
//            authDepNumList = Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            authDepNumList.add(currentUser.getDepNum());
            return authDepNumList.contains(entity.getDepNum());
        } else {
            return entity.getCreateUserId().equals(currentUser.getUserId())
                    || (ResourceCategoryEnum.USER.getName().equals(entity.getCategory()) && entity.getFixed());
        }
    }

    @Override
    public RestResponse checkUploadAuth(Integer id) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity entity = busResourceManageRepo.getById(id);
        return RestResponse.success(this.checkOperateAuth(currentUser, entity));
    }

    private Boolean checkEditUnitAuth() {
        SystemUser currentUser = ContextUtil.currentUser();
        return currentUser.getSpecialAuth().contains(UserSpecialAuthEnum.UNIT_FILE_MANAGE.getAuthCode());
    }

    /**
     * 生成唯一的基础文件名（不含扩展名）
     */
    public String generateUniqueName(String originalName, List<String> existingNames) {
        // 提取文件名中的数字部分（如果文件名已经包含类似 (1) 的结构）
        Pattern pattern = Pattern.compile("(.*)\\((\\d+)\\)$");
        Matcher matcher = pattern.matcher(originalName);
        int counter = 1;

        if (matcher.find()) {
            // 如果文件名已经包含类似 (1) 的结构，提取数字并递增
            originalName = matcher.group(1);
            counter = Integer.parseInt(matcher.group(2)) + 1;
        }

        // 生成新的文件名，直到找到一个不冲突的
        String newName;
        do {
            newName = String.format("%s(%d)", originalName, counter);
            counter++;
        } while (existingNames.contains(newName));

        return newName;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse saveResource(BusResourceManageDTO resourceManageDTO) {
        SystemUser currentUser = ContextUtil.currentUser();
        if (null == resourceManageDTO) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
        boolean reBool = false;

        String names = resourceManageDTO.getName();
        if (StringUtils.isBlank(names)) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "文件名不能为空");
        }
        if (resourceManageDTO.getResourceType().equals(ResourceTypeEnum.RESOURCE_FILE.getCode())) {
            if (StringUtils.isBlank(resourceManageDTO.getFileType())) {
                return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上传文件时请传入文件后缀");
            }
            if (StringUtils.isBlank(resourceManageDTO.getSize())) {
                return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上传文件时请传入文件大小,格式：33kb");
            }
            if (StringUtils.isBlank(resourceManageDTO.getFileId())) {
                return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "文件下载ID不能为空");
            }
        }
        // 查父ID文件类型
        BusResourceManageEntity parent = busResourceManageRepo.resourceTypeById(resourceManageDTO.getParentId());
        if (null == parent) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上级文件夹未找到");
        }
        resourceManageDTO.setParentGuid(parent.getGuid());
        resourceManageDTO.setCategory(parent.getCategory());
        //判断权限
        BusDepInfoDTO dep = depInfoRepo.getByDepNum(parent.getDepNum());
        if (ResourceCategoryEnum.DEP.getName().equals(parent.getCategory())) {
            if (null == dep && !"".equals(parent.getDepNum())) {
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "上级文件夹所属部门已失效");
            }
            if (!currentUser.getDepNum().equals(parent.getDepNum())) {
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
            }
            resourceManageDTO.setDepNum(currentUser.getDepNum());
            resourceManageDTO.setDepName(currentUser.getDepName());
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(parent.getCategory()) && !this.checkEditUnitAuth()) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }


        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : ContextUtil.getUserId();
        resourceManageDTO.setCreateUserId(userId); // 填充默认值
        List<SyncFileDTO> syncFileDTOS = new ArrayList<>(); // 用于给文件同步知识库id与文件ID
        List<RagProcessDTO> fileIdList = new ArrayList<>(); // 用于将文件同步到ragflow
        List<ConvertMarkdownDTO> mdList = new ArrayList<>(); // 用于将文件转换为md文件
        // 文件夹上传
        if (resourceManageDTO.getResourceType().equals(ResourceTypeEnum.RESOURCE_FOLDER.getCode())) {
            FileEmbeddingConfigDTO embeddingConfig;
            String fileEmbeddingConfigCode = StringUtils.isEmpty(resourceManageDTO.getEmbeddingConfigCode()) ? "" : resourceManageDTO.getEmbeddingConfigCode();
            embeddingConfig = this.getEmbeddingConfigByConfigCode(fileEmbeddingConfigCode, resourceManageDTO.getEmbeddingConfig());
            //处理重复名字
            List<BusResourceManageDTO> otherCode = busResourceManageRepo.listByParentIdAndResourceType(parent.getId(), ResourceTypeEnum.RESOURCE_FOLDER.getCode());
            if (CollUtil.isNotEmpty(otherCode)) {
                List<String> existNameList = Linq.select(otherCode, BusResourceManageDTO::getName);
                if (existNameList.contains(resourceManageDTO.getName())) {
                    resourceManageDTO.setName(this.generateUniqueName(resourceManageDTO.getName(), existNameList));
                }
            }
            resourceManageDTO.setSort(busResourceManageRepo.maxSort() + 1);
            Long resourceId = busResourceManageRepo.add(resourceManageDTO);
            reBool = resourceId != null;
            BusResourceEmbeddingDTO busResourceEmbeddingDTO = embeddingMapping.configDto2Dto(embeddingConfig);
            busResourceEmbeddingDTO.setResourceId(Math.toIntExact(resourceId));
            embeddingRepo.add(busResourceEmbeddingDTO);
        }
        // 文件上传
        else if (resourceManageDTO.getResourceType().equals(ResourceTypeEnum.RESOURCE_FILE.getCode())) {
            List<Map<String, Object>> fileList = new ArrayList<>();
            String fileIds = resourceManageDTO.getFileId();
            String sizes = resourceManageDTO.getSize();
            String fileTypes = resourceManageDTO.getFileType();
            String embeddingConfigCodes = resourceManageDTO.getEmbeddingConfigCode();
            String embeddingConfigNames = resourceManageDTO.getEmbeddingConfigName();
            // 新增文件时，可能会多值。
            String[] fields = fileIds.split(",");
            String[] sizeFields = sizes.split(",");
            String[] fileTypeFields = fileTypes.split(",");
            String[] nameFields = names.split(",");
            String[] Codes = embeddingConfigCodes.split(",");
            String[] Names = embeddingConfigNames.split(",");
            List<FileEmbeddingConfigDTO> embeddingConfigList= new ArrayList<>();
            for (String code : Codes) {
                embeddingConfigList.add(this.getEmbeddingConfigByConfigCode(code, resourceManageDTO.getEmbeddingConfig()));
            }

            for (int i = 0; i <= fields.length - 1; i++) {
                Map<String, Object> file = new HashMap<>();
                file.put(ServiceConstants.RESOURCE_FILE_NAME, nameFields[i]);
                file.put(ServiceConstants.RESOURCE_FILE_ID, fields[i]);
                file.put(ServiceConstants.RESOURCE_FILE_SIZE, sizeFields[i]);
                file.put(ServiceConstants.RESOURCE_FILE_TYPE, fileTypeFields[i]);
                fileList.add(file);
            }
            try {
                if (parent.getResourceType().equals(ResourceTypeEnum.RESOURCE_FILE.getCode())) {
                    return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "文件下不能创建节点");
                }
                List<Long> resourceIds = new ArrayList<>();
                //处理重复名字
                List<BusResourceManageDTO> otherCode = busResourceManageRepo.listByParentIdAndResourceType(parent.getId(), ResourceTypeEnum.RESOURCE_FILE.getCode());
                for (int i = 0; i < fileList.size(); i++) {
                    Map<String, Object> stringObjectMap = fileList.get(i);
                    resourceManageDTO.setSort(busResourceManageRepo.maxSort() + 1);
                    resourceManageDTO.setFileId(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_ID) + "");
                    resourceManageDTO.setFileType(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_TYPE) + "");
                    resourceManageDTO.setSize(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_SIZE) + "");
                    resourceManageDTO.setName(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_NAME) + "");
                    resourceManageDTO.setEmbeddingConfigCode(Codes[i]);
                    resourceManageDTO.setEmbeddingConfigName(Names[i]);
                    //处理重复名字
                    String fileType = resourceManageDTO.getFileType();
                    List<BusResourceManageDTO> subCodeList = Linq.find(otherCode, x -> x.getFileType().equals(fileType));
                    if (CollUtil.isNotEmpty(subCodeList)) {
                        List<String> existNameList = Linq.select(subCodeList, BusResourceManageDTO::getName);
                        if (existNameList.contains(resourceManageDTO.getName())) {
                            resourceManageDTO.setName(this.generateUniqueName(resourceManageDTO.getName(), existNameList));
                        }
                    }
                    otherCode.add(resourceManageDTO);
                    Long resourceId = busResourceManageRepo.add(resourceManageDTO);
                    resourceIds.add(resourceId);
                    BusResourceEmbeddingDTO busResourceEmbeddingDTO = embeddingMapping.configDto2Dto(embeddingConfigList.get(i));
                    busResourceEmbeddingDTO.setResourceId(Math.toIntExact(resourceId));
                    embeddingRepo.add(busResourceEmbeddingDTO);

                }
                // TODO 该段逻辑后续可能更改，当前设置为一个用户仅一个个人知识库
                String datasetsId = "";
//                if (ResourceCategoryEnum.UNIT.getName().equals(parent.getCategory())){
//                    datasetsId = datasetsConfigProperties.getUnitId();
//                }
//                if (ResourceCategoryEnum.DEP.getName().equals(parent.getCategory())){
//                    BusResourceDatasetDTO datasetDTO = datasetRepo.getByCode(parent.getDepNum());
//                    datasetsId = null != datasetDTO ? datasetDTO.getDatasetsId() : this.createDatasets(dep.getDepName(), parent.getDepNum());
//                    datasetRepo.add(ResourceCategoryEnum.DEP.getCode(), parent.getDepNum(), datasetsId);
//                }
//                if (ResourceCategoryEnum.USER.getName().equals(parent.getCategory())){
//                    //根据用户查是否存在个人知识库
//                    BusResourceDatasetDTO datasetDTO = datasetRepo.getByCode(currentUser.getUserId());
//                    // 不存在个人知识库，新建知识库  调用有云创建空知识库
//                    datasetsId = null != datasetDTO ? datasetDTO.getDatasetsId() : this.createDatasets(currentUser.getUserName(), currentUser.getLoginId());
//                    datasetRepo.add(ResourceCategoryEnum.USER.getCode(), currentUser.getUserId(), datasetsId);
//                }
                for (int i = 0; i < resourceIds.size(); i++) {
                    Long resourceId = resourceIds.get(i);
                    // 根据资源ID查fileId
                    BusResourceManageEntity manageEntity = busResourceManageRepo.getById(resourceId);
                    // 存用户关联文档表
                    RelUserResourceDTO relUserResourceDTO = new RelUserResourceDTO();
                    relUserResourceDTO.setResourceId(resourceId);
                    relUserResourceDTO.setUserId(userId);
                    relUserResourceDTO.setDatasetsId(datasetsId);
                    relUserResourceDTO.setFileId(manageEntity.getFileId());
                    relUserResourceRepo.add(relUserResourceDTO);

                    RagProcessDTO ragProcessDTO = new RagProcessDTO();
                    ragProcessDTO.setResourceId(resourceId);
                    ragProcessDTO.setFileId(manageEntity.getFileId());
                    ragProcessDTO.setEmbeddingConfigCode(Codes[i]);
                    fileIdList.add(ragProcessDTO);

                    if (Arrays.asList(toMarkdownWhiteList).contains(manageEntity.getFileType())) {
                        ConvertMarkdownDTO markdownDTO = new ConvertMarkdownDTO();
                        markdownDTO.setResourceId(resourceId);
                        markdownDTO.setFileId(manageEntity.getFileId());
                        mdList.add(markdownDTO);
                    }

                    SyncFileDTO syncFileDTO = new SyncFileDTO();
                    syncFileDTO.setFileName(manageEntity.getName() + "." + manageEntity.getFileType());
                    syncFileDTO.setFileId(manageEntity.getFileId());
                    syncFileDTO.setDatasetId(datasetsId);
                    syncFileDTO.setFileEmbeddingConfigCode(Codes[i]);
                    syncFileDTO.setFileEmbeddingConfigName(Names[i]);
                    syncFileDTO.setEmbeddingConfig(embeddingConfigList.get(i));
                    syncFileDTOS.add(syncFileDTO);
                }
                reBool = true;
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
            }
        }
        if (reBool) {
            //同步文件到dify知识库并更新documentId和batch
            // this.syncFileToDify(syncFileDTOS);
            resourceProcessService.execute(fileIdList);
            this.addPreview(mdList);
        }
        return reBool ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.ADD_FAIL);
    }

    private void addPreview(List<ConvertMarkdownDTO> list) {
//        ThreadPoolExecutor singleThreadExecutor = new ThreadPoolExecutor(1, 1,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(1000));
//        try {
//            ConvertMarkdownRunnable runnable = new ConvertMarkdownRunnable(list, fileUploadRecordMapper, busResourceManageRepo, fileApi, fileBasePath);
//            singleThreadExecutor.submit(runnable);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            singleThreadExecutor.shutdown();
//        }
    }

    private String createDatasets(String name, String id) {
        String datasetsName = ServiceConstants.CUS_DATASETS_DEFAULT_NAME + "-" + name + "(" + id + ")";
        String datasetsId = this.createDatasetsId(datasetsName);
        log.info("知识库创建成功!返回知识库ID为：{}", datasetsId);
        return datasetsId;
    }

    /**
     * 同步文件到dify知识库并更新documentId和batch
     *
     * @param syncFileDTOS
     */
    private void syncFileToDify(List<SyncFileDTO> syncFileDTOS) {
        try {
            for (SyncFileDTO syncFileDTO : syncFileDTOS) {
                RestResponse restResponse = fileApi.syncDocument(syncFileDTO);
                if (!restResponse.isSuccess()) {
                    log.error("调用文件同步接口失败,失败状态码【{}】,状态信息：【{}】", restResponse.getCode(), restResponse.getMsg());

                } else {
//                        RestResponse fileDTO = fileApi.getByFileId(syncFileDTO.getFileId());
                    LinkedHashMap data = (LinkedHashMap<String, Object>) restResponse.getData();
                    if (data != null) {
                        String fileId = (null == data.get("guid")) ? "" : data.get("guid") + "";
                        String documentId = (null == data.get("documentId")) ? "" : data.get("documentId") + "";
                        String batch = (null == data.get("batch")) ? "" : data.get("batch") + "";
                        String indexStatus = (null == data.get("indexStatus")) ? "" : data.get("indexStatus") + "";
//                            FileUploadRecordDTO fileInfo = (FileUploadRecordDTO) fileDTO.getData();
                        IndexingStatusEnum indexingStatusEnum = IndexingStatusEnum.fromIndexingStatus(indexStatus);
                        boolean f = relUserResourceRepo.updateInfoFromDify(fileId, documentId, batch, indexingStatusEnum);
                        log.info("更新document_id是否成功：{}", f);
                    }
                }
            }
        } catch (Exception e) {
            log.error("调用文件同步接口失败");
        }
    }

    private FileEmbeddingConfigDTO getEmbeddingConfigByConfigCode(String configCode, FileEmbeddingConfigDTO configDTO) {
        FileEmbeddingConfigDTO result;
        if (StringUtils.isEmpty(configCode) && null != configDTO) {
            result = configDTO;
        } else {
            List<FileEmbeddingConfigDTO> embeddingConfigList = embeddingConfigRepo.listAll();
            result = Linq.first(embeddingConfigList, x -> configCode.equals(x.getConfigCode()));
            result = null == result ? Linq.first(embeddingConfigList, x -> FileEmbeddingConfigEnum.GENERAL_CONFIG.getCode().equals(x.getConfigCode()))
                    : result;
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse updateResource(BusResourceManageDTO resourceManageDTO) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity entity = busResourceManageRepo.getById(resourceManageDTO.getId());
        RelUserResourceDTO relUserResourceDTO = relUserResourceRepo.getOneByFileId(entity.getFileId());
        if (null == entity) {
            return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR);
        }
        //判断权限
        if (entity.getFixed()) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.USER.getName().equals(entity.getCategory()) && !entity.getCreateUserId().equals(currentUser.getUserId())) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory()) && !entity.getDepNum().equals(currentUser.getDepNum())) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(entity.getCategory()) && !this.checkEditUnitAuth()) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (!entity.getParentId().equals(resourceManageDTO.getParentId())) {
            BusResourceManageEntity newParent = busResourceManageRepo.getById(resourceManageDTO.getParentId());
            //检查循环
            if (entity.getResourceType().equals(ResourceTypeEnum.RESOURCE_FOLDER.getCode())) {
                List<BusResourceManageDTO> childrenList = this.getChildrenList(busResourceManageRepo.listResourceFloder(), new ArrayList<>(), Math.toIntExact(entity.getId()));
                List<Integer> idList = Linq.select(childrenList, BusResourceManageDTO::getId);
                idList.add(Math.toIntExact(entity.getId()));
                if (idList.contains(resourceManageDTO.getParentId())) {
                    return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
                }
            }
            if (!newParent.getCategory().equals(entity.getCategory())) {
                return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，不可跨越知识库移动");
            }
            resourceManageDTO.setParentGuid(newParent.getGuid());
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : ContextUtil.getUserId();
        resourceManageDTO.setUpdateUserId(userId);
        if (!entity.getName().equals(resourceManageDTO.getName())) {
            //处理重复名字
            List<BusResourceManageDTO> otherCode = busResourceManageRepo.listByParentIdAndResourceType(Long.valueOf(resourceManageDTO.getParentId()), entity.getResourceType());
            if (CollUtil.isNotEmpty(otherCode)) {
                List<String> existNameList = Linq.select(otherCode, BusResourceManageDTO::getName);
                if (existNameList.contains(resourceManageDTO.getName())) {
                    resourceManageDTO.setName(this.generateUniqueName(resourceManageDTO.getName(), existNameList));
                }
            }
        }
//        if (!entity.getEmbeddingConfigCode().equals(resourceManageDTO.getEmbeddingConfigCode()) || entity.getEmbeddingConfigCode().isEmpty()) {
//            FileEmbeddingConfigDTO embeddingConfig;
//            String fileEmbeddingConfigCode = StringUtils.isEmpty(resourceManageDTO.getEmbeddingConfigCode()) ? "" : resourceManageDTO.getEmbeddingConfigCode();
//            embeddingConfig = this.getEmbeddingConfigByConfigCode(fileEmbeddingConfigCode, resourceManageDTO.getEmbeddingConfig());
//
//            BusResourceEmbeddingDTO busResourceEmbeddingDTO = embeddingMapping.configDto2Dto(embeddingConfig);
//            busResourceEmbeddingDTO.setResourceId(resourceManageDTO.getId());
//            embeddingRepo.delete(resourceManageDTO.getId());
//            embeddingRepo.add(busResourceEmbeddingDTO);
//            resourceManageDTO.setEmbeddingConfigCode(embeddingConfig.getConfigCode());
//            resourceManageDTO.setEmbeddingConfigName(embeddingConfig.getConfigName());
//
//            if (null != relUserResourceDTO) {
//                SyncFileDTO syncFileDTO = new SyncFileDTO();
//                syncFileDTO.setFileName(resourceManageDTO.getName() + "." + entity.getFileType());
//                syncFileDTO.setFileId(relUserResourceDTO.getFileId());
//                syncFileDTO.setDatasetId(relUserResourceDTO.getDatasetsId());
//                syncFileDTO.setFileEmbeddingConfigCode(fileEmbeddingConfigCode);
//                syncFileDTO.setFileEmbeddingConfigName(resourceManageDTO.getEmbeddingConfigName());
//                syncFileDTO.setEmbeddingConfig(embeddingConfig);
//                //同步文件到dify知识库并更新documentId和batch
//                this.syncFileToDify(List.of(syncFileDTO));
//            }
//        }
        boolean b = busResourceManageRepo.update(resourceManageDTO);
        return b ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.EDIT_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse getPreview(Integer id) throws IOException {
        BusResourceManageEntity entity = busResourceManageRepo.getById(id);
        if (null == entity) {
            return RestResponse.success(null);
        }
        if (StringUtils.isEmpty(entity.getPreviewFileId())) {
            return RestResponse.success(null);
        }
        FileUploadRecordDTO preview = fileUploadRecordMapper.getByFileId(entity.getPreviewFileId());
        String content = new String(Files.readAllBytes(Path.of(fileBasePath + File.separator + preview.getPath() + preview.getFileName())));
        return RestResponse.success(content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse deleteById(Integer id) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity entity = busResourceManageRepo.getById(id);
        if (null == entity) {
            return RestResponse.fail(ResourceErrorCode.DELETE_GUID_NOT_EXISTS);
        }
        //判断权限
        if (entity.getFixed()) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.USER.getName().equals(entity.getCategory()) && !entity.getCreateUserId().equals(currentUser.getUserId())) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory()) && !entity.getDepNum().equals(currentUser.getDepNum())) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(entity.getCategory()) && !this.checkEditUnitAuth()) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        boolean success = busResourceManageRepo.delete(id);
        if (ResourceTypeEnum.RESOURCE_FILE.getCode().equals(entity.getResourceType())) {
            RelUserResourceDTO document = relUserResourceRepo.getOneByResourceId(entity.getId());
            if (null != document) {
                // this.deleteDocument(document.getDatasetsId(), document.getDocumentId());
                resourceProcessService.delete(entity.getId(), document.getDocumentId(), document.getFileId(), entity.getEmbeddingConfigCode());
            }
            relUserResourceRepo.deleteByFileId(entity.getFileId());
        }
        return success ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.DELETE_FAIL);
    }

    @Override
    public RestResponse resourceSegmentList(Integer id) {
        if (id == null) {
            return RestResponse.fail(ResourceErrorCode.ID_NULL);
        }
        // 根据文件ID找fileId
        BusResourceManageEntity busResourceManageEntity = busResourceManageRepo.getById(Long.parseLong(id + ""));
        if (null == busResourceManageEntity) {
            return RestResponse.fail(ResourceErrorCode.DATA_NOT_EXISTS.getCode(), ResourceErrorCode.DATA_NOT_EXISTS.getMsg());
        }
        String fileId = busResourceManageEntity.getFileId();
        List<RelUserResourceEntity> relUserResourceDTOS = relUserResourceRepo.oneByField(fileId);
        if (null == relUserResourceDTOS || relUserResourceDTOS.isEmpty()) {
            return RestResponse.fail(ResourceErrorCode.DATA_NOT_EXISTS.getCode(), ResourceErrorCode.DATA_NOT_EXISTS.getMsg());
        }
        String indexingStatus = relUserResourceDTOS.get(0).getIndexingStatus();
        // 文档处理状态如果为空或者不为完成状态，则直接返回空列表
        if (StringUtils.isBlank(indexingStatus) || !indexingStatus.equals(IndexingStatusEnum.COMPLETED.getIndexingStatus())) {
            return RestResponse.success(null, 0);
        }
        String datasetsId = relUserResourceDTOS.get(0).getDatasetsId();
        String documentId = relUserResourceDTOS.get(0).getDocumentId();
        // 状态成功，查文档分段列表
        Map map = this.fileSegmentsByDatasetIdAndDocumentId(datasetsId, documentId);
        List<Map> res = null;
        if (!map.isEmpty()) {
            res = map.get(ServiceConstants.RES_DATA) != null ? (List<Map>) (map.get(ServiceConstants.RES_DATA)) : new ArrayList<>();
        }
        return RestResponse.success(res);
    }

    @Override
    public RestResponse moveNode(Integer operateId, Integer targetId, String operateType) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity operateNode = busResourceManageRepo.getById(operateId);
        BusResourceManageEntity targetNode = busResourceManageRepo.getById(targetId);
        if (ResourceOperateEnum.INNER.getCode().equals(operateType) || !operateNode.getParentId().equals(targetNode.getParentId())) {
            Integer parentId = ResourceOperateEnum.INNER.getCode().equals(operateType) ? targetId : targetNode.getParentId();
            BusResourceManageEntity newParent = busResourceManageRepo.getById(parentId);
            //检查循环
            if (operateNode.getResourceType().equals(ResourceTypeEnum.RESOURCE_FOLDER.getCode())) {
                List<BusResourceManageDTO> childrenList = this.getChildrenList(busResourceManageRepo.listResourceFloder(), new ArrayList<>(), Math.toIntExact(operateNode.getId()));
                List<Integer> idList = Linq.select(childrenList, BusResourceManageDTO::getId);
                idList.add(Math.toIntExact(operateNode.getId()));
                if (idList.contains(parentId)) {
                    return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
                }
            }
            if (!newParent.getCategory().equals(operateNode.getCategory())) {
                return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，不可跨越知识库移动");
            }
            operateNode.setParentGuid(newParent.getGuid());
        }
        boolean b = busResourceManageRepo.update(busResourceManageMapping.entity2Dto(operateNode));
        return b ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.EDIT_FAIL);
    }

    @Override
    public RestResponse updateParentId(Integer id, Integer originParentId, Integer newParentId) {
        busResourceManageRepo.updateParentId(id, newParentId);
        return RestResponse.SUCCESS;
    }

    /**
     * 获取所有父类别详情
     *
     * @param parentList   父类别列表
     * @param parentIdList 要获取的父类别的类别编码列表
     * @param parentId     要获取的父类别的类别编码
     * @return 结果
     */
    private List<BusResourceManageDTO> getParentList(List<BusResourceManageDTO> parentList, List<Integer> parentIdList, Integer parentId) {
        if (null == parentId) {
            return parentList;
        }
        BusResourceManageDTO parent = busResourceManageMapping.entity2Dto(busResourceManageRepo.getById(Long.valueOf(parentId)));
        if (parentIdList.contains(parent.getId())) {
            return parentList;
        }
        parentIdList.add(parent.getId());
        parentList.add(parent);
        return getParentList(parentList, parentIdList, parent.getParentId());
    }

    private List<BusResourceManageDTO> getChildrenList(List<BusResourceManageDTO> list, List<BusResourceManageDTO> childList, Integer parentId) {
        List<BusResourceManageDTO> allChild = Linq.find(list, x -> parentId.equals(x.getParentId()));
        if (CollUtil.isEmpty(allChild)) {
            return childList;
        }
        childList.addAll(allChild);
        for (BusResourceManageDTO child : childList) {
            getChildrenList(list, childList, child.getId());
        }
        return childList;
    }

    @Override
    public RestResponse updateSort(List<BusResourceManageDTO> list) {
        for (BusResourceManageDTO resourceDTO : list) {
            busResourceManageRepo.updateSort(resourceDTO.getId(), resourceDTO.getSort());
        }
        return RestResponse.SUCCESS;
    }

    /**
     * 创建空的个人知识库
     *
     * @param datasetsName 知识库名称
     * @return 知识库ID
     */
    private String createDatasetsId(String datasetsName) {
        String datasetsId = "";
        String url = aiConfigProperties.getPersonDatasetsCreateApi();
        Map<String, Object> map = new HashMap<>();
        /**
         * 知识库名称（必填）
         */
        map.put("name", datasetsName);
        /**
         * only_me 仅自己
         * all_team_members 所有团队成员
         * partial_members 部分团队成员
         */
        map.put("permission", "only_me");
        /**
         * 知识库描述（选填）
         */
        map.put("description", "");
        /**
         * 索引模式（选填，建议填写）
         * high_quality 高质量
         * economy 经济
         */
        map.put("indexing_technique", "high_quality"); // 索引模式（选填，建议填写）
        /**
         * Provider（选填，默认 vendor）
         * vendor 上传文件
         * external 外部知识库
         */
        map.put("provider", "vendor");
        /**
         * 外部知识库 API_ID（选填）
         */
        map.put("external_knowledge_api_id", "");
        /**
         * 外部知识库 ID（选填）
         */
        map.put("external_knowledge_id", "");

        try {
            String string = HttpUtils.doPost(url, map, apikeyConfigProperties.getCustomvector(), "");
            log.info("创建知识库响应结果：" + string);
            if (StringUtils.isNotBlank(string)) {
                Gson gson = new Gson();
                Map resultMap = gson.fromJson(string, Map.class);
                if (resultMap != null && !resultMap.isEmpty()) {
                    datasetsId = resultMap.get(ServiceConstants.RES_ID) + ""; // 获取新建的知识库ID
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return datasetsId;
    }

    @Override
    public RestResponse updateInfoFromDify(String fileId, String documentId, String batch) {
        relUserResourceRepo.updateInfoFromDify(fileId, documentId, batch, null);
        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse updateIndexStatus(String documentId, String indexingStatus, String indexingStatusName) {
        relUserResourceRepo.updateIndexStatus(documentId, indexingStatus, indexingStatusName);
        return RestResponse.SUCCESS;
    }

    /**
     * 根据知识库ID和文档ID查文档分段信息
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @return 文档分段信息
     */
    private Map fileSegmentsByDatasetIdAndDocumentId(String datasetId, String documentId) {
        String url = aiConfigProperties.getDatasetsDocumentSegmentApi();
        url = String.format(url, datasetId, documentId);
        Map map = new HashMap<>();
        String response = HttpUtils.doGet(url, apikeyConfigProperties.getCustomvector());
        if (StringUtils.isNotBlank(response)) {
            Gson gson = new Gson();
            map = gson.fromJson(response, Map.class);
        }
        return map;
    }

    /**
     * 根据知识库ID和文档ID删除文档
     *
     * @param datasetId  知识库ID
     * @param documentId 文档ID
     * @return
     */
    private Map deleteDocument(String datasetId, String documentId) {
        String url = aiConfigProperties.getDatasetsDeleteDocumentApi();
        url = String.format(url, datasetId, documentId);
        Map map = new HashMap<>();
        String response = HttpUtils.doDelete(url, apikeyConfigProperties.getCustomvector());
        if (StringUtils.isNotBlank(response)) {
            Gson gson = new Gson();
            map = gson.fromJson(response, Map.class);
        }
        return map;
    }

    /**
     * 根据知识库ID查询知识库文档列表
     *
     * @param datasetId 知识库ID
     * @return
     */
    private Map syncDifyDocumentByDatasetId(String datasetId, Integer page) {
        String url = aiConfigProperties.getDatasetsDocumentListApi() + "?page=" + page + "&limit=100";
        url = String.format(url, datasetId);
        Map map = new HashMap<>();
        String response = HttpUtils.doGet(url, apikeyConfigProperties.getCustomvector());
        if (StringUtils.isNotBlank(response)) {
            Gson gson = new Gson();
            map = gson.fromJson(response, Map.class);
        }
        return map;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse syncDifyDocument(String datasetId, Integer parentId) {
        BusResourceManageEntity parent = busResourceManageRepo.getById(parentId);
        SystemUser currentUser = ContextUtil.currentUser();
        List<BusResourceManageDTO> otherCode = busResourceManageRepo.listByParentIdAndResourceType(parent.getId(), ResourceTypeEnum.RESOURCE_FILE.getCode());
        List<String> existNameList = CollUtil.isEmpty(otherCode) ? new ArrayList<>() : Linq.select(otherCode, BusResourceManageDTO::getName);

        for (int i = 1; i < 9; i++) {
            Map map = this.syncDifyDocumentByDatasetId(datasetId, i);
            List<Map> dataList = (List<Map>) map.get("data");
            for (Map subData : dataList) {
                Map file = (Map) subData.get("data_source_detail_dict");
                Map fileInfo = (Map) file.get("upload_file");

                BusResourceManageDTO dto = new BusResourceManageDTO();
                dto.setParentId(Math.toIntExact(parent.getId()));
                dto.setParentGuid(parent.getGuid());
                dto.setResourceType(2);
                dto.setCategory(parent.getCategory());
                dto.setDepNum(parent.getDepNum());
                dto.setDepName(parent.getDepName());
                dto.setName((String) fileInfo.get("name"));
                dto.setFileId((String) fileInfo.get("id"));
                dto.setSize(String.valueOf((Double) fileInfo.get("size")));
                dto.setFileType((String) fileInfo.get("extension"));

                if (existNameList.contains(dto.getName())) {
                    continue;
                }
                Long resourceId = busResourceManageRepo.add(dto);

                RelUserResourceEntity relEntity = new RelUserResourceEntity();
                relEntity.setResourceId(Math.toIntExact(resourceId));
                relEntity.setUserId(currentUser.getUserId());
                relEntity.setFileId(dto.getFileId());
                relEntity.setDatasetsId(datasetId);
                relEntity.setDocumentId((String) subData.get("id"));
                relEntity.setBatch((String) fileInfo.get("created_by"));
                String indexingStatus = (String) subData.get("indexing_status");
                IndexingStatusEnum indexingStatusEnum = IndexingStatusEnum.fromIndexingStatus(indexingStatus);
                relEntity.setIndexingStatus(indexingStatus);
                relEntity.setIndexingStatusName(null == indexingStatusEnum ? "完成" : indexingStatusEnum.getIndexingStatusName());
                relUserResourceRepo.save(relEntity);
            }
        }

        return RestResponse.SUCCESS;
    }

    @Override
    public RestResponse listEmbedConfig() {
        return RestResponse.success(embeddingConfigRepo.listAll());
    }

    @Override
    public RestResponse getEmbedConfig(String configCode) {
        return RestResponse.success(embeddingConfigRepo.getByCode(configCode));
    }

    @Override
    public RestResponse getResourceEmbedInfo(Integer resourceId) {
        return RestResponse.success(embeddingRepo.getByResourceId(resourceId));
    }

    /**
     * 查询文档切片内容
     *
     * @param id
     * @return
     */
    @Override
    public RestResponse resourceSegmentNewList(Integer id) {
        if (null == id) {
            return RestResponse.fail(ResourceErrorCode.ID_NULL);
        }
        // 根据文件ID找fileId
        BusResourceManageEntity busResourceManageEntity = busResourceManageRepo.getById(Long.parseLong(id + ""));
        if (null == busResourceManageEntity) {
            return RestResponse.fail(ResourceErrorCode.DATA_NOT_EXISTS.getCode(), ResourceErrorCode.DATA_NOT_EXISTS.getMsg());
        }
        String fileId = busResourceManageEntity.getFileId();
        List<RelUserResourceEntity> relUserResourceDTOS = relUserResourceRepo.oneByField(fileId);
        if (null == relUserResourceDTOS || relUserResourceDTOS.isEmpty()) {
            return RestResponse.fail(ResourceErrorCode.DATA_NOT_EXISTS.getCode(), ResourceErrorCode.DATA_NOT_EXISTS.getMsg());
        }
        String indexingStatus = relUserResourceDTOS.get(0).getIndexingStatus();
        // 文档处理状态如果为空或者不为完成状态，则直接返回空列表
        if (StringUtils.isBlank(indexingStatus) || !indexingStatus.equals(IndexingStatusEnum.COMPLETED.getIndexingStatus())) {
            return RestResponse.success(null, 0);
        }
        String datasetsId = fileEmbeddingConfigMapper.getDataSetId(busResourceManageEntity.getEmbeddingConfigCode());
        String documentId = relUserResourceDTOS.get(0).getDocumentId();
        // 状态成功，查文档分段列表
        Map map = ragFlowProcessService.chunks(datasetsId, documentId, 1, 50);
        List<Map> resList = null;
        if (null != map && map.get("chunks") != null) {
            resList = (List<Map>) map.get("chunks");
            for (Map dataMap : resList) {
                dataMap.put("word_count", dataMap.get("content").toString().length());
            }
        }
        return RestResponse.success(resList);
    }

    @Override
    public RestResponse getLatestFiles(String fileName, String sortField, String sortType) {
        // 当前登录用户
        SystemUser currentUser = ContextUtil.currentUser();
        // 可以在日志表中，查询当前用户查看切片、修改、移动过的文件id
        List<SysOptLogEntity> logList = sysOptLogRepo.lambdaQuery()
                // 当前用户更新的
                .eq(SysOptLogEntity::getUpdateUserId, currentUser.getUserId())
                // 文件类型为文件
                .eq(SysOptLogEntity::getFileType, ResourceTypeEnum.RESOURCE_FILE.getCode())
                // 操作类型为预览、修改、移动
                .in(SysOptLogEntity::getOperateType, OperateTypeEnum.PREVIEW.getName()
                        , OperateTypeEnum.EDIT.getName(), OperateTypeEnum.MOVE.getName())
                .list();
        List<Long> fileIdList = logList.stream().map(SysOptLogEntity::getResourceId).distinct().toList();

        Page<BusResourceFileEntity> page = new Page<>();
        QueryWrapper<BusResourceFileEntity> queryWrapper = new QueryWrapper<>();
        if (!fileIdList.isEmpty()) {
            queryWrapper.in("id", fileIdList)
                    .like(StringUtils.isNotBlank(fileName), "name", fileName);
            if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
                sortField = handleSortField(sortField);
                if (ResourceOrderEnum.ASC.getCode().equals(sortType)) {
                    queryWrapper.orderByAsc(sortField);
                } else if (ResourceOrderEnum.DESC.getCode().equals(sortType)) {
                    queryWrapper.orderByDesc(sortField);
                }
            }
            page = busResourceFileRepo.page(new Page<>(1, 100), queryWrapper);
        }

        // 查询每个文件的文件路径
        List<BusResourceFileEntity> records = page.getRecords();
        // 最近文件id列表
        List<Long> idList = records.stream().map(BusResourceFileEntity::getId).toList();
        // entity转vo
        List<BusResourceFileVO> voList = records.stream().map(busResourceFileMapping::entity2Vo).toList();
        // 封装map方便后面查询拼接文件路径
        List<BusResourceFolderDTO> folderDTOS = folderRepo.listAll(true);
        Map<Integer, List<BusResourceFolderDTO>> folderListMap = folderDTOS.stream().collect(Collectors.groupingBy(BusResourceFolderDTO::getId));
        // 查询解析状态
        List<RelUserResourceEntity> relResourceList = relUserResourceRepo.getListByResourceIdList(idList, currentUser.getUserId());
        Map<Integer, List<RelUserResourceEntity>> relMap = relResourceList.stream().collect(Collectors.groupingBy(RelUserResourceEntity::getResourceId));

        // 权限
        Boolean systemAdminAuth = userInfoRepo.checkAuth(currentUser.getUserId(), UserSpecialAuthEnum.SYSTEM_MANAGE.getAuthCode());

        List<Integer> parentIdList = voList.stream().map(BusResourceFileVO::getFolderId).distinct().toList();
        List<BusResourceMemberDTO> authList = kmService.getFolderAuthList(parentIdList);
        Map<Integer, BusResourceMemberDTO> authMap = authList.stream().collect(Collectors.toMap(BusResourceMemberDTO::getFolderId,auth -> auth,(existing, replacement) -> existing));

        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        List<Integer> adminFolderIds = Linq.select(memberRepo.listAdminByUser(currentUser.getUserId()), BusResourceMemberDTO::getFolderId);
        List<BusResourceManageListDTO> allAdminList = TreeNodeServiceImpl.getChildrenList(allList, adminFolderIds);
        List<Integer> allAdminIds = Linq.select(allAdminList, BusResourceManageListDTO::getId);

        // 处理结果
        for (BusResourceFileVO vo : voList) {
            // 资源类型：1-文件夹；2-文件
            vo.setResourceType(2);
            // 解析状态
            List<RelUserResourceEntity> relUserResourceEntities = relMap.get(vo.getId());
            if (relUserResourceEntities != null && !relUserResourceEntities.isEmpty()) {
                vo.setIndexingStatusName(relUserResourceEntities.get(0).getIndexingStatusName());
            }
            // 文件路径
            StringBuilder filePath = new StringBuilder();
            Integer folderId = vo.getFolderId();
            while (folderId != 0) {
                List<BusResourceFolderDTO> busResourceFolderDTOS = folderListMap.get(folderId);
                if (busResourceFolderDTOS != null && !busResourceFolderDTOS.isEmpty()) {
                    BusResourceFolderDTO parent = busResourceFolderDTOS.get(0);
                    filePath.insert(0, parent.getName() + "/");
                    folderId = parent.getParentId();
                }
            }
            filePath.insert(0, "/").deleteCharAt(filePath.length() - 1);
            vo.setFileLocation(filePath.toString());

            // 权限
            if (ResourceTypeEnum.RESOURCE_FILE.getCode().equals(vo.getResourceType())){
                BusResourceMemberDTO auth = authMap.get(vo.getFolderId());
                vo.setEditAuth(auth.getEditAuth());
                vo.setDeleteAuth(auth.getDeleteAuth());
                vo.setViewAuth(auth.getViewAuth());
                vo.setDownloadAuth(auth.getDownloadAuth());
                vo.setShareAuth(auth.getShareAuth());
            } else {
                vo.setViewLogAuth(ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(vo.getResourceType()) && vo.getCreateUserId().equals(currentUser.getUserId()));
                vo.setEditAuth(ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(vo.getResourceType()) && (systemAdminAuth || allAdminIds.contains(vo.getId())));
            }
            if(StringUtils.isEmpty(vo.getIndexingStatusName()) ){
                vo.setIndexingStatusName("--");
            }
        }

        Page<BusResourceFileVO> resultPage = new Page<>();
        resultPage.setRecords(voList);
        resultPage.setTotal(page.getTotal());

        return RestResponse.success(resultPage, resultPage.getTotal());
    }

    /**
     * 排序字段处理
     */
    private String handleSortField(String sortField) {
        switch (sortField) {
            case "name":
                return "name";
            case "updateTime":
                return "update_time";
            case "createTime":
                return "create_time";
            case "size":
                return "size";
            case "fileType":
                return "file_type";
            default:
                return "update_time";
        }
    }

    @Override
    public RestResponse getFavoriteResourceList(String fileName, String sortField, String sortType, Integer start, Integer size) {
        // 当前登录用户
        SystemUser currentUser = ContextUtil.currentUser();
        QueryWrapper<BusResourceFavoriteEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(fileName), "name", fileName)
                .eq("create_user_id", currentUser.getUserId());
        if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortType)) {
            sortField = handleSortField(sortField);
            if (ResourceOrderEnum.ASC.getCode().equals(sortType)) {
                queryWrapper.orderByAsc(sortField);
            } else if (ResourceOrderEnum.DESC.getCode().equals(sortType)){
                queryWrapper.orderByDesc(sortField);
            }
        }
        Page<BusResourceFavoriteEntity> page = busResourceFavoriteRepo.page(new Page<>(start, size), queryWrapper);
        // 查询每个文件的文件路径
        List<BusResourceFavoriteEntity> records = page.getRecords();
        // 资源id列表
        List<Long> idList = records.stream().map(BusResourceFavoriteEntity::getId).toList();
        // entity转vo
        List<BusResourceFavoriteVO> voList = records.stream().map(busResourceFavoriteMapping::entity2Vo).toList();
        // 封装map方便后面查询拼接文件路径
        List<BusResourceFolderDTO> folderDTOS = folderRepo.listAll(true);
        Map<Integer, List<BusResourceFolderDTO>> folderListMap = folderDTOS.stream().collect(Collectors.groupingBy(BusResourceFolderDTO::getId));
        // 查询解析状态
        List<RelUserResourceEntity> relResourceList = relUserResourceRepo.getListByResourceIdList(idList, currentUser.getUserId());
        Map<Integer, List<RelUserResourceEntity>> relMap = relResourceList.stream().collect(Collectors.groupingBy(RelUserResourceEntity::getResourceId));

        // 权限
        Boolean systemAdminAuth = userInfoRepo.checkAuth(currentUser.getUserId(), UserSpecialAuthEnum.SYSTEM_MANAGE.getAuthCode());

        List<Integer> parentIdList = voList.stream().map(BusResourceFavoriteVO::getParentId).distinct().toList();
        List<BusResourceMemberDTO> authList = kmService.getFolderAuthList(parentIdList);
        Map<Integer, BusResourceMemberDTO> authMap = authList.stream().collect(Collectors.toMap(BusResourceMemberDTO::getFolderId,auth -> auth,(existing, replacement) -> existing));

        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        List<Integer> adminFolderIds = Linq.select(memberRepo.listAdminByUser(currentUser.getUserId()), BusResourceMemberDTO::getFolderId);
        List<BusResourceManageListDTO> allAdminList = TreeNodeServiceImpl.getChildrenList(allList, adminFolderIds);
        List<Integer> allAdminIds = Linq.select(allAdminList, BusResourceManageListDTO::getId);

        // 处理结果
        for (BusResourceFavoriteVO vo : voList) {
            // 资源类型：1-文件夹；2-文件
//            vo.setResourceType(2);
            // 解析状态
            List<RelUserResourceEntity> relUserResourceEntities = relMap.get(vo.getId());
            if (relUserResourceEntities != null && !relUserResourceEntities.isEmpty()) {
                vo.setIndexingStatusName(relUserResourceEntities.get(0).getIndexingStatusName());
            }
            // 文件路径
            StringBuilder filePath = new StringBuilder();
            Integer folderId = vo.getParentId();
            while (folderId != 0) {
                List<BusResourceFolderDTO> busResourceFolderDTOS = folderListMap.get(folderId);
                if (busResourceFolderDTOS != null && !busResourceFolderDTOS.isEmpty()) {
                    BusResourceFolderDTO parent = busResourceFolderDTOS.get(0);
                    filePath.insert(0, parent.getName() + "/");
                    folderId = parent.getParentId();
                }
            }
            filePath.insert(0, "/").deleteCharAt(filePath.length() - 1);
            vo.setFileLocation(filePath.toString());

            // 权限
            if (ResourceTypeEnum.RESOURCE_FILE.getCode().equals(vo.getResourceType())){
                BusResourceMemberDTO auth = authMap.get(vo.getParentId());
                vo.setEditAuth(auth.getEditAuth());
                vo.setDeleteAuth(auth.getDeleteAuth());
                vo.setViewAuth(auth.getViewAuth());
                vo.setDownloadAuth(auth.getDownloadAuth());
                vo.setShareAuth(auth.getShareAuth());
            } else {
                vo.setViewLogAuth(ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(vo.getResourceType()) && vo.getCreateUserId().equals(currentUser.getUserId()));
                vo.setEditAuth(ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(vo.getResourceType()) && (systemAdminAuth || allAdminIds.contains(vo.getId())));
            }
            if(StringUtils.isEmpty(vo.getIndexingStatusName()) ){
                vo.setIndexingStatusName("--");
            }
        }

        Page<BusResourceFavoriteVO> resultPage = new Page<>();
        resultPage.setRecords(voList);
        resultPage.setTotal(page.getTotal());

        return RestResponse.success(resultPage, resultPage.getTotal());
    }

    @Override
    public RestResponse saveFavorite(BusResourceFavoriteDTO dto) {
        Long favoriteId = busResourceFavoriteRepo.saveFavorite(dto);
        return favoriteId != null ? RestResponse.SUCCESS : RestResponse.error("添加收藏失败");
    }

    @Override
    public RestResponse deleteFavorite(Integer resourceId, Integer parentId, Integer resourceType) {
        List<BusResourceFavoriteEntity> list = busResourceFavoriteRepo.lambdaQuery().eq(BusResourceFavoriteEntity::getResourceId, resourceId)
                .eq(BusResourceFavoriteEntity::getParentId, parentId)
                .eq(BusResourceFavoriteEntity::getResourceType, resourceType)
                .list();
        if (CollUtil.isEmpty(list)) {
            return RestResponse.error("数据不存在");
        }
        boolean b = busResourceFavoriteRepo.lambdaUpdate()
                .set(BusResourceFavoriteEntity::getDeleted, true)
                .eq(BusResourceFavoriteEntity::getResourceId, resourceId)
                .eq(BusResourceFavoriteEntity::getParentId, parentId)
                .eq(BusResourceFavoriteEntity::getResourceType, resourceType)
                .update();
        return b ? RestResponse.SUCCESS : RestResponse.error("删除收藏失败");
    }
}