package com.ustack.chat.repo;

import com.ustack.chat.entity.FileEmbeddingConfigEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.resource.dto.FileEmbeddingConfigDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【file_embedding_config(文件向量化配置枚举表)】的数据库操作Service
* @createDate 2025-03-18 16:43:50
*/
public interface FileEmbeddingConfigRepo extends IService<FileEmbeddingConfigEntity> {
    /**
     * 列表
     */
    List<FileEmbeddingConfigDTO> listAll();

    /**
     * 根据 向量化配置编码 查询 向量化配置
     */
    FileEmbeddingConfigDTO getByCode(String configCode);
}
