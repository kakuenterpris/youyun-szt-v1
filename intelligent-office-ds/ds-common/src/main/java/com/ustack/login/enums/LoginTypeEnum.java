package com.ustack.login.enums;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: LoginTypeEnum
 * @Date: 2024-11-11 11:28
 */
public enum LoginTypeEnum {

    sso("基于技术中台的统一身份认证"),
    redis("基于redis token的身份认证");

    private final String type;

    LoginTypeEnum(String type) {
        this.type = type;
    }
}
