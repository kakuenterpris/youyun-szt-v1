package com.thtf.chat.mappings;

import com.thtf.chat.entity.BusResourceManageEntity;
import com.thtf.resource.dto.BusResourceManageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusResourceManageMapping {

    BusResourceManageEntity dto2Entity(BusResourceManageDTO param);

    BusResourceManageDTO entity2Dto(BusResourceManageEntity param);


}
