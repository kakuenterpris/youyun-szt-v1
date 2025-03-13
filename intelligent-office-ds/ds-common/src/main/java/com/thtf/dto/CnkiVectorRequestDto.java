package com.thtf.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年02月21日
 */
@Data
public class CnkiVectorRequestDto {

    private String query;

    private Integer type = 1;

    private Integer limit = 20;

    private Boolean isReranker = true;

    private Double threshold = 0.9;
}
