package com.ustack.file.enums;

import lombok.Getter;

/**
 * @author linxin
 * @Description : 文件列表，文件类型
 * @ClassName : FileListTypeEnum
 * @Date: 2022-06-21 11:39
 */
public enum FileListTypeEnum {
    DIR("dir"),
    FILE("com/ustack/file"),

    ;

    @Getter
    private String type;

    FileListTypeEnum(String type) {
        this.type = type;
    }
}
