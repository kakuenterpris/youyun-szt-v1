package com.ustack.login.enums;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: LoginTypeEnum
 * @Date: 2024-11-11 11:28
 */
public enum UserSpecialAuthEnum {

    UNIT_FILE_MANAGE("UNIT_FILE_MANAGE", "机构知识库管理"),

    SYSTEM_MANAGE("SYSTEM_MANAGE", "知识管理平台系统管理员");

    private final String authCode;
    private final String auth;

    UserSpecialAuthEnum(String authCode, String auth) {
        this.authCode = authCode;
        this.auth = auth;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getAuth() {
        return auth;
    }
}
