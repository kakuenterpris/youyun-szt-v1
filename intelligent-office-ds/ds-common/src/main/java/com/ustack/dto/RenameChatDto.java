package com.ustack.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年02月23日
 */
@Data
public class RenameChatDto {

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 会话名称
     */
    private String conversationName;
}
