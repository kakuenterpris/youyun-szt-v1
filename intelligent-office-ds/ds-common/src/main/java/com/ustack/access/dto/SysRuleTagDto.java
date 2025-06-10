package com.ustack.access.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysRuleTagDto {
    private List<Long> ids;

    private Long id;

    private Long ruleExtractId;

    private String code;

    private Boolean isUp;
}
