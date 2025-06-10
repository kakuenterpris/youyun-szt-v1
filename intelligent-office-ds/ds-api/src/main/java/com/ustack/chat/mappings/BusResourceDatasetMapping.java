package com.ustack.chat.mappings;

import com.ustack.chat.entity.BusResourceDatasetEntity;
import com.ustack.resource.dto.BusResourceDatasetDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface BusResourceDatasetMapping {

    BusResourceDatasetEntity dto2Entity(BusResourceDatasetDTO param);

    BusResourceDatasetDTO entity2Dto(BusResourceDatasetEntity param);


}
