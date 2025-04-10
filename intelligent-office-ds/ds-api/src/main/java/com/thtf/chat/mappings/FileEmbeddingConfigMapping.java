package com.thtf.chat.mappings;

import com.thtf.chat.entity.FileEmbeddingConfigEntity;
import com.thtf.resource.dto.FileEmbeddingConfigDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface FileEmbeddingConfigMapping {

    FileEmbeddingConfigEntity dto2Entity(FileEmbeddingConfigDTO param);

    FileEmbeddingConfigDTO entity2Dto(FileEmbeddingConfigEntity param);


}
