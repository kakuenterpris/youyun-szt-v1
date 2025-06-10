package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.op.entity.BusResourceManageEntity;
import com.ustack.resource.dto.BusResourceManageDTO;

import java.util.List;

/**
 * @author Admin_14104
 * @description 针对表【bus_resource_manage】的数据库操作Mapper
 * @createDate 2025-02-18 17:57:12
 * @Entity com.ustack.op.entity.BusResourceManageEntity
 */
public interface BusResourceManageMapper extends BaseMapper<BusResourceManageEntity> {

    List<BusResourceManageDTO> selectListByCondition(String userId, String name, Integer parentId,
                                                     String category, List<Integer> fileYearList,
                                                     List<String> embeddingConfigNameList, List<String> authDepNumList,
                                                     Integer begin, Integer end);
    Integer selectCountByCondition(String userId, String name, Integer parentId,
                                                     String category, List<Integer> fileYearList,
                                                     List<String> embeddingConfigNameList, List<String> authDepNumList);

    List<BusResourceManageDTO> selectListByUser(String userId);
}




