package com.ustack.emdedding.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年03月25日
 */
@Data
public class EmbeddingDTO {

    private String model = "bge-m3";

    private String encoding_format = "float";

    private List<String> input;
}
