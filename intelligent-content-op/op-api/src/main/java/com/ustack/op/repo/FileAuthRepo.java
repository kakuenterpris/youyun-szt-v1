package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.op.entity.FileAuthEntity;
import com.ustack.resource.dto.BusResourceMemberDTO;

import java.util.List;


/**
* @author 86187
* @description 针对表【FILE_AUTH(文件权限表)】的数据库操作Service
* @createDate 2025-05-20 18:56:18
*/
public interface FileAuthRepo extends IService<FileAuthEntity> {
// 根据用户ID获取文件ID列表
    List<FileAuthEntity> listFileIdByUser(String userId);

}
