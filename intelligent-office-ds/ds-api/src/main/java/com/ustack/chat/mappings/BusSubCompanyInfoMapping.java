package com.ustack.chat.mappings;

import com.ustack.chat.entity.BusSubCompanyInfoEntity;
import com.ustack.login.dto.BusSubCompanyInfoDTO;
import com.ustack.login.dto.TfSubCompanyInfoDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusSubCompanyInfoMapping {

    BusSubCompanyInfoEntity dto2Entity(BusSubCompanyInfoDTO param);

    BusSubCompanyInfoDTO entity2Dto(BusSubCompanyInfoEntity param);

    BusSubCompanyInfoDTO tf2Bs(TfSubCompanyInfoDTO param);


}
