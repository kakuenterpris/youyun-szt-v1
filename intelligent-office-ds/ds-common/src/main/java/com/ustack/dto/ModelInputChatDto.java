package com.ustack.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年02月20日
 */
@Data
public class ModelInputChatDto {

    private String personal_knowledge;

    private Integer thtf_knowledge = 0;

    private Integer networking = 0;

    private String networking_knowledge;

    // 数据中台智能体
    private String knowledge;
}
