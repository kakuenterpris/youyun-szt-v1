package com.thtf.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.op.entity.FileEmbeddingConfigEntity;
import com.thtf.op.mapper.FileEmbeddingConfigMapper;
import com.thtf.op.mappings.FileEmbeddingConfigMapping;
import com.thtf.op.repo.FileEmbeddingConfigRepo;
import com.thtf.global.common.utils.Linq;
import com.thtf.resource.dto.FileEmbeddingConfigDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【file_embedding_config(文件向量化配置枚举表)】的数据库操作Service实现
* @createDate 2025-03-18 16:43:50
*/
@Service
@RequiredArgsConstructor
public class FileEmbeddingConfigRepoImpl extends ServiceImpl<FileEmbeddingConfigMapper, FileEmbeddingConfigEntity>
    implements FileEmbeddingConfigRepo {
    private final FileEmbeddingConfigMapping configMapping;

    @Override
    public List<FileEmbeddingConfigDTO> listAll() {
        List<FileEmbeddingConfigEntity> list = lambdaQuery()
                .eq(FileEmbeddingConfigEntity::getDeleted, false)
                .orderBy(true, false, FileEmbeddingConfigEntity::getOrderBy)
                .list();
        return Linq.select(list, configMapping::entity2Dto);
    }

    @Override
    public FileEmbeddingConfigDTO getByCode(String configCode) {
        List<FileEmbeddingConfigEntity> list = lambdaQuery()
                .eq(FileEmbeddingConfigEntity::getConfigCode, configCode)
                .eq(FileEmbeddingConfigEntity::getDeleted, false)
                .list();
        return configMapping.entity2Dto(Linq.first(list));
    }
}




