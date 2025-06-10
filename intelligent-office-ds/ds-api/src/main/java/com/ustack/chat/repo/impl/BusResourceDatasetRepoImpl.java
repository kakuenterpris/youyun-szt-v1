package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.BusResourceDatasetEntity;
import com.ustack.chat.mappings.BusResourceDatasetMapping;
import com.ustack.chat.repo.BusResourceDatasetRepo;
import com.ustack.chat.mapper.BusResourceDatasetMapper;
import com.ustack.global.common.utils.Linq;
import com.ustack.resource.dto.BusResourceDatasetDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_dataset(人员或部门与知识库关联表)】的数据库操作Service实现
* @createDate 2025-03-27 17:43:25
*/
@Service
@RequiredArgsConstructor
public class BusResourceDatasetRepoImpl extends ServiceImpl<BusResourceDatasetMapper, BusResourceDatasetEntity>
    implements BusResourceDatasetRepo {
    private final BusResourceDatasetMapping datasetMapping;

    @Override
    public boolean add(BusResourceDatasetDTO dto) {
        BusResourceDatasetEntity entity = datasetMapping.dto2Entity(dto);
        entity.setId(null);
        return save(entity);
    }

    @Override
    public boolean add(String categoryCode, String code, String datasetsId) {
        BusResourceDatasetEntity entity = new BusResourceDatasetEntity();
        entity.setCategoryCode(categoryCode);
        entity.setCode(code);
        entity.setDatasetsId(datasetsId);
        return save(entity);
    }

    @Override
    public boolean delete(String code) {
        return lambdaUpdate()
                .set(BusResourceDatasetEntity::getDeleted, true)
                .eq(BusResourceDatasetEntity::getCode, code)
                .eq(BusResourceDatasetEntity::getDeleted, false)
                .update(new BusResourceDatasetEntity());
    }

    @Override
    public boolean update(BusResourceDatasetDTO dto) {
        BusResourceDatasetEntity entity = datasetMapping.dto2Entity(dto);

        return lambdaUpdate()
                .eq(BusResourceDatasetEntity::getId, dto.getId())
                .eq(BusResourceDatasetEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public List<BusResourceDatasetDTO> list(String categoryCode) {
        List<BusResourceDatasetEntity> list = lambdaQuery()
                .eq(BusResourceDatasetEntity::getCategoryCode, categoryCode)
                .eq(BusResourceDatasetEntity::getDeleted, false)
                .list();
        return Linq.select(list, datasetMapping::entity2Dto);
    }

    @Override
    public BusResourceDatasetDTO getByCode(String code) {
        List<BusResourceDatasetEntity> list = lambdaQuery()
                .eq(BusResourceDatasetEntity::getCode, code)
                .eq(BusResourceDatasetEntity::getDeleted, false)
                .list();
        return datasetMapping.entity2Dto(Linq.first(list));
    }
}




