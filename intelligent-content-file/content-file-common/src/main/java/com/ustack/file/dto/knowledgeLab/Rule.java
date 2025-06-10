package com.ustack.file.dto.knowledgeLab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rule {
    /**
     * (array[object])
     */
    private List<PreProcessingRule> pre_processing_rules;

    /**
     * (object) 分段规则
     */
    private Segmentation segmentation;

    /**
     * 父分段的召回模式 full-doc 全文召回 / paragraph 段落召回
     */
    private String parent_mode;

    /**
     * (object) 子分段规则
     */
    private SubChunkSegmentation subchunk_segmentation;

}
