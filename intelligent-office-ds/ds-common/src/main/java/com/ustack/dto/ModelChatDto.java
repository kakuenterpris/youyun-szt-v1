package com.ustack.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年02月18日
 */
@Data
public class ModelChatDto {
    private Object inputs;
    private String query;
    private String response_mode;
    private String conversation_id;
    private String user;
    private List<ModelFileChatDto> files;
}
