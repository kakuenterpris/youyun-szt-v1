package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ustack.global.common.rest.RestResponse;

import com.ustack.op.entity.SysRoleMenuEntity;
import com.ustack.op.mapper.SysRoleMenuMapper;
import com.ustack.op.repo.SysRoleMenuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 86187
* @description 针对表【sys_role_menu(角色和菜单关联表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysRoleMenuRepoImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity>
    implements SysRoleMenuRepo {

    @Override
    public RestResponse getByRoleId(Integer roleId) {
        try {
            LambdaQueryWrapper<SysRoleMenuEntity> sysRoleMenuQuery = new LambdaQueryWrapper<>();
            sysRoleMenuQuery.eq(SysRoleMenuEntity::getRoleId, roleId);
            return RestResponse.success(this.list(sysRoleMenuQuery));
        } catch (Exception e) {
            return RestResponse.error("获取角色权限失败");
        }
    }




}



