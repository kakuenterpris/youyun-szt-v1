package com.thtf.resource.enums;

public enum ResourceCategoryEnum {

    UNIT (1, "机构"),
    DEP (2, "部门"),
    PERSONAL (3, "个人");

    private final Integer code;
    private final String name;

    ResourceCategoryEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
