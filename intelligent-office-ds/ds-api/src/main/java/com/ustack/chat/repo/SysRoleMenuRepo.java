package com.ustack.chat.repo;

import com.ustack.chat.dto.AssignMenusDTO;
import com.ustack.chat.dto.MenuTreeNode;
import com.ustack.chat.entity.SysRoleMenuEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.global.common.rest.RestResponse;

import java.util.List;

/**
* @author 86187
* @description 针对表【sys_role_menu(角色和菜单关联表)】的数据库操作Service
* @createDate 2025-04-15 18:33:50
*/
public interface SysRoleMenuRepo extends IService<SysRoleMenuEntity> {

    boolean assignMenus(AssignMenusDTO amd);

    RestResponse getByRoleId(Integer roleId);


}
