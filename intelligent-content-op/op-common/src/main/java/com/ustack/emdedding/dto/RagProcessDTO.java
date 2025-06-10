package com.ustack.emdedding.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年04月14日
 */
@Data
public class RagProcessDTO {

    private String fileId;

    private Long resourceId;

    private String embeddingConfigCode;
}
