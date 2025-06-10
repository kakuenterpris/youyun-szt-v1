package com.ustack.chat.repo;

import com.ustack.chat.dto.AssignRolesDTO;
import com.ustack.chat.entity.SysUserRoleEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.global.common.rest.RestResponse;

/**
* @author 86187
* @description 针对表【sys_user_role(用户和角色关联表)】的数据库操作Service
* @createDate 2025-04-15 18:33:50
*/
public interface SysUserRoleRepo extends IService<SysUserRoleEntity> {
    boolean assignRoles(AssignRolesDTO dto);

    RestResponse getUserPermissions(Integer userId);
}
