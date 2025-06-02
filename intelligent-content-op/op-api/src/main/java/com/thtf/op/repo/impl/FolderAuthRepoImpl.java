package com.thtf.op.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.thtf.op.entity.FolderAuthEntity;
import com.thtf.op.mapper.FolderAuthMapper;
import com.thtf.op.repo.FolderAuthRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86187
* @description 针对表【FOLDER_AUTH(文件夹权限表)】的数据库操作Service实现
* @createDate 2025-05-21 17:47:28
*/
@Service
public class FolderAuthRepoImpl extends ServiceImpl<FolderAuthMapper, FolderAuthEntity>
    implements FolderAuthRepo {

    @Override
    public List<FolderAuthEntity> listByRoleId(Long roleId) {
        LambdaQueryWrapper<FolderAuthEntity> listByRoleId = new LambdaQueryWrapper<>();
        listByRoleId.eq(FolderAuthEntity::getRoleId, roleId);
        return  this.list(listByRoleId);
    }
}




