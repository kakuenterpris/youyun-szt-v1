package com.ustack.chat.enums;

import java.util.Objects;

public enum ApprovalType {
    // 智能助手
    folder("1", "folderAuth"),
    // 智能代码
    menu("2", "menuAuth");

    private String code;
    private String key;

    ApprovalType(String code, String key) {
        this.code = code;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public static String getKey(String code) {
        for (ApprovalType approvalType : values()) {
            if (Objects.equals(approvalType.getCode(), code)) {
                return approvalType.getKey();
            }
        }
        return null;
    }
}
