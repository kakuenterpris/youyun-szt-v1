package com.ustack.chat.mapper;

import com.ustack.chat.entity.FileEmbeddingConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author allm
* @description 针对表【file_embedding_config(文件向量化配置枚举表)】的数据库操作Mapper
* @createDate 2025-03-18 16:43:50
* @Entity com.ustack.chat.entity.FileEmbeddingConfigEntity
*/
@Mapper
public interface FileEmbeddingConfigMapper extends BaseMapper<FileEmbeddingConfigEntity> {

}




