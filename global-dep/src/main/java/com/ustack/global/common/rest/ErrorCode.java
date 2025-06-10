package com.ustack.global.common.rest;

import org.apache.commons.lang3.StringUtils;

import java.util.MissingFormatArgumentException;
import java.util.Objects;

/**
 * @Description: 错误码定义
 * @author：linxin
 * @ClassName: ErrorCodeTag
 * @Date: 2023-12-15 10:42
 */
public interface ErrorCode {

    String name();

    String getMsg();

    Integer getCode();

    default String fmtMsg(String ...params){
        try {
            return (Objects.isNull(params) || params.length == 0 ) ? StringUtils.replace(this.getMsg(),"%s", "") : String.format(this.getMsg(), params);
        } catch (MissingFormatArgumentException e) {
            return this.getMsg();
        }
    }

}
