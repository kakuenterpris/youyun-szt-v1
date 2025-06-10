package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.op.entity.FolderAuthEntity;

import java.util.List;

/**
* @author 86187
* @description 针对表【FOLDER_AUTH(文件夹权限表)】的数据库操作Service
* @createDate 2025-05-21 17:47:28
*/
public interface FolderAuthRepo extends IService<FolderAuthEntity> {

    List<FolderAuthEntity> listByRoleId(Long roleId);
}
