package com.thtf.meeting.dto;

import lombok.Data;

/**
 * Author：PingY
 * Package：com.thtf.meeting.dto
 * Project：intelligent-office-platform
 * Classname：OrderInfoDTO
 * Date：2025/3/26  15:43
 * Description:
 */
@Data
public class OrderInfoDTO {
    private String orderId;
    private int failType;
    private int status;
    private int originalDuration;
    private long realDuration;
    private long expireTime;
}
