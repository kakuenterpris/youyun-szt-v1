package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.access.dto.SysRoleDto;
import com.ustack.chat.dto.AssignMenusDTO;
import com.ustack.chat.dto.UpdateRoleDto;
import com.ustack.chat.entity.*;
import com.ustack.chat.enums.ApprovalType;
import com.ustack.chat.repo.ApprovalDetailRepo;
import com.ustack.chat.repo.FolderAuthRepo;
import com.ustack.chat.repo.SysAuthApprovalRepo;
import com.ustack.chat.repo.SysRoleMenuRepo;
import com.ustack.chat.repo.SysRoleRepo;
import com.ustack.chat.mapper.SysRoleMapper;
import com.ustack.chat.repo.SysUserRoleRepo;
import com.ustack.chat.service.BusUserInfoService;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.Linq;
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

    @Autowired
    private SysAuthApprovalRepo sysAuthApprovalRepo;

    @Autowired
    private ApprovalDetailRepo approvalDetailRepo;


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
            Page<SysRoleEntity> pageData = this.page(page, roleQuery);
            List<SysRoleEntity> records = pageData.getRecords();
            List<Long> select = Linq.select(records, SysRoleEntity::getRoleId);
            List<FolderAuthEntity> folderAuths= new ArrayList<>();
            List<SysRoleMenuEntity> menuAuths = new ArrayList<>();
            if (select != null && select.size()>0) {
                folderAuths = folderAuthRepo.list(new LambdaQueryWrapper<FolderAuthEntity>().in(FolderAuthEntity::getRoleId, select));
                menuAuths = sysRoleMenuRepo.list(new LambdaQueryWrapper<SysRoleMenuEntity>().in(SysRoleMenuEntity::getRoleId, select));
            }
            for (SysRoleEntity item : records) {
                Long roleId = item.getRoleId();
                item.setMenuAuth(menuAuths.stream().filter(menuAuth -> menuAuth.getRoleId().equals(roleId)).collect(Collectors.toList()));
                item.setFolderAuthList(folderAuths.stream().filter(folderAuth -> folderAuth.getRoleId().equals(Math.toIntExact(roleId))).collect(Collectors.toList()));
            }
            return RestResponse.success(pageData);
        }catch (Exception e){
            return RestResponse.error("查询失败");
        }
    }

    @Override
    public RestResponse updateByRoleId(UpdateRoleDto role) {
        try {
            SysAuthApprovalEntity sysAuthApproval = new SysAuthApprovalEntity();
            List<FolderAuthEntity> folderAuthList = role.getFolderAuthList();
            List<SysRoleMenuEntity> menuAuthList = role.getMenuAuth();
            boolean editFolderAuth = role.getFolderAuthList() != null;
            boolean editMenuAuth = role.getMenuAuth()!=null;

//            分配知识库权限
            //获取用户角色
            SysRoleEntity roleByUserId = getRoleByUserId();
            if ((role.getFolderAuthList() != null||role.getMenuAuth()!=null)){
                List<ApprovalDetailEntity> approval = new ArrayList<>();
//               创建审批
                sysAuthApproval.setRoleId(Math.toIntExact(role.getRoleId()));
                sysAuthApproval.setStatus("0");
                if (editFolderAuth){
                    sysAuthApproval.setIsUpdateFolderAuth(1);
                }else {
                    sysAuthApproval.setIsUpdateFolderAuth(0);
                }
                if (editMenuAuth){
                    sysAuthApproval.setIsUpdateMenuAuth(1);
                }else {
                    sysAuthApproval.setIsUpdateMenuAuth(0);
                }
                sysAuthApprovalRepo.save(sysAuthApproval);
//               记录审批ID
                Long id = sysAuthApproval.getId();
                if (editFolderAuth) {
                    sysAuthApproval.setIsUpdateFolderAuth(1);
                    for (FolderAuthEntity folderAuthEntity : folderAuthList) {
                        ApprovalDetailEntity approvalDetailEntity = new ApprovalDetailEntity();
                        approvalDetailEntity.setApprovalId(id);
                        approvalDetailEntity.setRoleId(folderAuthEntity.getRoleId());
                        approvalDetailEntity.setFolderOrMenu(folderAuthEntity.getFolderId());
                        approvalDetailEntity.setAuthType(ApprovalType.folder.getCode());
                        approval.add(approvalDetailEntity);
                    }
                }
                if (editMenuAuth) {
                    sysAuthApproval.setIsUpdateMenuAuth(1);
                    for (SysRoleMenuEntity sysRoleMenu : menuAuthList) {
                        ApprovalDetailEntity approvalDetailEntity = new ApprovalDetailEntity();
                        approvalDetailEntity.setApprovalId(id);
                        approvalDetailEntity.setRoleId(Math.toIntExact(sysRoleMenu.getRoleId()));
                        approvalDetailEntity.setFolderOrMenu(Math.toIntExact(sysRoleMenu.getMenuId()));
                        approvalDetailEntity.setAuthType(ApprovalType.menu.getCode());
                        approvalDetailEntity.setAuthManage(sysRoleMenu.getManageAuth());
                        approval.add(approvalDetailEntity);
                    }
                }

                approvalDetailRepo.saveBatch(approval);
            }
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




