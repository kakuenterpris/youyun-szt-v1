package com.ustack.meeting.dto;

import lombok.Data;

/**
 * Author：PingY
 * Package：com.ustack.meeting.dto
 * Project：intelligent-office-platform
 * Classname：ContentDTO
 * Date：2025/3/26  15:43
 * Description:
 */
@Data
public class ContentDTO {
    private OrderInfoDTO orderInfo;
    private String orderResult;
    private int taskEstimateTime;
}