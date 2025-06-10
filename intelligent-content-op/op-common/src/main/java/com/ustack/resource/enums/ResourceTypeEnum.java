package com.ustack.resource.enums;

public enum ResourceTypeEnum {

    RESOURCE_FOLDER(1, "文件夹"),
    RESOURCE_FILE (2, "文件");

    private final Integer code;
    private final String name;

    ResourceTypeEnum(Integer code, String name) {
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
