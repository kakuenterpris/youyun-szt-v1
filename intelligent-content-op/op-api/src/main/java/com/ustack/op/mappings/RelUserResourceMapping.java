package com.ustack.op.mappings;

import com.ustack.op.entity.RelUserResourceEntity;
import com.ustack.resource.dto.RelUserResourceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelUserResourceMapping {

    RelUserResourceEntity dto2Entity(RelUserResourceDTO param);

    RelUserResourceDTO entity2Dto(RelUserResourceEntity param);


}
