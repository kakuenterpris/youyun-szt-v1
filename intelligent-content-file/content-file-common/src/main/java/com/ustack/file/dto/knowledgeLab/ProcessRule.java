package com.ustack.file.dto.knowledgeLab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessRule {

    /**
     * mode (string) 清洗、分段模式 ，automatic 自动 / custom 自定义
     */
    private String mode;

    /**
     * rules (object) 自定义规则（自动模式下，该字段为空）
     */
    private Rule rules;

}