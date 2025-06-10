package com.ustack.chat.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ustack.chat.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author ruoyi
 */
@Data
public class TreeSelect implements Serializable {

    /**
     * 节点ID
     */
    private Long id;

    /**
     * 节点名称
     */
    private String label;

    /**
     * 节点禁用
     */
    private boolean disabled = false;

    /**
     * 子节点
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect() {

    }

    public TreeSelect(SysDeptEntity dept) {
        this.id = dept.getDeptId();
        this.label = dept.getDeptName();
        this.disabled = StringUtils.equals("1", dept.getStatus());
        this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    public TreeSelect(SysMenuEntity menu) {
        this.id = menu.getMenuId();
        this.label = menu.getMenuName();
        this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
    }

}
