package com.thtf.chat.mapper;

import com.thtf.chat.entity.BusResourceManageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thtf.resource.dto.BusResourceManageDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Mapper
 * @createDate 2025-02-18 17:57:12
 * @Entity com.thtf.chat.entity.BusResourceManageEntity
 */
public interface BusResourceManageMapper extends BaseMapper<BusResourceManageEntity> {

    List<BusResourceManageDTO> selectListByCondition(String userId, String name, Integer parentId, String category);

    List<BusResourceManageDTO> selectListByUser(String userId);
}




