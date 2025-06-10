package com.ustack.chat.repo;

import com.ustack.chat.entity.BusResourceDatasetEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.resource.dto.BusResourceDatasetDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_dataset(人员或部门与知识库关联表)】的数据库操作Service
* @createDate 2025-03-27 17:43:25
*/
public interface BusResourceDatasetRepo extends IService<BusResourceDatasetEntity> {
    /**
     * 添加
     */
    boolean add(BusResourceDatasetDTO dto);
    /**
     * 添加
     */
    boolean add(String categoryCode, String code, String datasetsId);

    /**
     * 逻辑删除
     */
    boolean delete(String code);

    /**
     * 更新
     */
    boolean update(BusResourceDatasetDTO dto);

    /**
     * 根据类别编码查询列表
     */
    List<BusResourceDatasetDTO> list(String categoryCode);

    /**
     * 根据 user_id或dep_num 查询
     */
    BusResourceDatasetDTO getByCode(String code);

}
