package com.ustack.op.mappings;

import com.ustack.op.entity.BusResourceMemberEntity;
import com.ustack.resource.dto.BusResourceMemberDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusResourceMemberMapping {

    BusResourceMemberEntity dto2Entity(BusResourceMemberDTO param);

    BusResourceMemberDTO entity2Dto(BusResourceMemberEntity param);


}
