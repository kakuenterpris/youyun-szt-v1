package com.thtf.op.mappings;

import com.thtf.op.entity.RelUserResourceEntity;
import com.thtf.resource.dto.RelUserResourceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RelUserResourceMapping {

    RelUserResourceEntity dto2Entity(RelUserResourceDTO param);

    RelUserResourceDTO entity2Dto(RelUserResourceEntity param);


}
