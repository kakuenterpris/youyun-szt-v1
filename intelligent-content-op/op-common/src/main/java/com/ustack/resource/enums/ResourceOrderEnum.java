package com.ustack.resource.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Liyingzheng
 * @data 2025/5/8 14:40
 * @describe 排序方式
 */
@Getter
@AllArgsConstructor
public enum ResourceOrderEnum {
    ASC("asc", "升序"),
    DESC("desc", "降序");

    private final String code;
    private final String name;
}
