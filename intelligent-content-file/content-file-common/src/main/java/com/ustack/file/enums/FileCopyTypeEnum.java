package com.ustack.file.enums;

import lombok.Getter;

/**
 * @author linxin
 * @Description : 文件复制类型
 * @ClassName : FileCopyTypeEnum
 * @Date: 2022-07-06 08:37
 */
public enum FileCopyTypeEnum {

    COPY_BY_BUSINESS_GUID(10, "根据业务guid复制"),
    COPY_BY_FILE_GUID(20, "根据文件guid复制"),

    ;


    @Getter
    private Integer type;
    @Getter
    private String desc;

    FileCopyTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
