package com.ustack.op.service;

import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.resource.dto.*;
import com.ustack.resource.param.SaveFileParam;

import java.io.IOException;
import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Service
 * @createDate 2025-02-18 17:57:12
 */
public interface KmService {

    /**
     * 左侧树
     */
    RestResponse resourceTreeListLeft();

    /**
     * 左侧文件夹列表
     */
    List<BusResourceManageListDTO> getResourceListLeft(String requestType, Integer folderType, SystemUser systemUser);

    /**
     * 右侧树
     */
    RestResponse resourceListRight(QueryDTO query);

    /**
     * 右侧文件、文件夹列表
     * @param notDelete 是否删除（为true时，查询未删除的文件/文件夹，为false时，查询所有文件/文件夹，仅当操作日志调该方法时传false）
     */
    RestResponse resourceListRight(QueryDTO query, boolean notDelete);

    /**
     * 更新是否参与问答
     */
    RestResponse updateJoinQuery(QueryDTO query);

    /**
     * 获取文件操作权限
     */
    RestResponse getAuth(Integer id);

    List<BusResourceMemberDTO> getFolderAuthList(List<Integer> idList);

    /**
     * 获取文件夹成员
     */
    RestResponse getMember(Integer id);

    /**
     * 保存文件夹
     */
    RestResponse saveFolder(BusResourceFolderDTO dto);

    /**
     * 保存文件
     */
    RestResponse saveFile(SaveFileParam dto);

    /**
     * 原文预览
     */
    RestResponse getPreview(Integer id) throws IOException;

    /**
     * 检查文件夹是否为空
     */
    RestResponse checkEmptyFolder(Integer id);

    /**
     * 删除文件夹
     */
    RestResponse deleteFolder(Integer id);

    /**
     * 删除文件
     */
    RestResponse deleteFile(Integer id);

    RestResponse listEmbedConfig();

    RestResponse getEmbedConfig(String configCode);

    /**
     * 获取文件向量化配置
     */
    RestResponse getResourceEmbedInfo(String resourceGuid);

    /**
     * 查询文档切片内容
     */
    RestResponse resourceSegmentNewList(Integer id);

    /**
     *
     */
    RestResponse moveNode(Integer operateId, Integer targetId, String operateType);

    /**
     *
     */
    RestResponse updateParentId(Integer id, Integer originParentId, Integer newParentId);

    /**
     *
     */
    RestResponse updateSort(List<BusResourceManageDTO> list);

    /**
     *
     */
    RestResponse syncDifyDocument(String datasetId, Integer parentId);

    RestResponse moveFolder(BusResourceFolderDTO dto);

    RestResponse moveBachFile(SaveFileParam dto);
}
