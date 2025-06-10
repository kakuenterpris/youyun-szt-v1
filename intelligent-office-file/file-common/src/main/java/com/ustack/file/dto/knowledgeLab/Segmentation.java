package com.ustack.file.dto.knowledgeLab;

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
    private Integer max_tokens;

    /**
     * 分段重叠指的是在对数据进行分段时，段与段之间存在一定的重叠部分（选填）
     */
    private Integer chunk_overlap;
}
