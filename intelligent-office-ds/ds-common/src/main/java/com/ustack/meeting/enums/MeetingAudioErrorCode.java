package com.ustack.meeting.enums;

import com.ustack.global.common.rest.ErrorCode;
import lombok.Getter;

/**
 * @author PingY
 * @Classname ResourceErrorCode
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
public enum MeetingAudioErrorCode implements ErrorCode {
    ADD_FAIL(1000, "添加失败"),
    DATA_NOT_EXISTS(1001, "数据不存在"),
    EDIT_FAIL(1002, "编辑失败"),
    DELETE_FAIL(1003, "删除失败"),
    EDIT_GUID_NOT_EXISTS(1004, "编辑数据不存在"),
    DELETE_GUID_NOT_EXISTS(1005, "删除的数据不存在"),
    ID_NULL(1007, "ID为空"),
    FILE_ID_NULL(1008, "文件ID为空"),
    ;



    MeetingAudioErrorCode(Integer code, String msg) {
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
