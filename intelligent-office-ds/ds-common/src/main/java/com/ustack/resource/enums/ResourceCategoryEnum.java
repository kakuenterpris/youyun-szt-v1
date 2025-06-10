package com.ustack.resource.enums;

public enum ResourceCategoryEnum {

    UNIT ("UNIT", "机构"),
    DEP ("DEP", "部门"),
    USER("USER", "个人");

    private final String code;
    private final String name;

    ResourceCategoryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
