package com.ustack.op.entity;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.List;
import java.util.Map;

public class DepartmentResponse {

    private boolean success;
    private String message;
    private List<KrmDepartmentEntity> content;
    private int count;
    private int total;

    // 需要添加的字段处理
    @JsonAnySetter
    public void unpackNested(Map<String, Object> data) {
        // 处理JSON中需要转换的特殊字段
    }

}
