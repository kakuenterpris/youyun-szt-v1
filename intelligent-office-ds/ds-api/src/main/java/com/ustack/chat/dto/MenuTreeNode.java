package com.ustack.chat.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuTreeNode {

    private Long id;
    private String name;
    private Long parentId;
    private String path;
    private String icon;
    private Integer sort; // 排序字段
    private List<MenuTreeNode> children;


}
