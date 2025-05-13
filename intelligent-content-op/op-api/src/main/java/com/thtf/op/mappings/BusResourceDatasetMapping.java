package com.thtf.op.mappings;

import com.thtf.op.entity.BusResourceDatasetEntity;
import com.thtf.resource.dto.BusResourceDatasetDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusResourceDatasetMapping {

    BusResourceDatasetEntity dto2Entity(BusResourceDatasetDTO param);

    BusResourceDatasetDTO entity2Dto(BusResourceDatasetEntity param);


}
