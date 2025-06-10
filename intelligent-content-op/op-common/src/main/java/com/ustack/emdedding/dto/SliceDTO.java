package com.ustack.emdedding.dto;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年03月27日
 */
@Data
public class SliceDTO {

    private String response_mode = "blocking";

    private String user;

    private SliceInputsDTO inputs;

}

