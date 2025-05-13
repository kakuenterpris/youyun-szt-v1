package com.thtf.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.op.entity.BusResourceFavoriteEntity;
import com.thtf.op.mappings.BusResourceFavoriteMapping;
import com.thtf.op.repo.BusResourceFavoriteRepo;
import com.thtf.op.mapper.BusResourceFavoriteMapper;
import com.thtf.resource.dto.BusResourceFavoriteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author Exile
* @description 针对表【bus_resource_favorite(收藏表)】的数据库操作Service实现
* @createDate 2025-05-08 15:56:56
*/
@Service
@RequiredArgsConstructor
public class BusResourceFavoriteRepoImpl extends ServiceImpl<BusResourceFavoriteMapper, BusResourceFavoriteEntity>
    implements BusResourceFavoriteRepo {

    private final BusResourceFavoriteMapping busResourceFavoriteMapping;

    @Override
    public Long saveFavorite(BusResourceFavoriteDTO dto) {
        BusResourceFavoriteEntity entity = busResourceFavoriteMapping.dto2Entity(dto);
        entity.setId(null);
        this.save(entity);
        return entity.getId();
    }
}




