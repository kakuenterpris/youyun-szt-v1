package com.ustack.file.dto.knowledgeLab;

import lombok.Data;

@Data
public class SubChunkSegmentation {

    /**
     * 分段标识符，目前仅允许设置一个分隔符。默认为
     */
    private String separator;

    /**
     * 最大长度 (token) 需要校验小于父级的长度
     */
    private Long max_tokens;

    /**
     * 分段重叠指的是在对数据进行分段时，段与段之间存在一定的重叠部分（选填）
     */
    private String chunk_overlap;
}