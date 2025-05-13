package com.thtf.op.repo;

import com.thtf.op.entity.BusResourceFavoriteEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.resource.dto.BusResourceFavoriteDTO;

/**
* @author Exile
* @description 针对表【bus_resource_favorite(收藏表)】的数据库操作Service
* @createDate 2025-05-08 15:56:56
*/
public interface BusResourceFavoriteRepo extends IService<BusResourceFavoriteEntity> {

    Long saveFavorite(BusResourceFavoriteDTO dto);
}
