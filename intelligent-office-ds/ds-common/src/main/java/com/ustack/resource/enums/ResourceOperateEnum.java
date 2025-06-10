package com.ustack.resource.enums;

public enum ResourceOperateEnum {

    UP ("up", "置于目标节点上方"),
    DOWN ("down", "置于目标节点下方"),
    INNER ("inner", "置于目标节点内部");

    private final String code;
    private final String name;

    ResourceOperateEnum(String code, String name) {
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
