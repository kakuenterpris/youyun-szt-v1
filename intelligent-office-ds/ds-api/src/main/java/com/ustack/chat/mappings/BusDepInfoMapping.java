package com.ustack.chat.mappings;

import com.ustack.chat.entity.BusDepInfoEntity;
import com.ustack.login.dto.BusDepInfoDTO;
import com.ustack.login.dto.TfDepInfoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusDepInfoMapping {

    BusDepInfoDTO tf2Bs(TfDepInfoDTO param);

    BusDepInfoEntity dto2Entity(BusDepInfoDTO param);

    BusDepInfoDTO entity2Dto(BusDepInfoEntity param);


}
