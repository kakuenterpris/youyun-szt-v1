package com.thtf.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.thtf.global.common.utils.Linq;
import com.thtf.op.entity.BusResourceMemberEntity;
import com.thtf.op.entity.FileAuthEntity;
import com.thtf.op.mapper.FileAuthMapper;
import com.thtf.op.repo.FileAuthRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86187
* @description 针对表【FILE_AUTH(文件权限表)】的数据库操作Service实现
* @createDate 2025-05-20 18:56:18
*/
@Service
public class FileAuthRepoImpl extends ServiceImpl<FileAuthMapper, FileAuthEntity>
    implements FileAuthRepo {

    @Override
    public List<FileAuthEntity> listFileIdByUser(String userId) {
        List<FileAuthEntity> list = lambdaQuery()
                .eq(FileAuthEntity::getUserId, userId)
                .list();
        return list;

    }
}




