package com.ustack.resource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Liyingzheng
 * @data 2025/4/25 10:04
 * @describe
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    ADD("add", "新建"),
    DELETE("delete", "删除"),
    GET("get", "查看"),
    PREVIEW("preview", "预览"),
    EDIT("edit", "修改"),
    UPLOAD("upload", "上传"),
    DOWNLOAD("download", "下载"),
    MOVE("move", "移动"),
    RENAME("rename", "重命名");

    private final String code;
    private final String name;
}
