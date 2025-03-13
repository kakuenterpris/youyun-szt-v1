package com.thtf.chat.mappings;

import com.thtf.chat.entity.BusDepInfoEntity;
import com.thtf.login.dto.BusDepInfoDTO;
import com.thtf.login.dto.TfDepInfoDTO;
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
