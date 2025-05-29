package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.dto.ApproveDTO;
import com.thtf.chat.dto.AssignMenusDTO;
import com.thtf.chat.dto.UpdateRoleDto;
import com.thtf.chat.entity.ApprovalDetailEntity;
import com.thtf.chat.entity.FileAuthEntity;
import com.thtf.chat.entity.FolderAuthEntity;
import com.thtf.chat.entity.SysAuthApprovalEntity;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.entity.SysRoleMenuEntity;
import com.thtf.chat.mapper.SysAuthApprovalMapper;
import com.thtf.chat.repo.ApprovalDetailRepo;
import com.thtf.chat.repo.FolderAuthRepo;
import com.thtf.chat.repo.SysAuthApprovalRepo;
import com.thtf.chat.repo.SysRoleMenuRepo;
import com.thtf.chat.repo.SysRoleRepo;
import com.thtf.global.common.rest.RestResponse;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.role;

/**
* @author 86187
* @description 针对表【SYS_AUTH_APPROVAL(系统审批表)】的数据库操作Service实现
* @createDate 2025-05-29 14:02:09
*/
@Service
public class SysAuthApprovalRepoImpl extends ServiceImpl<SysAuthApprovalMapper, SysAuthApprovalEntity>
    implements SysAuthApprovalRepo {
    @Autowired
    private ApprovalDetailRepo approvalDetailRepo;

    @Autowired
    private FolderAuthRepo folderAuthRepo;

    @Autowired
    private SysRoleMenuRepo sysRoleMenuRepo;


    @Override
    public RestResponse approveDispose(ApproveDTO approveDTO) {
        SysAuthApprovalEntity sysAuthApproval = this.getById(approveDTO.getId());

        List<ApprovalDetailEntity> folderAuths=approvalDetailRepo.getFolderAuthByApproveId(sysAuthApproval.getId());
        List<ApprovalDetailEntity> menuAuths=approvalDetailRepo.getMenuAuthByApproveId(sysAuthApproval.getId());
        if (approveDTO.getIsApprove()){
            sysAuthApproval.setStatus("1");
            //修改权限
            if (folderAuths!= null) {
                //分配文件夹权限
                assignFolder(folderAuths,sysAuthApproval.getRoleId());
            }
            if (menuAuths!=null) {
                // 分配菜单权限
                assignMenus(menuAuths,sysAuthApproval.getRoleId());
            }
        }else {
            // 拒绝
            sysAuthApproval.setStatus("2");
            this.updateById(sysAuthApproval);
            // 状态这设置驳回
        }
        return RestResponse.success("审批成功");
    }

    protected boolean assignMenus(List<ApprovalDetailEntity> menuAuths,Integer roleId) {
        // 删除历史

        sysRoleMenuRepo.remove(new LambdaQueryWrapper<SysRoleMenuEntity>().eq(SysRoleMenuEntity::getRoleId, roleId));
        if (menuAuths.size() == 0) {
            // 无需分配角色
            return true;
        }

        ArrayList<SysRoleMenuEntity> fileAuthEntities = new ArrayList<>();
        // 分配菜单权限
        for (ApprovalDetailEntity menuAuth : menuAuths) {
            SysRoleMenuEntity sysRoleMenu = new SysRoleMenuEntity();
            sysRoleMenu.setRoleId(Long.valueOf(menuAuth.getRoleId()));
            sysRoleMenu.setMenuId(Long.valueOf(menuAuth.getFolderOrMenu()));
            sysRoleMenu.setManageAuth(menuAuth.getAuthManage());
            fileAuthEntities.add(sysRoleMenu);
        }
        return sysRoleMenuRepo.saveBatch(fileAuthEntities);
    }

    protected boolean assignFolder(List<ApprovalDetailEntity> folderAuths,Integer roleId) {

        // 删除原有的知识库权限
        folderAuthRepo.remove(new LambdaQueryWrapper<FolderAuthEntity>().eq(FolderAuthEntity::getRoleId, roleId));
        if (folderAuths.size() == 0) {
            // 无需分配角色
            return true;
        }
        ArrayList<FolderAuthEntity> fileAuthEntities = new ArrayList<>();
        for (ApprovalDetailEntity folderAuth : folderAuths) {
            FolderAuthEntity folderAuthEntity = new FolderAuthEntity();
            folderAuthEntity.setRoleId(folderAuth.getRoleId());
            folderAuthEntity.setFolderId(folderAuth.getFolderOrMenu());
            fileAuthEntities.add(folderAuthEntity);
        }
       return folderAuthRepo.saveBatch(fileAuthEntities);
    }





}




