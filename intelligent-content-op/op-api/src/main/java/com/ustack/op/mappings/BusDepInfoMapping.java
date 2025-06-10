package com.ustack.op.mappings;

import com.ustack.op.entity.BusDepInfoEntity;
import com.ustack.login.dto.BusDepInfoDTO;
import com.ustack.login.dto.TfDepInfoDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusDepInfoMapping {

    BusDepInfoDTO tf2Bs(TfDepInfoDTO param);

    BusDepInfoEntity dto2Entity(BusDepInfoDTO param);

    BusDepInfoDTO entity2Dto(BusDepInfoEntity param);


}
