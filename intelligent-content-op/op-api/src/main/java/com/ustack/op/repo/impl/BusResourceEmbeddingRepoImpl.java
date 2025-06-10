package com.ustack.op.repo.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.op.entity.BusResourceEmbeddingEntity;
import com.ustack.op.mapper.BusResourceEmbeddingMapper;
import com.ustack.op.mappings.BusResourceEmbeddingMapping;
import com.ustack.op.repo.BusResourceEmbeddingRepo;
import com.ustack.global.common.utils.Linq;
import com.ustack.resource.dto.BusResourceEmbeddingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_embedding(知识库文件向量化配置关联表)】的数据库操作Service实现
* @createDate 2025-03-19 16:51:14
*/
@Service
@RequiredArgsConstructor
public class BusResourceEmbeddingRepoImpl extends ServiceImpl<BusResourceEmbeddingMapper, BusResourceEmbeddingEntity>
    implements BusResourceEmbeddingRepo {
    private final BusResourceEmbeddingMapping embeddingMapping;

    @Override
    public boolean add(BusResourceEmbeddingDTO dto) {
        BusResourceEmbeddingEntity entity = embeddingMapping.dto2Entity(dto);
        entity.setId(null);
        entity.setGuid(IdUtil.simpleUUID());
        return save(entity);
    }

    @Override
    public boolean delete(Integer resourceId) {
        return lambdaUpdate()
                .set(BusResourceEmbeddingEntity::getDeleted, true)
                .eq(BusResourceEmbeddingEntity::getResourceId, resourceId)
                .eq(BusResourceEmbeddingEntity::getDeleted, false)
                .update(new BusResourceEmbeddingEntity());
    }

    @Override
    public boolean delete(String resourceGuid) {
        return lambdaUpdate()
                .set(BusResourceEmbeddingEntity::getDeleted, true)
                .eq(BusResourceEmbeddingEntity::getResourceGuid, resourceGuid)
                .eq(BusResourceEmbeddingEntity::getDeleted, false)
                .update(new BusResourceEmbeddingEntity());
    }

    @Override
    public boolean update(BusResourceEmbeddingDTO dto) {
        BusResourceEmbeddingEntity entity = embeddingMapping.dto2Entity(dto);

        return lambdaUpdate()
                .eq(BusResourceEmbeddingEntity::getId, dto.getId())
                .eq(BusResourceEmbeddingEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public List<BusResourceEmbeddingDTO> list(Integer resourceId) {
        List<BusResourceEmbeddingEntity> list = lambdaQuery()
                .eq(BusResourceEmbeddingEntity::getResourceId, resourceId)
                .eq(BusResourceEmbeddingEntity::getDeleted, false)
                .list();
        return Linq.select(list, embeddingMapping::entity2Dto);
    }

    @Override
    public BusResourceEmbeddingDTO getByResourceId(Integer resourceId) {
        List<BusResourceEmbeddingEntity> list = lambdaQuery()
                .eq(BusResourceEmbeddingEntity::getResourceId, resourceId)
                .eq(BusResourceEmbeddingEntity::getDeleted, false)
                .list();
        return embeddingMapping.entity2Dto(Linq.first(list));
    }

    @Override
    public BusResourceEmbeddingDTO getByResourceGuid(String resourceGuid) {
        List<BusResourceEmbeddingEntity> list = lambdaQuery()
                .eq(BusResourceEmbeddingEntity::getResourceGuid, resourceGuid)
                .eq(BusResourceEmbeddingEntity::getDeleted, false)
                .list();
        return embeddingMapping.entity2Dto(Linq.first(list));
    }
}




