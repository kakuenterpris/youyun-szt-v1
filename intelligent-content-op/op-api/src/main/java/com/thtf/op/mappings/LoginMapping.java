package com.thtf.op.mappings;

import com.thtf.login.dto.TfJobInfoDTO;
import com.thtf.login.dto.TfUserInfoDTO;
import org.mapstruct.Mapper;

/**
 * @Description: 对象转换
 * @author：linxin
 * @ClassName: ExampleMapping
 * @Date: 2025-02-17 23:37
 */
@Mapper(componentModel = "spring")
public interface LoginMapping {

    TfJobInfoDTO user2Job(TfUserInfoDTO param);


}
