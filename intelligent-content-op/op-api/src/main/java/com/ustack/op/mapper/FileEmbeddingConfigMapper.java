package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.op.entity.FileEmbeddingConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author allm
 * @description 针对表【file_embedding_config(文件向量化配置枚举表)】的数据库操作Mapper
 * @createDate 2025-03-18 16:43:50
 * @Entity com.ustack.op.entity.FileEmbeddingConfigEntity
 */
@Mapper
public interface FileEmbeddingConfigMapper extends BaseMapper<FileEmbeddingConfigEntity> {

    Map getDataSetData(@Param("embeddingConfigCode") String embeddingConfigCode);

    String getDataSetId(@Param("embeddingConfigCode") String embeddingConfigCode);
}




