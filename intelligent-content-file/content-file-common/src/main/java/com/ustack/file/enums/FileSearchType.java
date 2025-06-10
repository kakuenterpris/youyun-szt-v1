package com.ustack.file.enums;

import lombok.Getter;

import java.util.stream.Stream;

/**
 * @Description : 文件搜索类型(文档服务管理页)
 * @Author : LinXin
 * @ClassName : FileSearchType
 * @Date: 2021-04-08 17:12
 */
public enum  FileSearchType {

    DIR("dir"),
    UUID("uuid"),
    BIZID("bizId");

    @Getter
    private String type;

    FileSearchType(String type){
        this.type = type;
    }

    public static FileSearchType getInstance(String type){
        return Stream.of(FileSearchType.values()).filter(e -> e.type.equals(type))
                .findFirst()
                .orElse(null);
    }

}
