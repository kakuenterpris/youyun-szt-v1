package com.thtf.file.dto.knowledgeLab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Segmentation {

    /**
     * 自定义分段标识符，目前仅允许设置一个分隔符。默认为 \n
     */
    private String separator;

    /**
     * max_tokens 最大长度（token）默认为 1000
     */
    private Long max_tokens;
}
