package com.ustack.kbase.enums;

import java.util.Objects;

/**
 * @author zhangwei
 * @date 2025年03月26日
 */
public enum KbaseTableEnum {
    COMPANY("KM_COMPANY", "company"),
    DEPARTMENT("KM_DEPARTMENT", "department"),
    PERSONAL("KM_PERSONAL", "personal"),
    TFGF_KM202504("TFGF_KM202504", "TFGF_KM202504"),
    ;

    private String key;

    private String name;

    KbaseTableEnum(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public static String getName(String key) {
        for (KbaseTableEnum kbaseTableEnum : values()) {
            if (Objects.equals(kbaseTableEnum.getKey(), key)) {
                return kbaseTableEnum.getName();
            }
        }
        return null;
    }
}
