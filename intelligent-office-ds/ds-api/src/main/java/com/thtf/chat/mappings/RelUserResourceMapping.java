package com.thtf.chat.mappings;

import com.thtf.chat.entity.BusResourceManageEntity;
import com.thtf.chat.entity.RelUserResourceEntity;
import com.thtf.resource.dto.BusResourceManageDTO;
import com.thtf.resource.dto.RelUserResourceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelUserResourceMapping {

    RelUserResourceEntity dto2Entity(RelUserResourceDTO param);

    RelUserResourceDTO entity2Dto(RelUserResourceEntity param);


}
