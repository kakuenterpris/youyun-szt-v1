package com.thtf.access.dto;

import lombok.Data;

@Data
public class SysRuleTagDto {

    private Long id;

    private Long ruleExtractId;

    private String code;

    private Boolean isUp;
}
