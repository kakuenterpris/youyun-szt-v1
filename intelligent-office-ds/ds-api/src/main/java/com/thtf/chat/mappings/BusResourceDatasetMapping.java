package com.thtf.chat.mappings;

import com.thtf.chat.entity.BusResourceDatasetEntity;
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
