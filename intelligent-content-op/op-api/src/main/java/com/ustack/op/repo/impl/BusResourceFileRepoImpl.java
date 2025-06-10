package com.ustack.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.utils.Linq;
import com.ustack.op.entity.BusResourceFileEntity;
import com.ustack.op.mappings.BusResourceFileMapping;
import com.ustack.resource.dto.BusResourceFileDTO;
import com.ustack.op.repo.BusResourceFileRepo;
import com.ustack.op.mapper.BusResourceFileMapper;
import com.ustack.resource.dto.BusResourceManageListDTO;
import com.ustack.resource.dto.QueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_file(文件表)】的数据库操作Service实现
* @createDate 2025-04-23 11:43:03
*/
@Service
@RequiredArgsConstructor
public class BusResourceFileRepoImpl extends ServiceImpl<BusResourceFileMapper, BusResourceFileEntity>
    implements BusResourceFileRepo {
    private final BusResourceFileMapper fileMapper;
    private final BusResourceFileMapping fileMapping;

    @Override
    public Long add(BusResourceFileDTO dto) {
        BusResourceFileEntity entity = fileMapping.dto2Entity(dto);
        entity.setId(null);
        this.save(entity);
        return entity.getId();
    }

    @Override
    public boolean delete(Integer id) {
        return lambdaUpdate()
                .set(BusResourceFileEntity::getDeleted, true)
                .eq(BusResourceFileEntity::getId, id)
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(new BusResourceFileEntity());
    }

    @Override
    public boolean deleteByFolderId(Integer id) {
        return lambdaUpdate()
                .set(BusResourceFileEntity::getDeleted, true)
                .eq(BusResourceFileEntity::getFolderId, id)
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(new BusResourceFileEntity());
    }

    @Override
    public boolean deleteByFolderIdList(List<Integer> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        return lambdaUpdate()
                .set(BusResourceFileEntity::getDeleted, true)
                .in(BusResourceFileEntity::getFolderId, idList)
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(new BusResourceFileEntity());
    }

    @Override
    public boolean update(BusResourceFileDTO dto) {
        BusResourceFileEntity entity = fileMapping.dto2Entity(dto);

        return lambdaUpdate()
                .eq(BusResourceFileEntity::getId, dto.getId())
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public boolean updateJoinQuery(List<Integer> idList, Boolean joinQuery) {
        if (CollUtil.isEmpty(idList) || null == joinQuery) {
            return true;
        }
        return lambdaUpdate()
                .set(BusResourceFileEntity::getJoinQuery, joinQuery)
                .in(BusResourceFileEntity::getId, idList)
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(new BusResourceFileEntity());
    }

    @Override
    public List<BusResourceFileDTO> list(Integer folderId) {
        List<BusResourceFileEntity> list = lambdaQuery()
                .eq(BusResourceFileEntity::getFolderId, folderId)
                .eq(BusResourceFileEntity::getDeleted, false)
                .list();
        return Linq.select(list, fileMapping::entity2Dto);
    }

    @Override
    public BusResourceFileDTO getById(Integer id) {
        List<BusResourceFileEntity> list = lambdaQuery()
                .eq(BusResourceFileEntity::getId, id)
                .eq(BusResourceFileEntity::getDeleted, false)
                .list();
        return fileMapping.entity2Dto(Linq.first(list));
    }

    @Override
    public List<BusResourceManageListDTO> resourceListRight(List<Integer> folderIdList,List<Integer> fileIdList, Boolean viewFile,QueryDTO query, boolean notDelete) {
        return fileMapper.selectListForLeft(ContextUtil.getUserId(),fileIdList, query.getName(), query.getParentId() ,
                query.getFileYearList(), query.getEmbeddingConfigNameList(), folderIdList, viewFile,
                (query.getPageNum() - 1) * query.getPageSize(), query.getPageSize(),query.getTimeSort(),query.getNameSort(), notDelete);
    }

    @Override
    public Integer resourceListRightCount(List<Integer> folderIdList,List<Integer> fileIdList, Boolean viewFile, QueryDTO query, boolean notDelete) {
        return fileMapper.selectCountForLeft(fileIdList,query.getName(), query.getParentId(),
                query.getFileYearList(), query.getEmbeddingConfigNameList(), folderIdList, viewFile, notDelete);
    }

    @Override
    public List<BusResourceFileDTO> listByFolderIdList(List<Integer> idList) {
        if (CollUtil.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<BusResourceFileEntity> list = lambdaQuery()
                .in(BusResourceFileEntity::getFolderId, idList)
                .eq(BusResourceFileEntity::getDeleted, false)
                .list();
        return Linq.select(list, fileMapping::entity2Dto);
    }

    @Override
    public Integer maxSort() {
        List<BusResourceFileEntity> list = lambdaQuery()
                .orderByDesc(BusResourceFileEntity::getSort)
                .list();
        return list.isEmpty() ? 0 : list.get(0).getSort();
    }

    @Override
    public boolean updateParentId(Integer id, Integer newParentId) {
        return lambdaUpdate()
                .set(BusResourceFileEntity::getFolderId, newParentId)
                .eq(BusResourceFileEntity::getId, id)
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(new BusResourceFileEntity());
    }

    @Override
    public List<BusResourceManageListDTO> selectFileList(List<Integer> folderIdList,List<Integer> fileIdList, QueryDTO query, boolean notDelete) {
        return fileMapper.selectFileList(ContextUtil.getUserId(), query.getName(), query.getFileYearList(),
                query.getEmbeddingConfigNameList(), folderIdList,fileIdList,
                query.getLevel(),query.getEmbeddingStatus(),query.getIndexingStatus(),query.getStartTime(), query.getEndTime(),
                (query.getPageNum() - 1) * query.getPageSize(), query.getPageSize(),
                query.getTimeSort(), query.getNameSort(), notDelete);
    }

    @Override
    public Integer selectFileListCount(List<Integer> folderIdList,List<Integer> fileIdList, QueryDTO query, boolean notDelete) {
        return fileMapper.selectFileListCount(query.getName(), query.getFileYearList(),
                query.getEmbeddingConfigNameList(), folderIdList,fileIdList,
                query.getLevel(),query.getEmbeddingStatus(),query.getIndexingStatus(),query.getStartTime(), query.getEndTime(),notDelete);
    }

    @Override
    public boolean updatePreviewFileId(Long id, String previewFileId) {
        return lambdaUpdate()
                .set(BusResourceFileEntity::getPreviewFileId, previewFileId)
                .eq(BusResourceFileEntity::getId, id)
                .eq(BusResourceFileEntity::getDeleted, false)
                .update(new BusResourceFileEntity());
    }

    @Override
    public List<BusResourceFileDTO> listFileIdByIdList(List<Integer> idList) {
        List<BusResourceFileEntity> list = lambdaQuery()
                .in(BusResourceFileEntity::getId, idList)
                .eq(BusResourceFileEntity::getDeleted, false)
                .list();
        return Linq.select(list, fileMapping::entity2Dto);
    }
}




