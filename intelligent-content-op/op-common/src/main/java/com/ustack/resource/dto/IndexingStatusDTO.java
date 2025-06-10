package com.ustack.resource.dto;

import lombok.Data;

@Data
public class IndexingStatusDTO {

    /**
     * 文档 ID
     */
    private String id;

    /**
     * 向量化状态
     */
    private String indexing_status;

    /**
     * 处理开始时间-时间戳
     */
    private Double processing_started_at;

    /**
     * 解析完成时间-时间戳
     */
    private Double parsing_completed_at;

    /**
     * 清洗完成时间-时间戳
     */
    private Double cleaning_completed_at;

    /**
     * 拆分完成时间-时间戳
     */
    private Double splitting_completed_at;

    /**
     * 向量化完成时间-时间戳
     */
    private Double completed_at;

    /**
     * 暂停时间-时间戳
     */
    private Double paused_at;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 停止时间-时间戳
     */
    private Double stopped_at;

    /**
     * 已完成段落数量
     */
    private Integer completed_segments;

    /**
     * 总段落数量
     */
    private Integer total_segments;
}
