package com.ustack.op.enums;

public enum ChunksStatusEnum {
    RUNNING("RUNNING", "切片中"),
    DONE("DONE", "切片完成"),
    FAIL("FAIL", "切片失败"),
    ;

    private String status;
    private String statusName;

    ChunksStatusEnum(String status, String statusName) {
        this.status = status;
        this.statusName = statusName;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }
}
