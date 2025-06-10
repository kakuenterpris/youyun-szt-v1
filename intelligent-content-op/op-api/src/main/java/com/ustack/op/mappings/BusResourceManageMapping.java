package com.ustack.op.mappings;

import com.ustack.op.entity.BusResourceManageEntity;
import com.ustack.resource.dto.BusResourceManageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusResourceManageMapping {

    BusResourceManageEntity dto2Entity(BusResourceManageDTO param);

    BusResourceManageDTO entity2Dto(BusResourceManageEntity param);


}
