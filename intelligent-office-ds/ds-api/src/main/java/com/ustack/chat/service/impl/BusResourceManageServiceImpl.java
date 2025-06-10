package com.ustack.chat.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.Gson;
import com.ustack.chat.entity.BusResourceManageEntity;
import com.ustack.chat.entity.RelUserResourceEntity;
import com.ustack.chat.enums.ChatApiKeyEnum;
import com.ustack.chat.mappings.BusResourceEmbeddingMapping;
import com.ustack.chat.mappings.BusResourceManageMapping;
import com.ustack.chat.properties.AiConfigProperties;
import com.ustack.chat.properties.ApikeyConfigProperties;
import com.ustack.chat.properties.DatasetsConfigProperties;
import com.ustack.chat.repo.*;
import com.ustack.chat.service.BusResourceManageService;
import com.ustack.chat.util.HttpUtils;
import com.ustack.feign.client.FileApi;
import com.ustack.file.dto.SyncFileDTO;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.*;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Service实现
 * @createDate 2025-02-18 17:57:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusResourceManageServiceImpl implements BusResourceManageService {
    @Autowired
    private AiConfigProperties aiConfigProperties;

    private final ApikeyConfigProperties apikeyConfigProperties;

    private final FileApi fileApi;

    private final BusResourceManageRepo busResourceManageRepo;
    private final RelUserResourceRepo relUserResourceRepo;
    private final BusDepInfoRepo depInfoRepo;
    private final FileEmbeddingConfigRepo embeddingConfigRepo;
    private final BusResourceEmbeddingRepo embeddingRepo;
    private final BusResourceDatasetRepo datasetRepo;
    private final DatasetsConfigProperties datasetsConfigProperties;
    private final BusResourceManageMapping busResourceManageMapping;
    private final BusResourceEmbeddingMapping embeddingMapping;

    @Override
    public RestResponse resourceTreeListLeft() {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = StringUtils.isBlank(currentUser.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : currentUser.getUserId();
        List<BusResourceManageDTO> fixedList = busResourceManageRepo.listFixed();
        List<BusResourceManageDTO> list = busResourceManageRepo.list(userId);
        list.addAll(fixedList);
        list.addAll(busResourceManageRepo.listUnit(ResourceTypeEnum.RESOURCE_FLODER.getCode()));
        List<String> depNumList = Linq.select(depInfoRepo.listAllSup(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
        depNumList.add(currentUser.getDepNum());
        depNumList.addAll(Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum));
        list.addAll(busResourceManageRepo.listDep(ResourceTypeEnum.RESOURCE_FLODER.getCode(), depNumList));
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

        if (ResourceCategoryEnum.USER.getName().equals(category)){
            list.addAll(busResourceManageRepo.list(userId));
        }
        if (ResourceCategoryEnum.DEP.getName().equals(category)){
            List<String> depNumList = Linq.select(depInfoRepo.listAllSup(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            depNumList.add(currentUser.getDepNum());
            depNumList.addAll(Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum));
            list.addAll(busResourceManageRepo.listDep(ResourceTypeEnum.RESOURCE_FLODER.getCode(), depNumList));
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(category)){
            list.addAll(busResourceManageRepo.listUnit(ResourceTypeEnum.RESOURCE_FLODER.getCode()));
        }

        TreeNodeServiceImpl.assembleTree(first, list);
        return RestResponse.success(Collections.singletonList(first));
    }

    @Override
    public RestResponse resourceListRight(String name, Integer parentId) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity entity = busResourceManageRepo.getById(parentId);
        if (null == entity) {
            return RestResponse.fail(ResourceErrorCode.P_NULL);
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : ContextUtil.getUserId();
        List<BusResourceManageDTO> busResourceManageDTOS = busResourceManageRepo.resourceListRight(userId, name, parentId, entity.getCategory());

        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory())){
            List<String> supDepNumList = Linq.select(depInfoRepo.listAllSup(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            List<String> authDepNumList = Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            authDepNumList.add(currentUser.getDepNum());
            if (!supDepNumList.contains(entity.getDepNum()) && !authDepNumList.contains(entity.getDepNum()) && !entity.getFixed()){
                return RestResponse.fail(ResourceErrorCode.NO_VIEW_AUTH.getCode(), "无查看权限");
            }
            busResourceManageDTOS = Linq.find(busResourceManageDTOS, x -> authDepNumList.contains(x.getDepNum()));
        }
        return RestResponse.success(busResourceManageDTOS, busResourceManageDTOS.size());
    }

    @Override
    public RestResponse checkUploadAuth(Integer id) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceManageEntity entity = busResourceManageRepo.getById(id);
        if (null == entity) {
            return RestResponse.success(false);
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(entity.getCategory())){
            return RestResponse.success(this.checkEditUnitAuth());
        }
        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory())){
            List<String> authDepNumList = new ArrayList<>();
//            authDepNumList = Linq.select(depInfoRepo.listAllChild(currentUser.getDepNum()), BusDepInfoDTO::getDepNum);
            authDepNumList.add(currentUser.getDepNum());
            return RestResponse.success(authDepNumList.contains(entity.getDepNum()));
        } else {
            return RestResponse.success(entity.getCreateUserId().equals(currentUser.getUserId())
                    || (ResourceCategoryEnum.USER.getName().equals(entity.getCategory()) && entity.getFixed()));
        }
    }

    private Boolean checkEditUnitAuth(){
        SystemUser currentUser = ContextUtil.currentUser();
        return currentUser.getSpecialAuth().contains(UserSpecialAuthEnum.UNIT_FILE_MANAGE.getAuthCode());
    }

    private String checkResourceName(Integer parentId, BusResourceManageDTO resourceManageDTO){
        return "";
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
        if (ResourceCategoryEnum.DEP.getName().equals(parent.getCategory())){
            if (null == dep && !"".equals(parent.getDepNum())) {
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "上级文件夹所属部门已失效");
            }
            if (!currentUser.getDepNum().equals(parent.getDepNum())){
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
            }
            resourceManageDTO.setDepNum(currentUser.getDepNum());
            resourceManageDTO.setDepName(currentUser.getDepName());
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(parent.getCategory()) && !this.checkEditUnitAuth()){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }

        FileEmbeddingConfigDTO embeddingConfig;
        String fileEmbeddingConfigCode = StringUtils.isEmpty(resourceManageDTO.getEmbeddingConfigCode()) ? "" : resourceManageDTO.getEmbeddingConfigCode();
        embeddingConfig = this.getEmbeddingConfigByConfigCode(fileEmbeddingConfigCode, resourceManageDTO.getEmbeddingConfig());

        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : ContextUtil.getUserId();
        resourceManageDTO.setCreateUserId(userId); // 填充默认值
        List<SyncFileDTO> syncFileDTOS = new ArrayList<>(); // 用于给文件同步知识库id与文件ID
        // 文件夹上传
        if (resourceManageDTO.getResourceType().equals(ResourceTypeEnum.RESOURCE_FLODER.getCode())) {
            //处理重复名字
            List<BusResourceManageDTO> otherCode = busResourceManageRepo.listByParentIdAndResourceType(parent.getId(), ResourceTypeEnum.RESOURCE_FLODER.getCode());
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
            // 新增文件时，可能会多值。
            String[] fields = fileIds.split(",");
            String[] sizeFields = sizes.split(",");
            String[] fileTypeFields = fileTypes.split(",");
            String[] nameFields = names.split(",");
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
                for (Map<String, Object> stringObjectMap : fileList) {
                    resourceManageDTO.setSort(busResourceManageRepo.maxSort() + 1);
                    resourceManageDTO.setFileId(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_ID) + "");
                    resourceManageDTO.setFileType(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_TYPE) + "");
                    resourceManageDTO.setSize(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_SIZE) + "");
                    resourceManageDTO.setName(stringObjectMap.get(ServiceConstants.RESOURCE_FILE_NAME) + "");
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
                    BusResourceEmbeddingDTO busResourceEmbeddingDTO = embeddingMapping.configDto2Dto(embeddingConfig);
                    busResourceEmbeddingDTO.setResourceId(Math.toIntExact(resourceId));
                    embeddingRepo.add(busResourceEmbeddingDTO);

                }
                // TODO 该段逻辑后续可能更改，当前设置为一个用户仅一个个人知识库
                String datasetsId = "";
                if (ResourceCategoryEnum.UNIT.getName().equals(parent.getCategory())){
                    datasetsId = datasetsConfigProperties.getUnitId();
                }
                if (ResourceCategoryEnum.DEP.getName().equals(parent.getCategory())){
                    BusResourceDatasetDTO datasetDTO = datasetRepo.getByCode(parent.getDepNum());
                    datasetsId = null != datasetDTO ? datasetDTO.getDatasetsId() : this.createDatasets(dep.getDepName(), parent.getDepNum());
                    datasetRepo.add(ResourceCategoryEnum.DEP.getCode(), parent.getDepNum(), datasetsId);
                }
                if (ResourceCategoryEnum.USER.getName().equals(parent.getCategory())){
                    //根据用户查是否存在个人知识库
                    BusResourceDatasetDTO datasetDTO = datasetRepo.getByCode(currentUser.getUserId());
                    // 不存在个人知识库，新建知识库  调用有云创建空知识库
                    datasetsId = null != datasetDTO ? datasetDTO.getDatasetsId() : this.createDatasets(currentUser.getUserName(), currentUser.getLoginId());
                    datasetRepo.add(ResourceCategoryEnum.USER.getCode(), currentUser.getUserId(), datasetsId);
                }
                for (Long resourceId : resourceIds) {
                    // 根据资源ID查fileId
                    BusResourceManageEntity manageEntity = busResourceManageRepo.getById(resourceId);
                    // 存用户关联文档表
                    RelUserResourceDTO relUserResourceDTO = new RelUserResourceDTO();
                    relUserResourceDTO.setResourceId(resourceId);
                    relUserResourceDTO.setUserId(userId);
                    relUserResourceDTO.setDatasetsId(datasetsId);
                    relUserResourceDTO.setFileId(manageEntity.getFileId());
                    relUserResourceRepo.add(relUserResourceDTO);

                    SyncFileDTO syncFileDTO = new SyncFileDTO();
                    syncFileDTO.setFileName(manageEntity.getName() + "." + manageEntity.getFileType());
                    syncFileDTO.setFileId(manageEntity.getFileId());
                    syncFileDTO.setDatasetId(datasetsId);
                    syncFileDTO.setFileEmbeddingConfigCode(fileEmbeddingConfigCode);
                    syncFileDTO.setFileEmbeddingConfigName(resourceManageDTO.getEmbeddingConfigName());
                    syncFileDTO.setEmbeddingConfig(embeddingConfig);
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
            this.syncFileToDify(syncFileDTOS);
        }
        return reBool ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.ADD_FAIL);
    }

    private String createDatasets(String name, String id){
        String datasetsName = ServiceConstants.CUS_DATASETS_DEFAULT_NAME + "-" + name + "(" + id + ")";
        String datasetsId = this.createDatasetsId(datasetsName);
        log.info("知识库创建成功!返回知识库ID为：{}", datasetsId);
        return datasetsId;
    }

    /**
     * 同步文件到dify知识库并更新documentId和batch
     * @param syncFileDTOS
     */
    private void syncFileToDify(List<SyncFileDTO> syncFileDTOS){
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
        if (StringUtils.isEmpty(configCode) && null != configDTO){
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
        if (entity.getFixed()){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.USER.getName().equals(entity.getCategory()) && !entity.getCreateUserId().equals(currentUser.getUserId())){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory()) && !entity.getDepNum().equals(currentUser.getDepNum())){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(entity.getCategory()) && !this.checkEditUnitAuth()){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (!entity.getParentId().equals(resourceManageDTO.getParentId())){
            BusResourceManageEntity newParent = busResourceManageRepo.getById(resourceManageDTO.getParentId());
            //检查循环
            if (entity.getResourceType().equals(ResourceTypeEnum.RESOURCE_FLODER.getCode())) {
                List<BusResourceManageDTO> childrenList = this.getChildrenList(busResourceManageRepo.listResourceFloder(), new ArrayList<>(), Math.toIntExact(entity.getId()));
                List<Integer> idList = Linq.select(childrenList, BusResourceManageDTO::getId);
                idList.add(Math.toIntExact(entity.getId()));
                if (idList.contains(resourceManageDTO.getParentId())){
                    return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
                }
            }
            if (!newParent.getCategory().equals(entity.getCategory())){
                return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，不可跨越知识库移动");
            }
            resourceManageDTO.setParentGuid(newParent.getGuid());
        }
        String userId = StringUtils.isBlank(ContextUtil.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : ContextUtil.getUserId();
        resourceManageDTO.setUpdateUserId(userId);
        if (!entity.getName().equals(resourceManageDTO.getName())){
            //处理重复名字
            List<BusResourceManageDTO> otherCode = busResourceManageRepo.listByParentIdAndResourceType(Long.valueOf(resourceManageDTO.getParentId()), entity.getResourceType());
            if (CollUtil.isNotEmpty(otherCode)) {
                List<String> existNameList = Linq.select(otherCode, BusResourceManageDTO::getName);
                if (existNameList.contains(resourceManageDTO.getName())) {
                    resourceManageDTO.setName(this.generateUniqueName(resourceManageDTO.getName(), existNameList));
                }
            }
        }
        if (!entity.getEmbeddingConfigCode().equals(resourceManageDTO.getEmbeddingConfigCode()) || entity.getEmbeddingConfigCode().isEmpty()){
            FileEmbeddingConfigDTO embeddingConfig;
            String fileEmbeddingConfigCode = StringUtils.isEmpty(resourceManageDTO.getEmbeddingConfigCode()) ? "" : resourceManageDTO.getEmbeddingConfigCode();
            embeddingConfig = this.getEmbeddingConfigByConfigCode(fileEmbeddingConfigCode, resourceManageDTO.getEmbeddingConfig());

            BusResourceEmbeddingDTO busResourceEmbeddingDTO = embeddingMapping.configDto2Dto(embeddingConfig);
            busResourceEmbeddingDTO.setResourceId(resourceManageDTO.getId());
            embeddingRepo.delete(resourceManageDTO.getId());
            embeddingRepo.add(busResourceEmbeddingDTO);
            resourceManageDTO.setEmbeddingConfigCode(embeddingConfig.getConfigCode());
            resourceManageDTO.setEmbeddingConfigName(embeddingConfig.getConfigName());

            if (null != relUserResourceDTO){
                SyncFileDTO syncFileDTO = new SyncFileDTO();
                syncFileDTO.setFileName(resourceManageDTO.getName() + "." + entity.getFileType());
                syncFileDTO.setFileId(relUserResourceDTO.getFileId());
                syncFileDTO.setDatasetId(relUserResourceDTO.getDatasetsId());
                syncFileDTO.setFileEmbeddingConfigCode(fileEmbeddingConfigCode);
                syncFileDTO.setFileEmbeddingConfigName(resourceManageDTO.getEmbeddingConfigName());
                syncFileDTO.setEmbeddingConfig(embeddingConfig);
                //同步文件到dify知识库并更新documentId和batch
                this.syncFileToDify(List.of(syncFileDTO));
            }
        }
        boolean b = busResourceManageRepo.update(resourceManageDTO);
        return b ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.EDIT_FAIL);
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
        if (entity.getFixed()){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.USER.getName().equals(entity.getCategory()) && !entity.getCreateUserId().equals(currentUser.getUserId())){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.DEP.getName().equals(entity.getCategory()) && !entity.getDepNum().equals(currentUser.getDepNum())){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        if (ResourceCategoryEnum.UNIT.getName().equals(entity.getCategory()) && !this.checkEditUnitAuth()){
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }
        boolean success = busResourceManageRepo.delete(id);
        if (ResourceTypeEnum.RESOURCE_FILE.getCode() == entity.getResourceType()){
            RelUserResourceDTO document = relUserResourceRepo.getOneByFileId(entity.getFileId());
            if (null != document){
                this.deleteDocument(document.getDatasetsId(), document.getDocumentId());
            }
            relUserResourceRepo.deleteByFileId(entity.getFileId());
            fileApi.deleteFileCommon(entity.getFileId());
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
        if (ResourceOperateEnum.INNER.getCode().equals(operateType) || !operateNode.getParentId().equals(targetNode.getParentId())){
            Integer parentId = ResourceOperateEnum.INNER.getCode().equals(operateType) ? targetId : targetNode.getParentId();
            BusResourceManageEntity newParent = busResourceManageRepo.getById(parentId);
            //检查循环
            if (operateNode.getResourceType().equals(ResourceTypeEnum.RESOURCE_FLODER.getCode())) {
                List<BusResourceManageDTO> childrenList = this.getChildrenList(busResourceManageRepo.listResourceFloder(), new ArrayList<>(), Math.toIntExact(operateNode.getId()));
                List<Integer> idList = Linq.select(childrenList, BusResourceManageDTO::getId);
                idList.add(Math.toIntExact(operateNode.getId()));
                if (idList.contains(parentId)){
                    return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
                }
            }
            if (!newParent.getCategory().equals(operateNode.getCategory())){
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
     * @param parentIdList   要获取的父类别的类别编码列表
     * @param parentId   要获取的父类别的类别编码
     * @return 结果
     */
    private List<BusResourceManageDTO> getParentList(List<BusResourceManageDTO> parentList, List<Integer> parentIdList, Integer parentId){
        if (null == parentId){
            return parentList;
        }
        BusResourceManageDTO parent = busResourceManageMapping.entity2Dto(busResourceManageRepo.getById(Long.valueOf(parentId)));
        if (parentIdList.contains(parent.getId())){
            return parentList;
        }
        parentIdList.add(parent.getId());
        parentList.add(parent);
        return getParentList(parentList, parentIdList, parent.getParentId());
    }

    private List<BusResourceManageDTO> getChildrenList(List<BusResourceManageDTO> list, List<BusResourceManageDTO> childList, Integer parentId){
        List<BusResourceManageDTO> allChild = Linq.find(list, x -> parentId.equals(x.getParentId()));
        if (CollUtil.isEmpty(allChild)){
            return childList;
        }
        childList.addAll(allChild);
        for (BusResourceManageDTO child : childList){
            getChildrenList(list, childList, child.getId());
        }
        return childList;
    }

    @Override
    public RestResponse updateSort(List<BusResourceManageDTO> list) {
        for (BusResourceManageDTO resourceDTO : list){
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
     * @param datasetId  知识库ID
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
            for (Map subData : dataList){
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

                if (existNameList.contains(dto.getName())){
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
}