package com.ustack.chat.mappings;

import com.ustack.chat.entity.FileEmbeddingConfigEntity;
import com.ustack.resource.dto.FileEmbeddingConfigDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface FileEmbeddingConfigMapping {

    FileEmbeddingConfigEntity dto2Entity(FileEmbeddingConfigDTO param);

    FileEmbeddingConfigDTO entity2Dto(FileEmbeddingConfigEntity param);


}
