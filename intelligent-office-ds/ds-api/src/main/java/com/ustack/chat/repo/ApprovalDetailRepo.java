package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.ApprovalDetailEntity;

import java.util.List;

/**
* @author 86187
* @description 针对表【APPROVAL_DETAIL】的数据库操作Service
* @createDate 2025-05-29 17:30:36
*/
public interface ApprovalDetailRepo extends IService<ApprovalDetailEntity> {

    List<ApprovalDetailEntity> getFolderAuthByApproveId(Long id);

    List<ApprovalDetailEntity> getMenuAuthByApproveId(Long id);
}
