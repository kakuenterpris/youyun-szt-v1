package com.ustack.chat.mappings;

import com.ustack.chat.entity.BusResourceManageEntity;
import com.ustack.chat.entity.RelUserResourceEntity;
import com.ustack.resource.dto.BusResourceManageDTO;
import com.ustack.resource.dto.RelUserResourceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelUserResourceMapping {

    RelUserResourceEntity dto2Entity(RelUserResourceDTO param);

    RelUserResourceDTO entity2Dto(RelUserResourceEntity param);

}
