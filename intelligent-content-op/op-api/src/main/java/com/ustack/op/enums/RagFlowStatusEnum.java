package com.ustack.op.enums;

public enum RagFlowStatusEnum {

    UPLOAD_RAG("UPLOAD_RAG", "上传"),
    PRASE_FILE("PRASE_FILE", "解析"),
    ;

    private String type;
    private String typeName;

    RagFlowStatusEnum(String type, String typeName) {
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
