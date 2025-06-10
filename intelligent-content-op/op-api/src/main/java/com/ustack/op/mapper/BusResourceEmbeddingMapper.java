package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.op.entity.BusResourceEmbeddingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
* @author allm
* @description 针对表【bus_resource_embedding(知识库文件向量化配置关联表)】的数据库操作Mapper
* @createDate 2025-03-19 16:51:14
* @Entity com.ustack.op.entity.BusResourceEmbeddingEntity
*/
@Mapper
public interface BusResourceEmbeddingMapper extends BaseMapper<BusResourceEmbeddingEntity> {

}




