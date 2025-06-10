package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.SysRoleEntity;

import java.util.List;

/**
* @author 86187
* @description 针对表【sys_role(角色信息表)】的数据库操作Service
* @createDate 2025-04-15 18:33:50
*/
public interface SysRoleRepo extends IService<SysRoleEntity> {
//    根据用户id查看角色
    List<SysRoleEntity> getRoleByUserId(Integer userId);

}
