package com.ustack.phrases.enums;

import com.ustack.global.common.rest.ErrorCode;
import lombok.Getter;

/**
 * @Description: 错误码，每个模块分一个号段
 * @author：linxin
 * @ClassName: BizErrorCode
 * @Date: 2025-02-18 12:50
 */
public enum PhrasesErrorCode implements ErrorCode {
    ADD_FAIL(1101, "添加失败"),
    DATA_NOT_EXISTS(1102, "数据不存在"),
    EDIT_FAIL(1103, "编辑失败"),
    DELETE_FAIL(1104, "删除失败"),
    EDIT_GUID_NOT_EXISTS(1105, "编辑数据不存在"),
    DELETE_GUID_NOT_EXISTS(1106, "删除的数据不存在"),
    NO_AUTH(1107, "操作失败，无权限"),;



    PhrasesErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Getter
    private final Integer code;

    @Getter
    private final String msg;


    @Override
    public String getMsg() {
        return "";
    }

    @Override
    public Integer getCode() {
        return 0;
    }
}
