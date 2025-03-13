package com.thtf.chat.mappings;

import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.global.common.dto.BusUserInfoDTO;
import com.thtf.login.dto.TfUserInfoDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface BusUserInfoMapping {

    BusUserInfoDTO tf2Bs(TfUserInfoDTO param);

    SystemUser bs2User(BusUserInfoDTO param);

    BusUserInfoEntity dto2Entity(BusUserInfoDTO param);

    BusUserInfoDTO entity2Dto(BusUserInfoEntity param);


}
