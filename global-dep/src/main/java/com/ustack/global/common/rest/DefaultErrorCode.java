package com.ustack.global.common.rest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author linxin
 * @Description : 错误码
 * @ClassName : DefaultErrorCode
 * @Date: 2022-10-24 10:30
 */
@Slf4j
public enum DefaultErrorCode implements ErrorCode {

    SYSTEM_INTERNAL_ERROR(500, "系统内部异常"),
    SYSTEM_INTERNAL_ERROR_FMT(500, "系统内部异常：%s"),
    INVALID_TOKEN(601, "无效token"),
    USERNAME_PASSWORD_WRONG(602, "用户名密码错误"),
    VERIFY_CODE_WRONG(603, "验证码错误"),
    PERMISSION_DENIED(604, "权限不足"),

    AUTH_REQUEST_ERROR(600, "请求认证接口失败"),
    AUTH_RESPONSE_EMPTY(605, "请求认证接口响应结果为空"),
    AUTH_RESPONSE_FAIL(606, "请求认证接口响应结果失败"),

    ADD_ERROR(501,"新增失败"),
    UPDATE_ERROR(502,"修改失败"),
    DELETE_ERROR(503,"删除失败"),
    GET_ERROR(504,"查询失败"),
    CHAT_ERROR(701,"对话失败"),
    NAME_ERROR(401,"用户不存在")

    ;


    @Getter
    private final Integer code;

    @Getter
    private final String msg;

    DefaultErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
