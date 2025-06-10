package com.ustack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author PingY
 * @Classname HistoryChatDTO
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
@Data
@Schema(name = "历史对话实体")
public class HistoryChatDTO {
    /**
     * 用户标识，由开发者定义规则，需保证用户标识在应用内唯一
     */
    private String user;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 当前页第一条聊天记录的 ID，默认 null
     */
    private String firstId;

    /**
     * （选填）一次请求返回多少条记录，默认 20 条，最大 100 条，最小 1 条。
     */
    private Integer limit;

    /**
     * 选填）当前页最后面一条记录的 ID，默认 null
     */
    private String lastId;

    /**
     * 选填）排序字段，默认 -updated_at(按更新时间倒序排列)
     * 可选值：created_at, -created_at, updated_at, -updated_at
     * 字段前面的符号代表顺序或倒序，-代表倒序
     */
    private String sortBy;

    /**
     * 检索词
     */
    private String query;
}
