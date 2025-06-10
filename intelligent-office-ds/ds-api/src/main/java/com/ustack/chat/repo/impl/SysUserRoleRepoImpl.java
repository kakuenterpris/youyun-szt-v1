package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.dto.AssignRolesDTO;
import com.ustack.chat.entity.SysUserRoleEntity;
import com.ustack.chat.repo.SysUserRoleRepo;
import com.ustack.chat.mapper.SysUserRoleMapper;
import com.ustack.global.common.rest.RestResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 86187
* @description 针对表【sys_user_role(用户和角色关联表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysUserRoleRepoImpl extends ServiceImpl<SysUserRoleMapper, SysUserRoleEntity>
    implements SysUserRoleRepo {

    @Autowired
    private SysUserRoleMapper sysUserRoleRepo;

    @Override
    public boolean assignRoles(AssignRolesDTO dto) {
        // 删除历史
        List<Long> userIds = dto.getUserIds();
        this.removeBatchByIds(userIds);
        if (CollectionUtils.isEmpty(dto.getRoleIds())) {
            // 无需分配角色
            return true;
        }

        // 批量新增
        List<SysUserRoleEntity> sysUserRoleList = new ArrayList<>();
        userIds.forEach(userId -> sysUserRoleList.addAll(dto.getRoleIds().stream()
                .map(roleId -> {
                    SysUserRoleEntity sysUserRole = new SysUserRoleEntity();
                    sysUserRole.setUserId(userId);
                    sysUserRole.setRoleId(roleId);
                    return sysUserRole;
                }).toList()));
        return super.saveBatch(sysUserRoleList);
    }

    @Override
    public RestResponse getUserPermissions(Integer userId) {
        try{
            LambdaQueryWrapper<SysUserRoleEntity> sysUserRoleQuery = new LambdaQueryWrapper<>();
            sysUserRoleQuery.eq(SysUserRoleEntity::getUserId, userId);
            this.list(sysUserRoleQuery);
        }catch (Exception e){
            return RestResponse.error("获取用户权限失败");
        }
        return RestResponse.success("获取用户权限成功");
    }

}




