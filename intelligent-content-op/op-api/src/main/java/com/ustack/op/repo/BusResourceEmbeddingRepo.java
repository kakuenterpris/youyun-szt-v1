package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.op.entity.BusResourceEmbeddingEntity;
import com.ustack.resource.dto.BusResourceEmbeddingDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_embedding(知识库文件向量化配置关联表)】的数据库操作Service
* @createDate 2025-03-19 16:51:14
*/
public interface BusResourceEmbeddingRepo extends IService<BusResourceEmbeddingEntity> {
    /**
     * 添加
     */
    boolean add(BusResourceEmbeddingDTO dto);

    /**
     * 逻辑删除
     */
    boolean delete(Integer resourceId);

    /**
     * 逻辑删除
     */
    boolean delete(String resourceGuid);

    /**
     * 更新
     */
    boolean update(BusResourceEmbeddingDTO dto);

    /**
     * 列表
     */
    List<BusResourceEmbeddingDTO> list(Integer resourceId);

    /**
     * 根据 ID 查询
     */
    BusResourceEmbeddingDTO getByResourceId(Integer resourceId);

    /**
     * 根据 ID 查询
     */
    BusResourceEmbeddingDTO getByResourceGuid(String resourceGuid);
}
