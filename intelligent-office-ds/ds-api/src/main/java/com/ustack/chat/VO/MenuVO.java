package com.ustack.chat.VO;

import com.ustack.chat.entity.SysMenuEntity;
import lombok.Data;

import java.util.List;

@Data
public class MenuVO {
    // 复制需要的字段
    private Long menuId;
    private String menuName;
    private Long parentId;
    /**
     * 父级菜单名
     */
    private String parentName;
    /**
     * 子类资源
     */
    private List<MenuVO> Children;

}
