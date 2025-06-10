package com.ustack.test;

import com.ustack.global.common.rest.ErrorCode;
import lombok.Getter;

/**
 * @Description: 错误码，每个模块分一个号段
 * @author：linxin
 * @ClassName: BizErrorCode
 * @Date: 2025-02-18 12:50
 */
public enum BizErrorCode implements ErrorCode {
    // 登录 1000-1500
    username_password_not_exist(1000, "用户名或者密码错误！"),
    need_login(1001, "您未登录或者登录已过期！"),

    // 其他模块自己分 以下只是示例
    attendance_item_short_name_exist(20002, "考勤符号已存在"),
    attendance_item_create_failed(20003, "考勤项目创建失败"),
    attendance_item_update_failed(20004, "考勤项目编辑失败"),
    attendance_item_delete_failed(20005, "考勤项目删除失败"),;



    BizErrorCode(Integer code, String msg) {
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
