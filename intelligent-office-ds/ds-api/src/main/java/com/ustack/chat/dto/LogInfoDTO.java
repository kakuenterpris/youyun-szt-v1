package com.ustack.chat.dto;

import lombok.Data;

@Data
public class LogInfoDTO {
    //id
    private String id;
    //用户
    private String userName;
    //角色
    private String roleName;
    //IP
    private String IP;
    //IP来源
    private String IPOrigin;
    //日志类型
    private String logType;
    //操作内容
    private String content;
    //请求耗时
    private String timeConsuming;
    //操作时间
    private String creatTime;
}
