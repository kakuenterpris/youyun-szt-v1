package com.thtf.op.mappings;

import com.thtf.op.entity.BusResourceFolderEntity;
import com.thtf.resource.dto.BusResourceFolderDTO;
import com.thtf.resource.dto.BusResourceManageListDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusResourceFolderMapping {

    BusResourceManageListDTO dto2ListDto(BusResourceFolderDTO param);

    BusResourceFolderDTO listDto2Dto(BusResourceManageListDTO param);

    BusResourceFolderEntity dto2Entity(BusResourceFolderDTO param);

    BusResourceFolderDTO entity2Dto(BusResourceFolderEntity param);


}
