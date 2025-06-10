package com.ustack.chat.mappings;

import com.ustack.login.dto.TfJobInfoDTO;
import com.ustack.login.dto.TfUserInfoDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface LoginMapping {

    TfJobInfoDTO user2Job(TfUserInfoDTO param);


}
