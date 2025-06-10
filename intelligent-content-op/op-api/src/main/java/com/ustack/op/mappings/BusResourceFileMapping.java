package com.ustack.op.mappings;

import com.ustack.op.entity.BusResourceFileEntity;
import com.ustack.resource.dto.BusResourceFileDTO;
import com.ustack.resource.dto.BusResourceManageListDTO;
import com.ustack.resource.vo.BusResourceFileVO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusResourceFileMapping {

    BusResourceManageListDTO dto2ListDto(BusResourceFileDTO param);

    BusResourceFileDTO listDto2Dto(BusResourceManageListDTO param);

    BusResourceFileEntity dto2Entity(BusResourceFileDTO param);

    BusResourceFileDTO entity2Dto(BusResourceFileEntity param);

    BusResourceFileVO entity2Vo(BusResourceFileEntity param);


}
