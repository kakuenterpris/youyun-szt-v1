package com.ustack.access.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SysRuleExtractDto {
    private Long id;

    private String name;

    private String code;

    private List<String> fileIds = new ArrayList<>();

    private List<String> folderIds = new ArrayList<>();
    ;
}
