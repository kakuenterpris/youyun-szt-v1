package com.ustack.op.mappings;

import com.ustack.op.entity.FileEmbeddingConfigEntity;
import com.ustack.resource.dto.FileEmbeddingConfigDTO;
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
