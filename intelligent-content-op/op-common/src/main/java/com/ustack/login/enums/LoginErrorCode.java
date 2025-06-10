package com.ustack.login.enums;

import com.ustack.global.common.rest.ErrorCode;
import lombok.Getter;

/**
 * @Description: 错误码，每个模块分一个号段
 * @author：linxin
 * @ClassName: BizErrorCode
 * @Date: 2025-02-18 12:50
 */
public enum LoginErrorCode implements ErrorCode {
    USERNAME_PASSWORD_WRONG(1001, "账号或密码错误"),
    AD_ACCOUNT_LOGIN_FAILED(1002, "AD域账号登录失败"),
    PLEASE_INPUT_VERIFY_CODE(1003, "请输入验证码！！"),
    VERIFY_CODE_ERROR(1003, "验证码错误！！"),
    VERIFY_CODE_EXPIRE(1004, "验证码已过期！！"),;



    LoginErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Getter
    private final Integer code;

    @Getter
    private final String msg;


    @Override
    public String getMsg() {
        return "";
    }

    @Override
    public Integer getCode() {
        return 0;
    }
}
