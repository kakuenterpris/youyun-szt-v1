package com.ustack.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.utils.Linq;
import com.ustack.op.entity.RelUserResourceEntity;
import com.ustack.op.mapper.RelUserResourceMapper;
import com.ustack.op.mappings.RelUserResourceMapping;
import com.ustack.op.repo.RelUserResourceRepo;
import com.ustack.resource.dto.RelUserResourceDTO;
import com.ustack.resource.enums.IndexingStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Admin_14104
 * @description 针对表【rel_user_resource】的数据库操作Service实现
 * @createDate 2025-02-19 12:18:15
 */
@Service
@RequiredArgsConstructor
public class RelUserResourceRepoImpl extends ServiceImpl<RelUserResourceMapper, RelUserResourceEntity>
        implements RelUserResourceRepo {

    private final RelUserResourceMapping relUserResourceMapping;
    private final RelUserResourceMapper relUserResourceMapper;

    @Override
    public boolean add(RelUserResourceDTO relUserResourceDTO) {
        RelUserResourceEntity entity = relUserResourceMapping.dto2Entity(relUserResourceDTO);
        entity.setId(null);
        return save(entity);
    }

    @Override
    public List<RelUserResourceEntity> listByUserId(String userId) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        List list = relUserResourceMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<RelUserResourceEntity> list(String userId) {
        return lambdaQuery().eq(RelUserResourceEntity::getUserId, userId).list();
    }

    @Override
    public List<RelUserResourceEntity> oneByField(String fileId) {
        List<RelUserResourceEntity> list = lambdaQuery().eq(RelUserResourceEntity::getFileId, fileId).list();
        return list;
    }

    @Override
    public RelUserResourceDTO getOneByFileId(String fileId) {
        RelUserResourceEntity one = lambdaQuery().eq(RelUserResourceEntity::getFileId, fileId).one();
        return null == one ? null : relUserResourceMapping.entity2Dto(one);
    }

    @Override
    public RelUserResourceDTO getOneByResourceId(Long resourceId) {
        RelUserResourceEntity one = lambdaQuery().eq(RelUserResourceEntity::getResourceId, resourceId).one();
        return null == one ? null : relUserResourceMapping.entity2Dto(one);
    }

    @Override
    public RelUserResourceDTO getOneByResourceFileId(Integer resourceFileId) {
        RelUserResourceEntity one = lambdaQuery().eq(RelUserResourceEntity::getResourceFileId, resourceFileId).one();
        return null == one ? null : relUserResourceMapping.entity2Dto(one);
    }

    @Override
    public List<RelUserResourceDTO> listByResourceFileIdList(List<Integer> resourceFileIdList) {
        List<RelUserResourceEntity> list = lambdaQuery().in(RelUserResourceEntity::getResourceFileId, resourceFileIdList).list();
        return Linq.select(list, relUserResourceMapping::entity2Dto);
    }

    @Override
    public boolean deleteByResourceFileId(Integer resourceFileId) {
        return lambdaUpdate()
                .set(RelUserResourceEntity::getDeleted, true)
                .eq(RelUserResourceEntity::getResourceFileId, resourceFileId)
                .eq(RelUserResourceEntity::getDeleted, false)
                .update(new RelUserResourceEntity());
    }

    @Override
    public boolean deleteByResourceFileIdList(List<Integer> resourceFileIdList) {
        if (CollUtil.isEmpty(resourceFileIdList)){
            return true;
        }
        return lambdaUpdate()
                .set(RelUserResourceEntity::getDeleted, true)
                .in(RelUserResourceEntity::getResourceFileId, resourceFileIdList)
                .eq(RelUserResourceEntity::getDeleted, false)
                .update(new RelUserResourceEntity());
    }

    @Override
    public boolean deleteByFileId(String fileId) {
        return lambdaUpdate()
                .set(RelUserResourceEntity::getDeleted, true)
                .eq(RelUserResourceEntity::getFileId, fileId)
                .eq(RelUserResourceEntity::getDeleted, false)
                .update(new RelUserResourceEntity());
    }

    @Override
    public boolean updateInfoFromDify(String fileId, String documentId, String batch, IndexingStatusEnum indexingStatusEnum) {
        return lambdaUpdate()
                .set(RelUserResourceEntity::getDocumentId, documentId)
                .set(RelUserResourceEntity::getBatch, batch)
                .set(Objects.nonNull(indexingStatusEnum), RelUserResourceEntity::getIndexingStatus, indexingStatusEnum.getIndexingStatus())
                .set(Objects.nonNull(indexingStatusEnum), RelUserResourceEntity::getIndexingStatusName, indexingStatusEnum.getIndexingStatusName())
                .eq(RelUserResourceEntity::getFileId, fileId)
                .eq(RelUserResourceEntity::getDeleted, false)
                .update(new RelUserResourceEntity());
    }

    @Override
    public boolean updateIndexStatus(String documentId, String indexingStatus, String indexingStatusName) {
        return lambdaUpdate()
                .set(RelUserResourceEntity::getIndexingStatus, indexingStatus)
                .set(RelUserResourceEntity::getIndexingStatusName, indexingStatusName)
                .eq(RelUserResourceEntity::getDocumentId, documentId)
                .eq(RelUserResourceEntity::getDeleted, false)
                .update(new RelUserResourceEntity());
    }

    @Override
    public List<RelUserResourceDTO> getIndexingList() {
        return relUserResourceMapper.getIndexingList();
    }

    @Override
    public List<RelUserResourceEntity> oneByField(String fileId, Integer resourceFileId) {
        List<RelUserResourceEntity> list = lambdaQuery().eq(RelUserResourceEntity::getFileId, fileId)
                .eq(RelUserResourceEntity::getResourceFileId, resourceFileId).list();
        return list;
    }

    @Override
    public List<RelUserResourceEntity> getListByResourceIdList(List<Long> idList, String userId) {
        return idList != null && !idList.isEmpty() ? lambdaQuery().in(RelUserResourceEntity::getResourceId, idList)
                .eq(RelUserResourceEntity::getUserId, userId).list() : new ArrayList<>();
    }
}




