package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.access.dto.SysRoleDto;
import com.thtf.chat.dto.AssignMenusDTO;
import com.thtf.chat.dto.UpdateRoleDto;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.entity.SysRoleMenuEntity;
import com.thtf.chat.repo.SysRoleMenuRepo;
import com.thtf.chat.repo.SysRoleRepo;
import com.thtf.chat.mapper.SysRoleMapper;
import com.thtf.global.common.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
* @author 86187
* @description 针对表【sys_role(角色信息表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysRoleRepoImpl extends ServiceImpl<SysRoleMapper, SysRoleEntity>
    implements SysRoleRepo {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuRepo sysRoleMenuRepo;


    @Override
    public List<SysRoleEntity> getRoleByUserId(Integer userId) {
        return sysRoleMapper.getRoleByUserId(userId);
    }

    @Override
    public RestResponse pageList(Page<SysRoleEntity> page, SysRoleDto vo) {
        try {
            LambdaQueryWrapper<SysRoleEntity> roleQuery = new LambdaQueryWrapper<>();
            roleQuery.eq(vo.getRoleName() != null, SysRoleEntity::getRoleName, vo.getRoleName());
            roleQuery.eq(vo.getStatus() != null, SysRoleEntity::getStatus, vo.getStatus());
            Page<SysRoleEntity> page1 = this.page(page, roleQuery);
            page1.getRecords().stream().forEach(item -> {
                Integer roleId = Math.toIntExact(item.getRoleId());
                LambdaQueryWrapper<SysRoleMenuEntity> sysRoleMenuQuery = new LambdaQueryWrapper<>();
                sysRoleMenuQuery.eq(SysRoleMenuEntity::getRoleId, roleId);
                List<SysRoleMenuEntity> list = sysRoleMenuRepo.list(sysRoleMenuQuery);
                item.setMenuAuth(list);
            });

            return RestResponse.success(page1);
        }catch (Exception e){
            return RestResponse.error("查询失败");
        }
    }

    @Override
    public RestResponse updateByRoleId(UpdateRoleDto role) {
        try {
            this.updateById(role);
            assignMenus(role);
        }catch (Exception e){
            return RestResponse.fail(1004, "修改失败！" + e.getMessage());
        }
        return RestResponse.success("修改成功");
    }
    protected boolean assignMenus(UpdateRoleDto dto) {
        // 分配角色
        AssignMenusDTO amd = new AssignMenusDTO();
        amd.setMenuAuth(dto.getMenuAuth());
        amd.setRoleIds(Collections.singletonList(dto.getRoleId().longValue()));
        return sysRoleMenuRepo.assignMenus(amd);
    }
}




