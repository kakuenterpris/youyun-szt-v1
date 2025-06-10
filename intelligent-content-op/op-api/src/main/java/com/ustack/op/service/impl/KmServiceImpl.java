package com.ustack.op.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.ustack.emdedding.dto.FileUploadRecordDTO;
import com.ustack.emdedding.dto.RagProcessDTO;
import com.ustack.feign.client.FileApi;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.Linq;
import com.ustack.login.enums.UserSpecialAuthEnum;
import com.ustack.op.entity.*;
import com.ustack.op.enums.FileScopeTypeEnum;
import com.ustack.op.mapper.BusResourceFolderMapper;
import com.ustack.op.mapper.FileEmbeddingConfigMapper;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.mappings.BusResourceEmbeddingMapping;
import com.ustack.op.mappings.BusResourceFileMapping;
import com.ustack.op.mappings.BusResourceFolderMapping;
import com.ustack.op.mappings.BusResourceManageMapping;
import com.ustack.op.properties.AiConfigProperties;
import com.ustack.op.properties.ApikeyConfigProperties;
import com.ustack.op.repo.*;
import com.ustack.op.runnable.ConvertMarkdownRunnable;
import com.ustack.op.service.KmService;
import com.ustack.op.service.RagFlowProcessService;
import com.ustack.op.service.ResourceProcessService;
import com.ustack.op.util.HttpUtils;
import com.ustack.resource.constants.ServiceConstants;
import com.ustack.resource.dto.*;

import com.ustack.resource.enums.*;
import com.ustack.resource.param.SaveFileParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Service实现
 * @createDate 2025-02-18 17:57:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KmServiceImpl implements KmService {
    private final AiConfigProperties aiConfigProperties;
    private final ApikeyConfigProperties apikeyConfigProperties;
    private final FileApi fileApi;

    private final BusResourceFolderRepo folderRepo;
    private final BusResourceFileRepo fileRepo;
    private final BusResourceMemberRepo memberRepo;
    private final BusUserInfoRepo userInfoRepo;
    private final BusResourceFolderMapping folderMapping;
    private final BusResourceFileMapping fileMapping;

    private final BusResourceManageRepo busResourceManageRepo;
    private final RelUserResourceRepo relUserResourceRepo;
    private final FileEmbeddingConfigRepo embeddingConfigRepo;
    private final BusResourceEmbeddingRepo embeddingRepo;
    private final BusResourceManageMapping busResourceManageMapping;
    private final BusResourceEmbeddingMapping embeddingMapping;
    private final ResourceProcessService resourceProcessService;
    private final FileEmbeddingConfigMapper fileEmbeddingConfigMapper;
    private final RagFlowProcessService ragFlowProcessService;
    private final FileAuthRepo fileAuthRepo;
    private final SysRoleRepo sysRoleRepo;


    private final BusResourceDatasetRepo busResourceDatasetRepo;


    // 操作日志
    private final SysOptLogRepo sysOptLogRepo;

    private final FolderAuthRepo folderAuthRepo;
    private final SysUserRoleRepo sysUserRoleRepo;

    private final FileUploadRecordMapper fileUploadRecordMapper;

    private final BusResourceFolderMapper busResourceFolderMapper;
    @Value("${file.base.path}")
    private String fileBasePath;
    private static final String[] toMarkdownWhiteList = {"PDF", "PPT", "PPTX", "DOC", "PNG", "JPG",
            "pdf", "ppt", "pptx", "doc", "docx", "png", "jpg"};

    /**
     * 左侧树
     */
    @Override
    public RestResponse resourceTreeListLeft() {
        // 将左侧文件夹列表组装成树形结构
        return RestResponse.success(TreeNodeServiceImpl.assembleTree(getResourceListLeft("", null, Objects.requireNonNull(ContextUtil.currentUser()))));
    }

    /**
     * 左侧文件夹列表
     */
    @Override
    public List<BusResourceManageListDTO> getResourceListLeft(String requestType, Integer folderType, SystemUser currentUser) {

        String userId = StringUtils.isBlank(currentUser.getUserId()) ? ServiceConstants.DEFAULT_USER_ID : currentUser.getUserId();
        List<BusResourceManageListDTO> result = new ArrayList<>();
        List<BusResourceManageListDTO> allList = new ArrayList<>();
        if ("wonderfulPen".equals(requestType)) {
            allList = Linq.select(folderRepo.listAllByType(true, folderType), folderMapping::dto2ListDto);
        } else {
            allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        }
        Boolean systemAdminAuth = this.checkSystemAdminAuth(userId);
        if (systemAdminAuth) {
            //系统管理员可查看所有文件夹(系统管理员不在文件夹成员里的话，不可查看该文件夹下的文件，但可查看该文件夹下的文件夹)
            result = allList;
        } else {
//            用户角色
            Long roleId = sysUserRoleRepo.getOne(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, currentUser.getId())).getRoleId();
            List<Integer> adminFolderIds = Linq.select(folderAuthRepo.list(new LambdaQueryWrapper<FolderAuthEntity>().eq(FolderAuthEntity::getRoleId, roleId)), FolderAuthEntity::getFolderId);
            List<Integer> createFolderids = Linq.select(folderRepo.list(new LambdaQueryWrapper<BusResourceFolderEntity>().eq(BusResourceFolderEntity::getCreateUserId, userId)), BusResourceFolderEntity::getId).stream().map(Long::intValue).collect(Collectors.toList());
            adminFolderIds.addAll(createFolderids);
            //文件夹包含的文件权限
            List<Integer> folderOfFileIds = fileListByUserAuth(userId);
            List<Integer> union= (List<Integer>) CollectionUtils.union(adminFolderIds, folderOfFileIds);
            //有权限的文件夹：向上 和 向下递归
            //向上递归的文件夹ids
            List<BusResourceManageListDTO> parentList = TreeNodeServiceImpl.getParentList(allList, union);
            result = parentList;
            //向下递归的文件夹ids
            List<BusResourceManageListDTO> childrenList = TreeNodeServiceImpl.getChildrenList(allList, union);

            //有权限的文件夹
            List<BusResourceManageListDTO> authChildrenList = TreeNodeServiceImpl.getChildrenList(allList, adminFolderIds);
            result.addAll(childrenList);
            result = result.stream().distinct().sorted(Comparator.comparing(BusResourceManageListDTO::getId)).collect(Collectors.toList());
            for (BusResourceManageListDTO x : result) {
                if (x.getId() != null && authChildrenList.contains(x)) {
                    x.setEditAuth(true);
                }else {
                    x.setEditAuth(false);
                }
            }

        }
        return result;
    }

    /**
     * 右侧树
     */
    @Override
    public RestResponse resourceListRight(QueryDTO query) {
        return resourceListRight(query, true);
    }

    /**
     * 右侧树
     *
     * @param query     查询参数
     * @param notDelete 是否删除（为true时，查询未删除的文件/文件夹，为false时，查询所有文件/文件夹，仅当操作日志调该方法时传false）
     */
    @Override
    public RestResponse resourceListRight(QueryDTO query, boolean notDelete) {
        //        1.可以看本文件夹下的所有文件和本文件夹下的所有文件夹：
//            1.1：本文件夹公开
//            1.2：本文件夹非公开：文件夹管理员  上级文件夹的管理员  文件夹成员
//        2.可以看本文件夹下的所有文件夹；但不能看本文件夹下的文件：不满1 且 是系统管理员
//        3.可以看本文件夹下的某些文件夹，上述'本文件夹下的某些文件夹'是下面某一层级有查看权限的文件夹的上级文件夹；但不能看本文件夹下的文件：不满足1 且 不满足2
        System.out.println("当前时间1  "+System.currentTimeMillis());
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        Integer parentId = query.getParentId();
        List<SysRoleEntity> roleByUserId = sysRoleRepo.getRoleByUserId(Integer.valueOf(currentUser.getId()));
        List<Long> roleList = Linq.select(roleByUserId, SysRoleEntity::getRoleId);
        List<Integer> folderIdList = new ArrayList<>();
        Boolean viewFile = false;
        List<Integer> fileList=null;
        List<BusResourceFolderDTO> busResourceFolderDTOS = folderRepo.listAll(notDelete);
        BusResourceFolderDTO entity = busResourceFolderDTOS.stream().filter(x -> x.getId().equals(parentId)).findFirst().orElse(null);
        List<BusResourceFolderDTO> collect1 = busResourceFolderDTOS.stream().filter(x -> x.getParentId().equals(parentId)).collect(Collectors.toList());
        if (null == entity) {
            return RestResponse.success(new ArrayList<>(), 0);
        }
        Boolean systemAdminAuth = this.checkSystemAdminAuth(userId);
        //本文件夹下的所有直接下级文件夹id
        folderIdList = Linq.select(collect1, BusResourceFolderDTO::getId);
        List<BusResourceManageListDTO> allList = Linq.select(busResourceFolderDTOS, folderMapping::dto2ListDto);
        //查询创建的文件夹
        List<BusResourceFolderDTO> createFolder = busResourceFolderDTOS.stream().filter(x -> x.getCreateUserId().equals(userId)).collect(Collectors.toList());
        List<BusResourceFolderEntity> createEntity = Linq.select(createFolder, folderMapping::dto2Entity);
        List<Integer> adminFolderIds = Linq.select(createEntity, BusResourceFolderEntity::getId).stream().map(Long::intValue).collect(Collectors.toList());;
        List<BusResourceManageListDTO> result = new ArrayList<>();
        Integer count = 0;
        System.out.println("当前时间2  "+System.currentTimeMillis());
        // 查询操作 查看父文件目录下左右匹配的文件夹和目录
        if (StringUtils.isNotEmpty(query.getName())||query.getIndexingStatus()!=null||query.getEmbeddingStatus()!=null||query.getLevel()!=null||query.getStartTime()!=null||query.getEndTime()!=null) {
            List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, parentId);
            List<Integer> childIds = Linq.select(childList, BusResourceManageListDTO::getId);
            List<Integer> authFolderIds = Linq.select(folderAuthRepo.list(new LambdaQueryWrapper<FolderAuthEntity>().in(FolderAuthEntity::getRoleId, roleList)), FolderAuthEntity::getFolderId);
//            向下遍历
            List<Integer> authFolderList = Linq.select(TreeNodeServiceImpl.getChildrenList(allList, authFolderIds), BusResourceManageListDTO::getId);
            List<Integer> canViewFolderIds = authFolderList;
            canViewFolderIds.addAll(adminFolderIds);
            //要查看的文件夹下的所有层级的有权限查看的文件夹
            List<Integer> canViewfiles = Linq.select(fileAuthRepo.listFileIdByUser(userId), FileAuthEntity::getFileId);
            //要查看的文件夹下的所有层级的有权限查看的
            if (systemAdminAuth){
                canViewFolderIds = childIds;
                canViewfiles=null;
            }
            List<Integer> canViewList = (List<Integer>) CollectionUtils.intersection(childIds, canViewFolderIds);
            query.setParentId(null);
            result = fileRepo.selectFileList(canViewList,canViewfiles, query, notDelete);
            count = fileRepo.selectFileListCount(canViewList,canViewfiles, query, notDelete);
            List<Integer> resultId = Linq.select(result, BusResourceManageListDTO::getId);
            List<FileAuthEntity> fileAuths= new ArrayList<>();
            if (resultId.size() > 0){
                fileAuths=fileAuthRepo.list(new LambdaQueryWrapper<FileAuthEntity>().in(FileAuthEntity::getId, resultId));
            }
            for (BusResourceManageListDTO busResourceManageListDTO : result) {
                List<FileAuthEntity> collect = fileAuths.stream().filter(fileAuthEntity -> fileAuthEntity.getFileId().equals(busResourceManageListDTO.getId())).collect(Collectors.toList());
                List<Integer> select = Linq.select(collect, FileAuthEntity::getId);
                busResourceManageListDTO.setScope(select);
            }
            System.out.println("当前时间3  "+System.currentTimeMillis());
            return RestResponse.success(result, count);


        } else {
            Boolean aBoolean = this.checkUpFolderAdminAuth(parentId, roleList, notDelete);
            if (systemAdminAuth||aBoolean) {
                viewFile = true;
            } else if (CollUtil.isNotEmpty(folderIdList)) {
                List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, parentId);
                List<Integer> childIds = Linq.select(childList, BusResourceManageListDTO::getId);
                List<Integer> folderAuthlist = new ArrayList<>();
                //有权限的文件夹
                for (Long roleId : roleList) {
                    List<Integer> list = Linq.select(folderAuthRepo.list(new LambdaQueryWrapper<FolderAuthEntity>().eq(FolderAuthEntity::getRoleId, roleId)), FolderAuthEntity::getFolderId);
                    folderAuthlist.addAll(list);
                }
                List<Integer> canViewIds = folderAuthlist;
                canViewIds.addAll(adminFolderIds);
                //要查看的文件夹下的所有层级的有权限查看的文件夹
               //todo 有权限的文件
                List<Integer> integers = fileListByUserAuth(userId);
                List<Integer> canViewList = (List<Integer>) CollectionUtils.intersection(childIds, canViewIds);
                canViewList.addAll(integers);
                //要查看的文件夹下的所有层级的有权限查看的文件夹，向上递归，找到 要查看的文件夹下的所有层级的有权限查看的文件夹的所有上级文件夹
                List<BusResourceManageListDTO> parentList = TreeNodeServiceImpl.getParentList(childList, canViewList);
                List<Integer> parentIds = Linq.select(parentList, BusResourceManageListDTO::getId);
                //要查看的文件夹下的所有层级的有权限查看的文件夹的所有上级文件夹  和  本文件夹下的所有直接下级文件夹id  作交集
                folderIdList = (List<Integer>) CollectionUtils.intersection(folderIdList, parentIds);
            }
            //判断是否有权限查看本文件夹下的文件
            if (viewFile==false){
                List<Integer> parentIdFilelist = Linq.select(fileRepo.list(new LambdaQueryWrapper<BusResourceFileEntity>().eq(BusResourceFileEntity::getFolderId, parentId)),BusResourceFileEntity::getId).stream().map(Long::intValue).collect(Collectors.toList());;
                List<Integer> haveAuthList = Linq.select(fileAuthRepo.list(new LambdaQueryWrapper<FileAuthEntity>().eq(FileAuthEntity::getUserId, userId)), FileAuthEntity::getFileId);
                fileList=(List<Integer>)CollectionUtils.intersection(parentIdFilelist, haveAuthList);
            }
            if (entity.getOpenView() || checkMemberViewAuth(parentId, userId)) {
                viewFile = true;
            }

            if (CollUtil.isNotEmpty(query.getFileYearList()) || CollUtil.isNotEmpty(query.getEmbeddingConfigNameList())){
                folderIdList = new ArrayList<>();
            }
            if (CollUtil.isEmpty(folderIdList) && !viewFile&&(fileList.size()==0)) {
                return RestResponse.success(new ArrayList<>(), 0);
            }

            result = fileRepo.resourceListRight(folderIdList,fileList,viewFile, query, notDelete);
            count=fileRepo.resourceListRightCount(folderIdList,fileList, viewFile, query, notDelete);
            List<Integer> resultId = Linq.select(result, BusResourceManageListDTO::getId);
            List<FileAuthEntity> fileAuths= new ArrayList<>();
            if (resultId.size() > 0){
                fileAuths=fileAuthRepo.list(new LambdaQueryWrapper<FileAuthEntity>().in(FileAuthEntity::getId, resultId));
            }
            for (BusResourceManageListDTO busResourceManageListDTO : result) {
                if (busResourceManageListDTO.getFileId()!=null&&busResourceManageListDTO.getFileId()!=""){
                    List<FileAuthEntity> collect = fileAuths.stream().filter(fileAuthEntity -> fileAuthEntity.getFileId().equals(busResourceManageListDTO.getId())).collect(Collectors.toList());
                    List<Integer> select = Linq.select(collect, FileAuthEntity::getId);
                    busResourceManageListDTO.setScope(select);
                }
            }
            System.out.println("当前时间3.5  "+System.currentTimeMillis());
        }
        List<Integer> idList = result.stream().map(BusResourceManageListDTO::getFolderId).distinct().toList();
        List<BusResourceMemberDTO> authList = this.getFolderAuthList(idList);
        List<BusResourceManageListDTO> allAdminList = TreeNodeServiceImpl.getChildrenList(allList, adminFolderIds);
        List<Integer> allAdminIds = Linq.select(allAdminList, BusResourceManageListDTO::getId);
        for( BusResourceManageListDTO dto : result){
            if (ResourceTypeEnum.RESOURCE_FILE.getCode().equals(dto.getResourceType())){
                List<FileAuthEntity> list = fileAuthRepo.list(new LambdaQueryWrapper<FileAuthEntity>().eq(FileAuthEntity::getFileId, dto.getId()));
                dto.setScope(Linq.select(list, FileAuthEntity::getUserId));
                if (viewFile==false&&null!=fileList&&fileList.contains(dto.getId())){
                    dto.setEditAuth(false);
                }else {
                    dto.setEditAuth(true);
                }
            }else {
                dto.setViewLogAuth(ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(dto.getResourceType()) && dto.getCreateUserId().equals(userId));
                dto.setEditAuth(ResourceTypeEnum.RESOURCE_FOLDER.getCode().equals(dto.getResourceType()) && (systemAdminAuth || allAdminIds.contains(dto.getId())));
            }
            if(StringUtils.isEmpty(dto.getIndexingStatusName()) ){
                dto.setIndexingStatusName("--");
            }
        }
        System.out.println("当前时间4  "+System.currentTimeMillis());

        /*//记录操作日志
        String operateType = OperateTypeEnum.GET.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FOLDER;
        boolean b = sysOptLogRepo.saveLog(entity.getName(), Long.valueOf(parentId), Long.valueOf(entity.getParentId()), operateType, resourceTypeEnum);
        String msg = ContextUtil.getUserName() + operateType + entity.getName() + resourceTypeEnum.getName();
        log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");*/
        return RestResponse.success(result, count);
    }

    //文件夹包含有权限文件的文件夹
    private List<Integer>  fileListByUserAuth(String userId) {
        List<FileAuthEntity> fileAuthEntities = fileAuthRepo.listFileIdByUser(userId);
        List<Integer> filIds = Linq.select(fileAuthEntities, FileAuthEntity::getFileId);
        List<BusResourceFileEntity> busResourceFileEntities = fileRepo.listByIds(filIds);
        List<Integer> folders = Linq.select(busResourceFileEntities, BusResourceFileEntity::getFolderId).stream().distinct().collect(Collectors.toList());
        return folders;
    }

    private boolean checkFolderViewAuth(Integer parentId, String userId) {
        List<BusResourceMemberDTO> dtoList = memberRepo.listMemberByUser(userId);
        return dtoList.stream()
                .anyMatch(e -> e.getFolderId().equals(parentId));
    }

    @Override
    public RestResponse updateJoinQuery(QueryDTO query) {
        SystemUser currentUser = ContextUtil.currentUser();
        fileRepo.updateJoinQuery(query.getIdList(), query.getJoinQuery());
        // 查询文件id
        List<BusResourceFileDTO> list = fileRepo.listFileIdByIdList(query.getIdList());
        if (CollUtil.isNotEmpty(list)) {
            // 获取文件id
            List<String> fileIdList = list.stream().map(BusResourceFileDTO::getFileId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(fileIdList)) {
                // 批量更新文件
                resourceProcessService.updateJoinQuery(query.getJoinQuery(), fileIdList);
            }
        }
        return RestResponse.SUCCESS;
    }

    /**
     * 获取文件操作权限
     */
    @Override
    public RestResponse getAuth(Integer id) {
        return RestResponse.success(this.getFileAuth(id));
    }

    private BusResourceMemberDTO getFileAuth(Integer id) {
        SystemUser currentUser = ContextUtil.currentUser();
        BusResourceFolderDTO folder = folderRepo.getOneById(Long.valueOf(id));
        //auth 不为 null，说明是 文件夹管理员 或 文件夹成员，直接返回查询到的权限数据
        BusResourceMemberDTO auth = memberRepo.getByUser(id, currentUser.getUserId());
        boolean isMember = null != auth;
        if (!isMember) {
            //auth 为 null，需继续判断
            //系统管理员不能操作没有权限的文件夹下的文件
            //判断文件夹是否为公开 或 当前人是否是上级文件夹的管理员
            boolean admin = this.checkUpFolderAdminAuth(id, currentUser.getUserId(), true);
            boolean operateAuth = null != folder && (folder.getOpenView() || admin);
            boolean uploadAuth = null != folder &&  folder.getOpenView() && currentUser.getUserId().equals(folder.getCreateUserId());
            auth = new BusResourceMemberDTO();
            auth.setIsAdmin(admin);
            auth.setViewAuth(operateAuth);
            auth.setDownloadAuth(operateAuth);
            auth.setShareAuth(operateAuth);
            auth.setUploadAuth(uploadAuth || admin);
            auth.setEditAuth(uploadAuth || admin);
            auth.setDeleteAuth(uploadAuth || admin);
        }
        auth.setFolderId(id);
        //新建子文件夹权限：父文件夹需设置可以建子文件夹，操作人需要是父文件夹成员或管理员（本级或上级或系统管理员）或者父文件夹为公开文件夹
//        if(folder!=null){
//            auth.setAddFolderAuth(folder.getCanAddSub() && (folder.getOpenView() || auth.getIsAdmin()
//                || this.checkSystemAdminAuth(currentUser.getUserId()) || isMember));
//        }
        return auth;
    }

    @Override
    public List<BusResourceMemberDTO> getFolderAuthList(List<Integer> idList) {
        List<BusResourceMemberDTO> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(idList)) {
            for (Integer id : idList) {
                BusResourceMemberDTO dto = this.getFileAuth(id);
                result.add(dto);
            }
        }
        return result;
    }

    /**
     * 获取文件夹成员
     */
    @Override
    public RestResponse getMember(Integer id) {
        return RestResponse.success(memberRepo.list(id));
    }

    private Boolean checkEditUnitAuth() {
        SystemUser currentUser = ContextUtil.currentUser();
        return currentUser.getSpecialAuth().contains(UserSpecialAuthEnum.UNIT_FILE_MANAGE.getAuthCode());
    }

    /**
     * 检查系统管理员权限
     * @param userId
     * @return
     */
    private Boolean checkSystemAdminAuth(String userId) {
        return userInfoRepo.checkAuth(userId, UserSpecialAuthEnum.SYSTEM_MANAGE.getAuthCode());
    }

    /**
     * 检查文件夹管理员权限
     * @param userId
     * @return
     */
    private Boolean checkFolderAdminAuth(Integer folderId, String userId) {
        BusResourceMemberDTO auth = memberRepo.getByUser(folderId, userId);
        return null != auth && auth.getIsAdmin();
    }

    /**
     * 检查上级文件夹的管理员权限
     *
     * @param userId
     * @param notDelete 是否删除（为true时，查询未删除的文件夹，为false时，查询所有文件夹，仅当操作日志调该方法时传false）
     * @return
     */
    private Boolean checkUpFolderAdminAuth(Integer folderId, String userId, boolean notDelete) {
        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(notDelete), folderMapping::dto2ListDto);
        List<BusResourceManageListDTO> parentList = TreeNodeServiceImpl.getParentList(allList, folderId);
        if (CollUtil.isEmpty(parentList)) {
            return false;
        }
        List<Integer> adminFolderIds = Linq.select(memberRepo.listAdminByUser(userId), BusResourceMemberDTO::getFolderId);
        return adminFolderIds.stream().anyMatch(Linq.select(parentList, BusResourceManageListDTO::getId)::contains);
    }

    private Boolean checkUpFolderAdminAuth(Integer folderId, List<Long>  roles, boolean notDelete) {
        for (Long role : roles) {
            List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(notDelete), folderMapping::dto2ListDto);
            List<BusResourceManageListDTO> parentList = TreeNodeServiceImpl.getParentList(allList, folderId);
            if (CollUtil.isEmpty(parentList)) {
                return false;
            }
            List<Integer> adminFolderIds = Linq.select(folderAuthRepo.listByRoleId(role), FolderAuthEntity::getFolderId);
            return adminFolderIds.stream().anyMatch(Linq.select(parentList, BusResourceManageListDTO::getId)::contains);
        }
        return false;
    }

    /**
     * 检查文件夹成员权限
     * @param userId
     * @return
     */
    private Boolean checkMemberViewAuth(Integer folderId, String userId) {
        BusResourceMemberDTO auth = memberRepo.getByUser(folderId, userId);
        return null != auth && auth.getViewAuth();
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

    /**
     * 保存文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse saveFolder(BusResourceFolderDTO dto) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        if (null == dto) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
        BusResourceFolderEntity origin = folderRepo.getById(dto.getId());
        boolean edit = null != origin;
        Long folderId = edit ? origin.getId() : null;
        String name = dto.getName();

        // 查父ID文件类型
        BusResourceFolderEntity parent = folderRepo.getById(dto.getParentId());
        if (null == parent) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上级文件夹未找到");
        }
        //判断权限
        //新建子文件夹权限：父文件夹需设置可以建子文件夹，操作人需要是父文件夹成员或管理员（本级或上级）或者父文件夹为公开文件夹
        //编辑文件夹权限：操作人需要是文件夹的管理员（本级或上级或系统管理员）或者文件夹为公开文件夹
        //todo 判断父文件夹权限重写
//        boolean parentOperateAuth = this.checkSystemAdminAuth(userId) || this.checkFolderAdminAuth(Math.toIntExact(parent.getId()), userId)
//                || this.checkUpFolderAdminAuth(Math.toIntExact(parent.getId()), userId, true) || this.checkMemberViewAuth(Math.toIntExact(parent.getId()), userId);
//        if (!parent.getCanAddSub()
//                || (!parent.getOpenView() && !parentOperateAuth)){
//            if (!parent.getCanAddSub() || !edit || !origin.getOpenView() && !this.checkFolderAdminAuth(Math.toIntExact(folderId), userId)) {
//                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
//            }
//        }
        dto.setParentGuid(parent.getGuid());

        //编辑移动操作
        if (edit && !origin.getParentId().equals(dto.getParentId())) {
            boolean originOperateAuth = this.checkSystemAdminAuth(userId) || this.checkFolderAdminAuth(Math.toIntExact(folderId), userId)
                    || this.checkUpFolderAdminAuth(Math.toIntExact(folderId), userId, true);
            if (!originOperateAuth){
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
            }
            //检查循环
            List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
            List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, Math.toIntExact(origin.getId()));
            List<Integer> idList = Linq.select(childList, BusResourceManageListDTO::getId);
            idList.add(Math.toIntExact(origin.getId()));
            if (idList.contains(dto.getParentId())) {
                return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
            }
        }
        //改名或者新建操作
        if (!edit || !origin.getName().equals(name)) {
            //处理重复名字
            List<BusResourceFolderDTO> otherCode = folderRepo.listByParentId(parent.getId());
            if (CollUtil.isNotEmpty(otherCode)) {
                List<String> existNameList = Linq.select(otherCode, BusResourceFolderDTO::getName);
                if (existNameList.contains(name)) {
                    dto.setName(this.generateUniqueName(name, existNameList));
                }
            }
        }

        if (edit){
            folderRepo.update(dto);
            // 同时修改ragflow知识库
            LambdaQueryWrapper<BusResourceDatasetEntity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(BusResourceDatasetEntity::getCode, userId);
            lambdaQueryWrapper.eq(BusResourceDatasetEntity::getFolderId, folderId);
            lambdaQueryWrapper.eq(BusResourceDatasetEntity::getDeleted, 0);
            BusResourceDatasetEntity one = busResourceDatasetRepo.getOne(lambdaQueryWrapper);
            Boolean updated = ragFlowProcessService.updateDataset(one.getDatasetsId(), name);
            if (!updated){
                log.error("创建RagFlow知识库失败，name: {}", name);
                return RestResponse.fail(ResourceErrorCode.EDIT_FAIL.getCode(), "修改RagFlow知识库失败");
            }

        } else {
            //设置文件类型
            if (parent.getParentId() == 0) {
                //获取busResourceFolderEntities中type最大的值
                LambdaQueryWrapper<BusResourceFolderEntity> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.select(BusResourceFolderEntity::getType);
                queryWrapper.orderByDesc(BusResourceFolderEntity::getType);
                queryWrapper.last("LIMIT 1");
                BusResourceFolderEntity entity = busResourceFolderMapper.selectOne(queryWrapper);
                if (entity != null) {
                    dto.setType(entity.getType() + 1);
                } else {
                    dto.setType(1);
                }
            } else {
                dto.setType(parent.getType());
            }

            dto.setSort(folderRepo.maxSort() + 1);
            folderId = folderRepo.add(dto);
            // 同时调用ragflow的创建知识库的接口
            String dataSetId = ragFlowProcessService.createRagFlow(name);
            if (StringUtils.isNotEmpty(dataSetId)) {
                BusResourceDatasetEntity busResourceDatasetEntity = new BusResourceDatasetEntity();
                busResourceDatasetEntity.setCategoryCode("user");
                busResourceDatasetEntity.setCode(userId);
                busResourceDatasetEntity.setDatasetsId(dataSetId);
                busResourceDatasetEntity.setFolderId(folderId);
                boolean save = busResourceDatasetRepo.save(busResourceDatasetEntity);
            } else {
                log.error("创建RagFlow知识库失败，name: {}", name);
                return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "创建RagFlow知识库失败");
            }
        }
        BusResourceFolderDTO newDTO = folderRepo.getOneById(folderId);
        //保存成员权限
        memberRepo.delete(folderId);
        if(null != origin){
            dto.getMemberList().forEach(x -> {
                x.setCreateUser(origin.getCreateUser());
                x.setCreateUserId(origin.getCreateUserId());
                x.setUpdateUser(currentUser.getUserName());
                x.setUpdateUserId(userId);
            });
        }
        memberRepo.add(dto.getMemberList(), folderId);
        //记录操作日志
        String operateType = edit ? OperateTypeEnum.EDIT.getName() : OperateTypeEnum.ADD.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FOLDER;
        String operateContent = operateType + dto.getName() + resourceTypeEnum.getName();
        boolean b = sysOptLogRepo.saveLog(operateContent, folderId, Long.valueOf(dto.getParentId()), operateType, resourceTypeEnum.getCode());
        String msg = currentUser.getUserName() + operateContent;
        log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");

        return RestResponse.success(newDTO.getGuid());
    }

    /**
     * 保存文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse saveFile(SaveFileParam dto) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        List<BusResourceFileDTO> saveFileList = dto.getFileList();
        if (null == dto || CollUtil.isEmpty(saveFileList)) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
        // 查父ID文件类型
        BusResourceFolderEntity parent = folderRepo.getById(dto.getFolderId());
        if (null == parent) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上级文件夹未找到");
        }
        //todo判断权限
//        BusResourceMemberDTO fileAuth = this.getFileAuth(dto.getFolderId());
//        if (!fileAuth.getIsAdmin() && !fileAuth.getUploadAuth()){
//            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
//        }

//        List<RagProcessDTO> fileIdList = new ArrayList<>(); // 用于将文件同步到ragflow
        List<ConvertMarkdownDTO> mdList = new ArrayList<>(); // 用于将文件转换为md文件
        List<Map<String, Object>> fileList = new ArrayList<>();
        //处理重复名字
        List<BusResourceFileDTO> otherCode = fileRepo.list(Math.toIntExact(parent.getId()));
        try {
            for (BusResourceFileDTO fileDTO : saveFileList) {
                boolean add = null == fileDTO.getId();
                Long resourceFileId = add ? null : Long.valueOf(fileDTO.getId());

                //处理重复名字
                String fileType = fileDTO.getFileType();
                List<BusResourceFileDTO> subCodeList = Linq.find(otherCode, x -> fileType.equals(x.getFileType()));
                if (CollUtil.isNotEmpty(subCodeList)) {
                    List<String> existNameList = Linq.select(subCodeList, BusResourceFileDTO::getName);
                    if (existNameList.contains(fileDTO.getName())) {
                        fileDTO.setName(this.generateUniqueName(fileDTO.getName(), existNameList));
                    }
                }
                otherCode.add(fileDTO);
                fileDTO.setFolderId(dto.getFolderId());
                if (add) {
                    resourceFileId = fileRepo.add(fileDTO);
                    //根据知悉规则设置知悉范围
                    String scopeRule = fileDTO.getScopeRule();
                    if (!FileScopeTypeEnum.ONLY_ME.getType().equals(scopeRule)&&!FileScopeTypeEnum.CUSTOM.getType().equals(scopeRule)) {
                        Integer secretLevel = userInfoRepo.getById(userId).getSecretLevel();
                        LambdaQueryWrapper<BusUserInfoEntity> levelQuery = new LambdaQueryWrapper<>();
                        levelQuery.select(BusUserInfoEntity::getId);
                        if (FileScopeTypeEnum.SAME_SECURITY_LEVEL.getType().equals(scopeRule)){
                            //同级
                            levelQuery.eq(BusUserInfoEntity::getSecretLevel, secretLevel);
                        }else {
                            //同级及以上
                            levelQuery.gt(BusUserInfoEntity::getSecretLevel, secretLevel);
                        }
                        List<BusUserInfoEntity> someLevel = userInfoRepo.list(levelQuery);
                        List<Integer> someLevelId = someLevel.stream().map(BusUserInfoEntity::getId).collect(Collectors.toList());
                        fileDTO.setScope(someLevelId);
                    }
                    List<Integer> scope = fileDTO.getScope();
                    List<FileAuthEntity> fileAuthEntities = new ArrayList<>();
                    Integer fileId = Math.toIntExact(resourceFileId);
                    if(scope != null) {
                        scope.forEach(x -> {
                            FileAuthEntity fileAuthEntity = new FileAuthEntity();
                            fileAuthEntity.setFileId(fileId);
                            fileAuthEntity.setUserId(x);
                            fileAuthEntities.add(fileAuthEntity);
                        });
                    }
                    fileAuthRepo.saveBatch(fileAuthEntities);
                } else {
                    BusResourceFileEntity source = fileRepo.getById(resourceFileId);
                    //如果被修改或者是自定义
                    if (!fileDTO.getScopeRule().equals(source.getScopeRule())||FileScopeTypeEnum.CUSTOM.getType().equals(fileDTO.getScopeRule())){
                      //                    删除原有的权限
                      fileAuthRepo.remove(new LambdaQueryWrapper<FileAuthEntity>().eq(FileAuthEntity::getFileId, fileDTO.getId()));
                      //根据知悉规则设置知悉范围
                      String scopeRule = fileDTO.getScopeRule();
                      if (!FileScopeTypeEnum.ONLY_ME.getType().equals(scopeRule)&&!FileScopeTypeEnum.CUSTOM.getType().equals(scopeRule)) {
                          Integer secretLevel = userInfoRepo.getById(userId).getSecretLevel();
                          LambdaQueryWrapper<BusUserInfoEntity> levelQuery = new LambdaQueryWrapper<>();
                          levelQuery.select(BusUserInfoEntity::getId);
                          if (FileScopeTypeEnum.SAME_SECURITY_LEVEL.getType().equals(scopeRule)){
                              //同级
                              levelQuery.eq(BusUserInfoEntity::getSecretLevel, secretLevel);
                          }else {
                              //同级及以上
                              levelQuery.gt(BusUserInfoEntity::getSecretLevel, secretLevel);
                          }
                          List<BusUserInfoEntity> someLevel = userInfoRepo.list(levelQuery);
                          List<Integer> someLevelId = someLevel.stream().map(BusUserInfoEntity::getId).collect(Collectors.toList());
                          fileDTO.setScope(someLevelId);
                      }
                      List<Integer> scope = fileDTO.getScope();
                      List<FileAuthEntity> fileAuthEntities = new ArrayList<>();
                      Integer fileId = Math.toIntExact(resourceFileId);
                      if(scope != null) {
                          scope.forEach(x -> {
                              FileAuthEntity fileAuthEntity = new FileAuthEntity();
                              fileAuthEntity.setFileId(fileId);
                              fileAuthEntity.setUserId(x);
                              fileAuthEntities.add(fileAuthEntity);
                          });
                          fileAuthRepo.saveBatch(fileAuthEntities);
                      }

                  }
                    fileRepo.update(fileDTO);
                }

                //新建时执行
                if (add) {
                    Map<String, Object> file = new HashMap<>();
                    file.put(ServiceConstants.RESOURCE_FILE_NAME, fileDTO.getName());
                    file.put(ServiceConstants.RESOURCE_FILE_ID, fileDTO.getFileId());
                    file.put(ServiceConstants.RESOURCE_FILE_SIZE, fileDTO.getSize());
                    file.put(ServiceConstants.RESOURCE_FILE_TYPE, fileDTO.getFileType());
                    fileList.add(file);

                    // 存用户关联文档表
                    RelUserResourceDTO relUserResourceDTO = new RelUserResourceDTO();
                    relUserResourceDTO.setResourceId(resourceFileId);
                    relUserResourceDTO.setResourceFileId(Math.toIntExact(resourceFileId));
                    relUserResourceDTO.setUserId(userId);
                    relUserResourceDTO.setIndexingStatus("1");
                    relUserResourceDTO.setIndexingStatusName("未提取");
                    relUserResourceDTO.setDatasetsId("");
                    relUserResourceDTO.setFileId(fileDTO.getFileId());
                    relUserResourceRepo.add(relUserResourceDTO);
                    //提取规则
                    FileEmbeddingConfigDTO embeddingConfig;
                    String fileEmbeddingConfigCode = StringUtils.isEmpty(fileDTO.getEmbeddingConfigCode()) ? "" : fileDTO.getEmbeddingConfigCode();
                    embeddingConfig = this.getEmbeddingConfigByConfigCode(fileEmbeddingConfigCode, null);

//                    RagProcessDTO ragProcessDTO = new RagProcessDTO();
//                    ragProcessDTO.setResourceId(resourceFileId);
//                    ragProcessDTO.setFileId(fileDTO.getFileId());
//                    ragProcessDTO.setEmbeddingConfigCode(embeddingConfig.getConfigCode());
//                    fileIdList.add(ragProcessDTO);

//                    if (Arrays.asList(toMarkdownWhiteList).contains(fileDTO.getFileType())) {
//                        ConvertMarkdownDTO markdownDTO = new ConvertMarkdownDTO();
//                        markdownDTO.setResourceId(resourceFileId);
//                        markdownDTO.setFileId(fileDTO.getFileId());
//                        mdList.add(markdownDTO);
//                    }
                }

                //记录操作日志
                String operateType = add ? OperateTypeEnum.UPLOAD.getName() : OperateTypeEnum.EDIT.getName();
                ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FILE;
                String operateContent = operateType + fileDTO.getName() + resourceTypeEnum.getName();
                boolean b = sysOptLogRepo.saveLog(operateContent, resourceFileId, Long.valueOf(fileDTO.getFolderId()), operateType, resourceTypeEnum.getCode());
                String msg = currentUser.getUserName() + operateContent;
                log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return RestResponse.fail(500, "文件上传失败");
        }

//        if (CollUtil.isNotEmpty(fileIdList)) {
//            resourceProcessService.execute(fileIdList);
//        }
//        if (CollUtil.isNotEmpty(mdList)) {
//            this.addPreview(mdList);
//        }
        return RestResponse.SUCCESS;
    }

    private void addPreview(List<ConvertMarkdownDTO> list) {
        ThreadPoolExecutor singleThreadExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1000));
        try {
            ConvertMarkdownRunnable runnable = new ConvertMarkdownRunnable(list, fileUploadRecordMapper, fileRepo, fileApi, fileBasePath);
            singleThreadExecutor.submit(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            singleThreadExecutor.shutdown();
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

    /**
     * 原文预览
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse getPreview(Integer id) {
        String content = null;
        BusResourceFileDTO fileDTO = fileRepo.getById(id);
        if (null != fileDTO && StringUtils.isNoneEmpty(fileDTO.getPreviewFileId())) {
            FileUploadRecordDTO preview = fileUploadRecordMapper.getByFileId(fileDTO.getPreviewFileId());
            try {
                content = new String(Files.readAllBytes(Path.of(fileBasePath + File.separator + preview.getPath() + preview.getFileName())));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        //记录操作日志
        String operateType = OperateTypeEnum.PREVIEW.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FILE;
        String operateContent = operateType + fileDTO.getName() + resourceTypeEnum.getName();
        boolean b = sysOptLogRepo.saveLog(operateContent, Long.valueOf(id), Long.valueOf(fileDTO.getFolderId()), operateType, resourceTypeEnum.getCode());
        String msg = ContextUtil.getUserName() + operateContent;
        log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");

        return RestResponse.success(content);
    }

    /**
     * 检查文件夹是否为空
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse checkEmptyFolder(Integer id) {
        List<BusResourceFileDTO> fileList = fileRepo.list(id);
        if (CollUtil.isNotEmpty(fileList)) {
            return RestResponse.success(false);
        }
        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, id);
        List<Integer> idList = Linq.select(childList, BusResourceManageListDTO::getId);
        fileList = fileRepo.listByFolderIdList(idList);
        return RestResponse.success(CollUtil.isEmpty(fileList));
    }

    /**
     * 删除文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse deleteFolder(Integer id) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        BusResourceFolderEntity folder = folderRepo.getById(id);
        if (null == folder) {
            return RestResponse.fail(ResourceErrorCode.DELETE_GUID_NOT_EXISTS);
        }
        //判断权限
        boolean auth = this.checkSystemAdminAuth(userId) || this.checkFolderAdminAuth(Math.toIntExact(id), userId)
                || this.checkUpFolderAdminAuth(Math.toIntExact(id), userId, true);
        if (!auth) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
        }

        //记录操作日志
        String operateType = OperateTypeEnum.DELETE.getName();
        ResourceTypeEnum resourceTypeEnum;
        boolean b;
        String msg;

        //删除文件夹下文件
        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, id);
        List<Integer> idList = Linq.select(childList, BusResourceManageListDTO::getId);
        List<BusResourceFileDTO> fileList = fileRepo.listByFolderIdList(idList);
        List<Integer> fileIdList = Linq.select(fileList, BusResourceFileDTO::getId);

        boolean success = folderRepo.deleteList(idList);
        if (CollUtil.isNotEmpty(fileList)) {
            List<RelUserResourceDTO> documentList = relUserResourceRepo.listByResourceFileIdList(fileIdList);
            for (BusResourceFileDTO fileDTO : fileList){
                RelUserResourceDTO document = Linq.first(documentList, x -> fileDTO.getId().equals(x.getResourceFileId()));
                if (null != document) {
                    resourceProcessService.delete(Long.valueOf(fileDTO.getId()), document.getDocumentId(), document.getFileId(), fileDTO.getEmbeddingConfigCode());
                }

                //记录操作日志
//                String operateType = OperateTypeEnum.DELETE.getName();
                resourceTypeEnum = ResourceTypeEnum.RESOURCE_FILE;
                // 文件 的上级目录用folderId
                String operateContent = operateType + fileDTO.getName() + resourceTypeEnum.getName();
                b = sysOptLogRepo.saveLog(operateContent, Long.valueOf(fileDTO.getId()), Long.valueOf(fileDTO.getFolderId()), operateType, resourceTypeEnum.getCode());
                msg = currentUser.getUserName() + operateContent;
                log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");
            }
            relUserResourceRepo.deleteByResourceFileIdList(fileIdList);
            fileRepo.deleteByFolderIdList(idList);
        }

        // 筛选出所有文件夹（包含自身）
        List<BusResourceManageListDTO> list = allList.stream().filter(x -> idList.contains(x.getId())).toList();
        for (BusResourceManageListDTO dto : list) {
            //记录操作日志
            resourceTypeEnum = ResourceTypeEnum.RESOURCE_FOLDER;
            String operateContent = operateType + dto.getName() + resourceTypeEnum.getName();
            if (Objects.nonNull(dto.getId()) && Objects.nonNull(dto.getParentId())) {
                // 文件夹 的上级目录用parentId
                b = sysOptLogRepo.saveLog(operateContent, Long.valueOf(dto.getId()), Long.valueOf(dto.getParentId()), operateType, resourceTypeEnum.getCode());
            } else {
                b = false;
            }
            msg = currentUser.getUserName() + operateContent;
            log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");
        }

        return success ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.DELETE_FAIL);
    }

    /**
     * 删除文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse deleteFile(Integer id) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        BusResourceFileDTO file = fileRepo.getById(id);
        if (null == file) {
            return RestResponse.fail(ResourceErrorCode.DELETE_GUID_NOT_EXISTS);
        }
        Boolean systemAdminAuth = this.checkSystemAdminAuth(userId);
        if (!systemAdminAuth){
            //判断权限
            BusResourceMemberDTO fileAuth = this.getFileAuth(file.getFolderId());
            if (!fileAuth.getIsAdmin() && !fileAuth.getUploadAuth()){
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
            }
        }

        fileRepo.delete(id);
    //todo   知识库删除

//        RelUserResourceDTO document = relUserResourceRepo.getOneByResourceFileId(id);
//        if (null != document) {
//            resourceProcessService.delete(Long.valueOf(id), document.getDocumentId(), document.getFileId(), file.getEmbeddingConfigCode());
//        }
        relUserResourceRepo.deleteByResourceFileId(id);

        //记录操作日志
        String operateType = OperateTypeEnum.DELETE.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FILE;
        String operateContent = operateType + file.getName() + resourceTypeEnum.getName();
        boolean b = sysOptLogRepo.saveLog(operateContent, Long.valueOf(id), Long.valueOf(file.getFolderId()), operateType, resourceTypeEnum.getCode());
        String msg = currentUser.getUserName() + operateContent;
        log.info("{}，操作结果：{}", msg, b ? "成功" : "失败");

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

    /**
     * 获取文件向量化配置
     */
    @Override
    public RestResponse getResourceEmbedInfo(String resourceGuid) {
        return RestResponse.success(embeddingRepo.getByResourceGuid(resourceGuid));
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
        BusResourceFileDTO fileDTO = fileRepo.getById(id);
        if (null == fileDTO) {
            return RestResponse.fail(ResourceErrorCode.DATA_NOT_EXISTS.getCode(), ResourceErrorCode.DATA_NOT_EXISTS.getMsg());
        }
        String fileId = fileDTO.getFileId();
        List<RelUserResourceEntity> relUserResourceDTOS = relUserResourceRepo.oneByField(fileId, id);
        if (null == relUserResourceDTOS || relUserResourceDTOS.isEmpty()) {
            return RestResponse.fail(ResourceErrorCode.DATA_NOT_EXISTS.getCode(), ResourceErrorCode.DATA_NOT_EXISTS.getMsg());
        }
        String indexingStatus = relUserResourceDTOS.get(0).getIndexingStatus();
        // 文档处理状态如果为空或者不为完成状态，则直接返回空列表
        if (StringUtils.isBlank(indexingStatus) || !indexingStatus.equals(IndexingStatusEnum.COMPLETED.getIndexingStatus())) {
            return RestResponse.success(null, 0);
        }
        String datasetsId = fileEmbeddingConfigMapper.getDataSetId(fileDTO.getEmbeddingConfigCode());
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

        //记录操作日志
        String optType = OperateTypeEnum.MOVE.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FILE;
        String operateContent = optType + operateNode.getName() + resourceTypeEnum.getName() + "到" + targetNode.getName() + "文件夹";
        // 移动之前文件夹的父节点
        BusResourceManageEntity parentNode = busResourceManageRepo.getById(operateNode.getParentId());
        // 移动之前文件夹的父节点的日志
        boolean flag1 = sysOptLogRepo.saveLog(operateContent, Long.valueOf(operateNode.getParentId()), Long.valueOf(parentNode.getParentId()), optType, resourceTypeEnum.getCode());
        // 移动之后文件夹的日志
        boolean flag2 = sysOptLogRepo.saveLog(operateContent, Long.valueOf(operateId), Long.valueOf(targetId), optType, resourceTypeEnum.getCode());
        String msg = currentUser.getUserName() + operateContent;
        log.info("{}，操作结果：{}", msg, flag1 && flag2 ? "成功" : "失败");

        return b ? RestResponse.success(busResourceManageRepo.list(ContextUtil.getUserId())) : RestResponse.fail(ResourceErrorCode.EDIT_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse updateParentId(Integer id, Integer originParentId, Integer newParentId) {
        busResourceManageRepo.updateParentId(id, newParentId);

        fileRepo.updateParentId(id, newParentId);

        BusResourceFileDTO repo = fileRepo.getById(id);
        // 移动之前文件的父节点
        BusResourceFolderEntity parentRepo = folderRepo.getById(originParentId);
        // 移动之后文件的父节点
        BusResourceFolderEntity newRepo = folderRepo.getById(newParentId);

        //记录操作日志
        String optType = OperateTypeEnum.MOVE.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FILE;
        String operateContent = "将" + repo.getName() + resourceTypeEnum.getName() + "从" + parentRepo.getName() + "文件夹" + optType + "到" + newRepo.getName() + "文件夹";
        // 移动之前文件的父节点的所有日志
        boolean b = sysOptLogRepo.saveLogByIdAndParentId(Long.valueOf(id), Long.valueOf(originParentId), Long.valueOf(parentRepo.getParentId()));
        // 移动日志
        boolean b1 = sysOptLogRepo.saveLog(operateContent, Long.valueOf(originParentId), Long.valueOf(parentRepo.getParentId()), optType, resourceTypeEnum.getCode());

        // 记录日志前更新文件日志的原父节点为新父节点（避免移动文件后丢失原操作日志）
        sysOptLogRepo.updateParentId(Long.valueOf(id), Long.valueOf(originParentId), Long.valueOf(newParentId));

        // 移动之后文件的日志
        boolean b2 = sysOptLogRepo.saveLog(operateContent, Long.valueOf(id), Long.valueOf(newParentId), optType, resourceTypeEnum.getCode());
        String msg = ContextUtil.getUserName() + operateContent;
        log.info("{}，操作结果：{}", msg, b && b1 && b2 ? "成功" : "失败");

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

    /**
     * 移动文件夹
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse moveFolder(BusResourceFolderDTO dto) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        if (null == dto) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
        BusResourceFolderEntity origin = folderRepo.getById(dto.getId());
        Long folderId = origin.getId();

        // 查父ID文件类型
        BusResourceFolderEntity parent = folderRepo.getById(dto.getParentId());
        if (null == parent) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上级文件夹未找到");
        }
        //判断权限
        //编辑文件夹权限：操作人需要是文件夹的管理员（本级或上级或系统管理员）
//        if (!parent.getCanAddSub()){
//            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "移动失败，所选上级文件夹下不允许创建下级文件夹");
//        }
//        Boolean systemAdminAuth = this.checkSystemAdminAuth(userId);
//        boolean openView = FolderViewTypeEnum.OPEN.getCode().equals(parent.getOpenView());
//        boolean parentOperateAuth = systemAdminAuth || this.checkFolderAdminAuth(Math.toIntExact(parent.getId()), userId)
//                || this.checkUpFolderAdminAuth(Math.toIntExact(parent.getId()), userId, true)
//                || (parent.getCanAddSub() && (openView || this.checkMemberAuth(Math.toIntExact(parent.getId()), userId)));
//        boolean currentOperateAuth = systemAdminAuth || this.checkFolderAdminAuth(Math.toIntExact(folderId), userId)
//                || this.checkUpFolderAdminAuth(Math.toIntExact(folderId), userId, true);
//        if (!parentOperateAuth || !currentOperateAuth) {
//            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无操作权限");
//        }
        dto.setParentGuid(parent.getGuid());

        //编辑
        if (origin.getParentId().equals(dto.getParentId())) {
            return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "请选择新的上级文件夹");
        }
        //检查循环
        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, Math.toIntExact(origin.getId()));
        List<Integer> idList = Linq.select(childList, BusResourceManageListDTO::getId);
        idList.add(Math.toIntExact(origin.getId()));
        if (idList.contains(dto.getParentId())) {
            return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
        }

        boolean a=folderRepo.updateParent(dto);

        // 原父节点
        BusResourceFolderEntity parentRepo = folderRepo.getById(origin.getParentId());

        //记录操作日志
        String optType = OperateTypeEnum.MOVE.getName();
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.RESOURCE_FOLDER;
        String operateContent = "将" + origin.getName() + resourceTypeEnum.getName() + "从" + parentRepo.getName() + "文件夹" + optType + "到" + parent.getName() + "文件夹";
        // 移动之前文件的父节点的所有日志
        boolean b = sysOptLogRepo.saveLogByIdAndParentId(folderId, Long.valueOf(origin.getParentId()), Long.valueOf(parentRepo.getParentId()));
        // 移动日志
        boolean b1 = sysOptLogRepo.saveLog(operateContent, Long.valueOf(origin.getParentId()), Long.valueOf(parentRepo.getParentId()), optType, resourceTypeEnum.getCode());

        // 记录日志前更新文件日志的原父节点为新父节点（避免移动文件后丢失原操作日志）
        sysOptLogRepo.updateParentId(folderId, Long.valueOf(origin.getParentId()), Long.valueOf(dto.getParentId()));

        // 移动之后文件的日志
        boolean b2 = sysOptLogRepo.saveLog(operateContent, folderId, Long.valueOf(dto.getParentId()), optType, resourceTypeEnum.getCode());
        String msg = ContextUtil.getUserName() + operateContent;
        log.info("{}，操作结果：{}", msg, b && b1 && b2 ? "成功" : "失败");
        return RestResponse.success(origin.getGuid());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse moveBachFile(SaveFileParam dto) {
        SystemUser currentUser = ContextUtil.currentUser();
        String userId = currentUser.getUserId();
        Boolean systemAdminAuth = this.checkSystemAdminAuth(userId);
        if (null == dto) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "参数为空");
        }
//检查父文件
        BusResourceFolderEntity parent = folderRepo.getById(dto.getFolderId());
        if (null == parent) {
            return RestResponse.fail(ResourceErrorCode.ADD_FAIL.getCode(), "上级文件夹未找到");
        }

        if (!systemAdminAuth){
            //todo 判断文件和文件夹夹权限
            //用户文件权限
            //获取用户角色
            Integer roleId=1;
            List<FileAuthEntity> fileList = fileAuthRepo.list(new LambdaQueryWrapper<FileAuthEntity>().eq(FileAuthEntity::getUserId, userId));
            List<FolderAuthEntity> folderList = folderAuthRepo.list(new LambdaQueryWrapper<FolderAuthEntity>().eq(FolderAuthEntity::getRoleId,roleId));
            List<Integer> fileListId = Linq.select(fileList, FileAuthEntity::getFileId);
            List<Integer> folderListId = Linq.select(folderList, FolderAuthEntity::getFolderId);
//        判断 select是否包含dto.getFileList()
            if (dto.getFileList()!=null){
                for (BusResourceFileDTO busResourceFileDTO : dto.getFileList()) {
                    if (!fileListId.contains(busResourceFileDTO.getId())) {
                        return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "包含无操作权限的文件");
                    }
                }
            }
            //todo 判断目标文件夹权限
            if (dto.getFolderList()!=null){
                for (BusResourceFolderDTO busResourceFolderDTO : dto.getFolderList()) {
                    if (!folderListId.contains(busResourceFolderDTO.getId())) {
                        return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "包含无操作权限文件夹");
                    }
                }
            }
            if (!folderListId.contains(dto.getFolderList())){
                return RestResponse.fail(ResourceErrorCode.NO_AUTH.getCode(), "无权限操作目标文件夹");
            }
        }
        //文件移动
        ArrayList<BusResourceFileEntity> updataFile = new ArrayList<>();
        for (BusResourceFileDTO busResourceFileDTO : dto.getFileList()) {
            BusResourceFileEntity busResourceFileEntity = new BusResourceFileEntity();
            busResourceFileEntity.setId(Long.valueOf(busResourceFileDTO.getId()));
            busResourceFileEntity.setFolderId(dto.getFolderId());
            busResourceFileDTO.setFolderId(dto.getFolderId());
            updataFile.add(busResourceFileEntity);
        }
        fileRepo.updateBatchById(updataFile);
        //文件夹移动
        List<BusResourceFolderDTO> folderList = dto.getFolderList();
        List<BusResourceManageListDTO> allList = Linq.select(folderRepo.listAll(true), folderMapping::dto2ListDto);
        for (BusResourceFolderDTO busResourceFolderDTO : folderList) {
            BusResourceFolderEntity origin = folderRepo.getById(busResourceFolderDTO.getId());
            busResourceFolderDTO.setParentGuid(parent.getGuid());
            //检查循环更新的父节点不能为原来节点和本身节点
            List<BusResourceManageListDTO> childList = TreeNodeServiceImpl.getChildrenList(allList, Math.toIntExact(origin.getId()));
            List<Integer> idList = Linq.select(childList, BusResourceManageListDTO::getId);
            idList.add(Math.toIntExact(origin.getId()));
            if (idList.contains(dto.getFolderId())) {
                return RestResponse.fail(DefaultErrorCode.UPDATE_ERROR.getCode(), "修改失败，上级文件夹选择不符合规范");
            }
            busResourceFolderDTO.setParentId(dto.getFolderId());
            folderRepo.updateParent(busResourceFolderDTO);
        }
        return RestResponse.success(parent.getGuid());
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
}