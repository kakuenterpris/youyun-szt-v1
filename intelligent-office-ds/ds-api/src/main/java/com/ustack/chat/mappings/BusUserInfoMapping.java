package com.ustack.chat.mappings;

import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.dto.BusUserInfoDTO;
import com.ustack.login.dto.TfUserInfoDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusUserInfoMapping {

    BusUserInfoDTO tf2Bs(TfUserInfoDTO param);

    SystemUser bs2User(BusUserInfoDTO param);

    BusUserInfoEntity dto2Entity(BusUserInfoDTO param);

    BusUserInfoDTO entity2Dto(BusUserInfoEntity param);


}
