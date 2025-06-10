package com.ustack.op.mappings;

import com.ustack.op.entity.BusResourceEmbeddingEntity;
import com.ustack.resource.dto.BusResourceEmbeddingDTO;
import com.ustack.resource.dto.FileEmbeddingConfigDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusResourceEmbeddingMapping {

    BusResourceEmbeddingEntity dto2Entity(BusResourceEmbeddingDTO param);

    BusResourceEmbeddingDTO entity2Dto(BusResourceEmbeddingEntity param);

    BusResourceEmbeddingDTO configDto2Dto(FileEmbeddingConfigDTO param);


}
