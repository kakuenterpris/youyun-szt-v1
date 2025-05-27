package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.access.dto.SysRoleDto;
import com.thtf.chat.dto.AssignMenusDTO;
import com.thtf.chat.dto.UpdateRoleDto;
import com.thtf.chat.entity.*;
import com.thtf.chat.repo.FolderAuthRepo;
import com.thtf.chat.repo.SysRoleMenuRepo;
import com.thtf.chat.repo.SysRoleRepo;
import com.thtf.chat.mapper.SysRoleMapper;
import com.thtf.chat.repo.SysUserRoleRepo;
import com.thtf.chat.service.BusUserInfoService;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.global.common.rest.ContextUtil;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.utils.Linq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private  FolderAuthRepo folderAuthRepo;

    @Autowired
    private SysUserRoleRepo sysUserRoleRepo;


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
            List<SysRoleEntity> records = page1.getRecords();
            List<Long> select = Linq.select(records, SysRoleEntity::getRoleId);
            List<FolderAuthEntity> list1 = folderAuthRepo.list(new LambdaQueryWrapper<FolderAuthEntity>().in(FolderAuthEntity::getRoleId, select));
            page1.getRecords().stream().forEach(item -> {
                Integer roleId = Math.toIntExact(item.getRoleId());
                LambdaQueryWrapper<SysRoleMenuEntity> sysRoleMenuQuery = new LambdaQueryWrapper<>();
                sysRoleMenuQuery.eq(SysRoleMenuEntity::getRoleId, roleId);
                List<SysRoleMenuEntity> list = sysRoleMenuRepo.list(sysRoleMenuQuery);
                item.setMenuAuth(list);
                item.setFolderAuthList(list1.stream().filter(item1 -> item1.getRoleId().equals(roleId)).collect(Collectors.toList()));
            });

            return RestResponse.success(page1);
        }catch (Exception e){
            return RestResponse.error("查询失败");
        }
    }

    @Override
    public RestResponse updateByRoleId(UpdateRoleDto role) {
        try {
//            分配知识库权限
            //获取用户角色
            SysRoleEntity roleByUserId = getRoleByUserId();
            if ((roleByUserId==null||!roleByUserId.getRoleKey().equals("security"))&&(role.getFolderAuthList() != null||role.getMenuAuth()!=null)){

            }

            // 删除原有的权限
            folderAuthRepo.remove(new LambdaQueryWrapper<FolderAuthEntity>().eq(FolderAuthEntity::getRoleId, role.getRoleId()));
            List<FolderAuthEntity> folderAuthList = role.getFolderAuthList();
            List<FolderAuthEntity> fileAuthEntities = folderAuthList;
            if (fileAuthEntities != null) {
                for (FolderAuthEntity fileAuthEntity : fileAuthEntities) {
                    fileAuthEntity.setRoleId(Math.toIntExact(role.getRoleId()));
                }
            }
            folderAuthRepo.saveBatch(fileAuthEntities);

            assignMenus(role);

//更新角色
            this.updateById(role);

        }catch (Exception e){
            return RestResponse.fail(1004, "修改失败！" + e.getMessage());
        }
        return RestResponse.success("修改成功");
    }

        public SysRoleEntity getRoleByUserId(){
            SystemUser currentUser = ContextUtil.currentUser();
            String id = currentUser.getId();
            SysUserRoleEntity sysUserRole = sysUserRoleRepo.getOne(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, id));
            return sysUserRole==null?null:this.getById(sysUserRole.getRoleId());
        }

    protected boolean assignMenus(UpdateRoleDto dto) {
        // 分配菜单权限
        AssignMenusDTO amd = new AssignMenusDTO();
        amd.setMenuAuth(dto.getMenuAuth());
        amd.setRoleIds(Collections.singletonList(dto.getRoleId().longValue()));
        return sysRoleMenuRepo.assignMenus(amd);
    }
}




