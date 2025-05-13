package com.thtf.op.mappings;

import com.thtf.op.entity.BusResourceFileEntity;
import com.thtf.resource.dto.BusResourceFileDTO;
import com.thtf.resource.dto.BusResourceManageListDTO;
import com.thtf.resource.vo.BusResourceFileVO;
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
