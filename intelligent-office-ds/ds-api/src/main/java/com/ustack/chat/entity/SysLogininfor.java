package com.ustack.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ustack.global.common.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class SysLogininfor extends BaseEntity {


    /** 用户账号 */
    private String userName;

    /** 登录状态 0成功 1失败 */
    private String status;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录地点 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 提示消息 */
    private String msg;

    /** 访问时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

}
