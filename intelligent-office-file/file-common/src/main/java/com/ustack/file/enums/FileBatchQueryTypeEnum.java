package com.ustack.file.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * @author linxin
 * @Description : 文件批量查询类型枚举
 * @ClassName : FileBatchQueryTypeEnum
 * @Date: 2022-11-03 09:12
 */
@Getter
public enum FileBatchQueryTypeEnum {
    FILE_GUID(10, "文件guid查询"),
    FILE_BUSINESS_ID(20, "业务ID"),


    ;


    private Integer type;

    private String desc;


    FileBatchQueryTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static FileBatchQueryTypeEnum getInstance(Integer type){
        return Arrays.stream(FileBatchQueryTypeEnum.values()).filter(f -> f.getType().equals(type)).findFirst().orElse(null);
    }
}
