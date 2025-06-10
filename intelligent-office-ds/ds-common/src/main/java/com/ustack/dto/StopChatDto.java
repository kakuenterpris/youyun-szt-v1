package com.ustack.dto;

import lombok.Data;

/**
 * 停止会话
 * @author zhangwei
 * @date 2025年02月23日
 */
@Data
public class StopChatDto {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 场景类型
     */
    private String sceneType;
}
