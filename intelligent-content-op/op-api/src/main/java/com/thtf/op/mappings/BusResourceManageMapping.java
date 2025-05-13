package com.thtf.op.mappings;

import com.thtf.op.entity.BusResourceManageEntity;
import com.thtf.resource.dto.BusResourceManageDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusResourceManageMapping {

    BusResourceManageEntity dto2Entity(BusResourceManageDTO param);

    BusResourceManageDTO entity2Dto(BusResourceManageEntity param);


}
