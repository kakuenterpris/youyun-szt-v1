package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.dto.AssignMenusDTO;
import com.ustack.chat.dto.MenuTreeNode;
import com.ustack.chat.entity.SysMenuEntity;
import com.ustack.chat.entity.SysRoleMenuEntity;
import com.ustack.chat.entity.SysUserRoleEntity;
import com.ustack.chat.repo.SysRoleMenuRepo;
import com.ustack.chat.mapper.SysRoleMenuMapper;
import com.ustack.global.common.rest.RestResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
* @author 86187
* @description 针对表【sys_role_menu(角色和菜单关联表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysRoleMenuRepoImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity>
    implements SysRoleMenuRepo {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;


    @Override
    public boolean assignMenus(AssignMenusDTO dto) {
        // 删除历史
        List<Long> roleIds = dto.getRoleIds();
        List<SysRoleMenuEntity> menuAuths = dto.getMenuAuth();
        this.removeBatchByIds(roleIds);
        if (CollectionUtils.isEmpty(menuAuths)) {
            // 无需分配角色
            return true;
        }

        // 批量新增
        List<SysRoleMenuEntity> sysRoleMenuList = new ArrayList<>();
        roleIds.forEach(roleId -> sysRoleMenuList.addAll(dto.getMenuAuth().stream()
                .map(menuauth -> {
                    SysRoleMenuEntity sysRoleMenu = new SysRoleMenuEntity();
                    sysRoleMenu.setRoleId(roleId);
                    sysRoleMenu.setMenuId(menuauth.getMenuId());
                    sysRoleMenu.setManageAuth(menuauth.getManageAuth());
                    return sysRoleMenu;
                }).toList()));
        return super.saveBatch(sysRoleMenuList);
    }

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



