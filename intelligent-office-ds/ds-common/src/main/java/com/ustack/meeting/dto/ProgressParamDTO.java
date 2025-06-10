package com.ustack.meeting.dto;

import lombok.Data;

/**
 * Author：PingY
 * Package：com.ustack.meeting.dto
 * Project：intelligent-office-platform
 * Classname：ProgressParamDTO
 * Date：2025/3/27  20:49
 * Description:
 */
@Data
public class ProgressParamDTO {
    private Long contentId;
    private String fileId;
    private Long realDuration;
    private String realDurationString;
}
