package com.thtf.chat.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.BusResourceManageEntity;
import com.thtf.chat.entity.PhrasesEntity;
import com.thtf.chat.mapper.BusResourceManageMapper;
import com.thtf.chat.mapper.PhrasesMapper;
import com.thtf.chat.mappings.BusResourceManageMapping;
import com.thtf.chat.mappings.PhrasesMapping;
import com.thtf.chat.repo.BusResourceManageRepo;
import com.thtf.chat.repo.PhrasesRepo;
import com.thtf.chat.repo.RelUserResourceRepo;
import com.thtf.global.common.utils.Linq;
import com.thtf.phrases.dto.PhrasesDTO;
import com.thtf.resource.dto.BusResourceManageDTO;
import com.thtf.resource.enums.ResourceCategoryEnum;
import com.thtf.resource.enums.ResourceTypeEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.management.Query;
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
                .eq(BusResourceManageEntity::getResourceType, ResourceTypeEnum.RESOURCE_FLODER.getCode())
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
    public List<BusResourceManageDTO> resourceListRight(String userId, String name, Integer parentId, String category) {
//        List<BusResourceManageEntity> list = new ArrayList<>();
//        if (StringUtils.isNotBlank(name)) {
//            list = lambdaQuery()
//                    .eq(BusResourceManageEntity::getCreateUserId, userId)
//                    .eq(BusResourceManageEntity::getParentId, parentId)
//                    .like(BusResourceManageEntity::getName, name)
//                    .list();
//        } else {
//            list = lambdaQuery()
//                    .eq(BusResourceManageEntity::getCreateUserId, userId)
//                    .eq(BusResourceManageEntity::getParentId, parentId)
//                    .list();
//        }
//        return Linq.select(list, busResourceManageMapping::entity2Dto);
        return busResourceManageMapper.selectListByCondition(userId, name, parentId ,category);
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
    public BusResourceManageEntity resourceTypeById(Integer id) {
        return lambdaQuery()
                .eq(BusResourceManageEntity::getId, id).one();
    }

    @Override
    public BusResourceManageEntity getById(Long id) {
        return lambdaQuery().eq(BusResourceManageEntity::getId, id).one();
    }

}




