package com.ustack.op.repo;

import com.ustack.op.entity.BusResourceFileEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.resource.dto.BusResourceFileDTO;
import com.ustack.resource.dto.BusResourceManageListDTO;
import com.ustack.resource.dto.QueryDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_file(文件表)】的数据库操作Service
* @createDate 2025-04-23 11:43:03
*/
public interface BusResourceFileRepo extends IService<BusResourceFileEntity> {
    /**
     * 添加
     */
    Long add(BusResourceFileDTO dto);

    /**
     * 逻辑删除
     */
    boolean delete(Integer id);

    /**
     * 逻辑删除
     */
    boolean deleteByFolderId(Integer id);

    /**
     * 逻辑删除
     */
    boolean deleteByFolderIdList(List<Integer> idList);

    /**
     * 更新
     */
    boolean update(BusResourceFileDTO dto);

    /**
     * 更新
     */
    boolean updateJoinQuery(List<Integer> idList, Boolean joinQuery);

    /**
     * 列表
     */
    List<BusResourceFileDTO> list(Integer folderId);

    /**
     * 根据 ID 查询
     */
    BusResourceFileDTO getById(Integer id);

    List<BusResourceManageListDTO> resourceListRight(List<Integer> folderIdList,List<Integer> fileIdList, Boolean viewFile,QueryDTO query, boolean notDelete);

    Integer resourceListRightCount(List<Integer> folderIdList,List<Integer> fildIdList, Boolean viewFile,QueryDTO query, boolean notDelete);

    List<BusResourceFileDTO> listByFolderIdList(List<Integer> idList);

    Integer maxSort();

    boolean updateParentId(Integer id, Integer newParentId);
    /**
     * 根据 文件名搜索
     */
    List<BusResourceManageListDTO> selectFileList(List<Integer> folderIdList,List<Integer> fileIdList, QueryDTO query, boolean notDelete);

    Integer selectFileListCount(List<Integer> folderIdList,List<Integer> fileIdList, QueryDTO query, boolean notDelete);

    boolean updatePreviewFileId(Long id, String previewFileId);

    List<BusResourceFileDTO> listFileIdByIdList(List<Integer> idList);
}
