package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ustack.chat.entity.ApprovalDetailEntity;
import com.ustack.chat.mapper.ApprovalDetailMapper;
import com.ustack.chat.repo.ApprovalDetailRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86187
* @description 针对表【APPROVAL_DETAIL】的数据库操作Service实现
* @createDate 2025-05-29 17:30:36
*/
@Service
public class ApprovalDetailRepoImpl extends ServiceImpl<ApprovalDetailMapper, ApprovalDetailEntity>
    implements ApprovalDetailRepo {

    @Override
    public List<ApprovalDetailEntity> getFolderAuthByApproveId(Long id) {
        List<ApprovalDetailEntity> list = this.list(new LambdaQueryWrapper<ApprovalDetailEntity>()
                                            .eq(ApprovalDetailEntity::getApprovalId, id)
                                            .eq(ApprovalDetailEntity::getAuthType, 1));

        return list;
    }

    @Override
    public List<ApprovalDetailEntity> getMenuAuthByApproveId(Long id) {
        List<ApprovalDetailEntity> list = this.list(new LambdaQueryWrapper<ApprovalDetailEntity>()
                .eq(ApprovalDetailEntity::getApprovalId, id)
                .eq(ApprovalDetailEntity::getAuthType, 2));
        return list;
    }
}




