package com.ustack.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.op.entity.BusResourceManageEntity;
import com.ustack.op.mapper.BusResourceManageMapper;
import com.ustack.op.mappings.BusResourceManageMapping;
import com.ustack.op.repo.BusResourceManageRepo;
import com.ustack.global.common.utils.Linq;
import com.ustack.resource.dto.BusResourceManageDTO;
import com.ustack.resource.dto.QueryDTO;
import com.ustack.resource.enums.ResourceCategoryEnum;
import com.ustack.resource.enums.ResourceTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusResourceManageRepoImpl extends ServiceImpl<BusResourceManageMapper, BusResourceManageEntity>
        implements BusResourceManageRepo {

    private final BusResourceManageMapping busResourceManageMapping;
    private final BusResourceManageMapper busResourceManageMapper;

    @Override
    public Long add(BusResourceManageDTO dto) {
        BusResourceManageEntity entity = busResourceManageMapping.dto2Entity(dto);
        entity.setFileYear(LocalDate.now().getYear());
        this.save(entity);
        return entity.getId();
    }

    @Override
    public boolean delete(Integer id) {
        return lambdaUpdate()
                .set(BusResourceManageEntity::getDeleted, true)
                .eq(BusResourceManageEntity::getId, id)
                .or()
                .eq(BusResourceManageEntity::getParentId, id)
                .eq(BusResourceManageEntity::getDeleted, false)
                .update(new BusResourceManageEntity());
    }

    @Override
    public boolean update(BusResourceManageDTO dto) {
        BusResourceManageEntity entity = busResourceManageMapping.dto2Entity(dto);
        return lambdaUpdate()
                .eq(BusResourceManageEntity::getId, dto.getId())
                .eq(BusResourceManageEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public boolean updateParentId(Integer id, Integer parentId) {
        return lambdaUpdate()
                .set(BusResourceManageEntity::getParentId, parentId)
                .eq(BusResourceManageEntity::getId, id)
                .eq(BusResourceManageEntity::getDeleted, false)
                .update(new BusResourceManageEntity());
    }

    @Override
    public boolean updateSort(Integer id, Integer sort) {
        return lambdaUpdate()
                .set(BusResourceManageEntity::getSort, sort)
                .eq(BusResourceManageEntity::getId, id)
                .eq(BusResourceManageEntity::getDeleted, false)
                .update(new BusResourceManageEntity());
    }

    @Override
    public boolean updatePreviewFileId(Long id, String previewFileId) {
        return lambdaUpdate()
                .set(BusResourceManageEntity::getPreviewFileId, previewFileId)
                .eq(BusResourceManageEntity::getId, id)
                .eq(BusResourceManageEntity::getDeleted, false)
                .update(new BusResourceManageEntity());
    }

    @Override
    public boolean updateJoinQuery(List<Integer> idList, Boolean joinQuery) {
        if (CollUtil.isEmpty(idList) || null == joinQuery) {
            return true;
        }
        return lambdaUpdate()
                .set(BusResourceManageEntity::getJoinQuery, joinQuery)
                .in(BusResourceManageEntity::getId, idList)
                .eq(BusResourceManageEntity::getDeleted, false)
                .update(new BusResourceManageEntity());
    }

    @Override
    public List<BusResourceManageDTO> listByIdList(List<Integer> idList, Boolean joinQuery) {
        if (CollUtil.isEmpty(idList)) {
            return new ArrayList<>();
        }
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(null != joinQuery, BusResourceManageEntity::getJoinQuery, joinQuery)
                .in(BusResourceManageEntity::getId, idList)
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> listFixed() {
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(BusResourceManageEntity::getFixed, true)
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> listResourceFloder() {
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(BusResourceManageEntity::getResourceType, ResourceTypeEnum.RESOURCE_FOLDER.getCode())
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> listUnit(Integer resourceType) {
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(null != resourceType, BusResourceManageEntity::getResourceType, resourceType)
                .eq(BusResourceManageEntity::getFixed, false)
                .eq(BusResourceManageEntity::getCategory, ResourceCategoryEnum.UNIT.getName())
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> listDep(Integer resourceType, List<String> depNumList) {
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(null != resourceType, BusResourceManageEntity::getResourceType, resourceType)
                .in(CollUtil.isNotEmpty(depNumList), BusResourceManageEntity::getDepNum, depNumList)
                .eq(BusResourceManageEntity::getFixed, false)
                .eq(BusResourceManageEntity::getCategory, ResourceCategoryEnum.DEP.getName())
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> list(String userId) {
//        List<BusResourceManageEntity> list = lambdaQuery()
//                .eq(BusResourceManageEntity::getCreateUserId, userId)
//                .eq(BusResourceManageEntity::getResourceType, ResourceTypeEnum.RESOURCE_FLODER.getCode())
//                .eq(BusResourceManageEntity::getDeleted, false)
//                .list();
//        return Linq.select(list, busResourceManageMapping::entity2Dto);
        return busResourceManageMapper.selectListByUser(userId);
    }

    @Override
    public int maxSort() {
        List<BusResourceManageEntity> list = lambdaQuery()
                .orderByDesc(BusResourceManageEntity::getSort)
                .list();
        return list.isEmpty() ? 0 : list.get(0).getSort();
    }

    @Override
    public List<BusResourceManageDTO> resourceListRight(String userId, String category, QueryDTO query) {
        return busResourceManageMapper.selectListByCondition(userId, query.getName(), query.getParentId() ,category,
                query.getFileYearList(), query.getEmbeddingConfigNameList(), query.getAuthDepNumList(),
                (query.getPageNum() - 1) * query.getPageSize(), query.getPageSize());
    }

    @Override
    public Integer resourceListRightCount(String userId, String category, QueryDTO query) {
        return busResourceManageMapper.selectCountByCondition(userId, query.getName(), query.getParentId() ,category,
                query.getFileYearList(), query.getEmbeddingConfigNameList(), query.getAuthDepNumList());
    }

    @Override
    public List<BusResourceManageDTO> listByParentId(Integer parentId) {
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(BusResourceManageEntity::getParentId, parentId)
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return CollUtil.isEmpty(list) ? new ArrayList<>() : Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> listByParentIdAndName(Integer parentId, List<String> nameList) {
        if (null == parentId || CollUtil.isEmpty(nameList)) {
            return new ArrayList<>();
        }
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(BusResourceManageEntity::getParentId, parentId)
                .in(BusResourceManageEntity::getName, nameList)
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return CollUtil.isEmpty(list) ? new ArrayList<>() : Linq.select(list, busResourceManageMapping::entity2Dto);
    }

    @Override
    public List<BusResourceManageDTO> listByParentIdAndResourceType(Long parentId, Integer resourceType) {
        List<BusResourceManageEntity> list = lambdaQuery()
                .eq(BusResourceManageEntity::getParentId, parentId)
                .eq(BusResourceManageEntity::getResourceType, resourceType)
                .eq(BusResourceManageEntity::getDeleted, false)
                .list();
        return CollUtil.isEmpty(list) ? new ArrayList<>() : Linq.select(list, busResourceManageMapping::entity2Dto);
    }


    @Override
    public BusResourceManageEntity resourceTypeById(Integer id) {
        return lambdaQuery()
                .eq(BusResourceManageEntity::getId, id).one();
    }

    @Override
    public BusResourceManageEntity getById(Long id) {
        return lambdaQuery().eq(BusResourceManageEntity::getId, id).one();
    }

}




