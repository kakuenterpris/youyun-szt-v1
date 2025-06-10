package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.SysRoleMenuEntity;

/**
* @author 86187
* @description 针对表【sys_role_menu(角色和菜单关联表)】的数据库操作Service
* @createDate 2025-04-15 18:33:50
*/
public interface SysRoleMenuRepo extends IService<SysRoleMenuEntity> {


    RestResponse getByRoleId(Integer roleId);


}
