package com.ustack.op.enums;

public enum FileScopeTypeEnum {
    ONLY_ME("ONLY_ME", "只有我"),
    SAME_SECURITY_LEVEL("SAME_SECURITY_LEVEL", "同密级可见"),
    SAME_OR_HIGHER("SAME_OR_HIGHER", "同级及上级可见"),
    CUSTOM("CUSTOM", "自定义")
    ;

    private String type;
    private String typeName;

    FileScopeTypeEnum(String type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }
}
