package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.dto.ApproveDTO;
import com.ustack.chat.entity.SysAuthApprovalEntity;
import com.ustack.global.common.rest.RestResponse;

/**
* @author 86187
* @description 针对表【SYS_AUTH_APPROVAL(系统审批表)】的数据库操作Service
* @createDate 2025-05-29 14:02:09
*/
public interface SysAuthApprovalRepo extends IService<SysAuthApprovalEntity> {

    RestResponse approveDispose(ApproveDTO approveDTO);

    RestResponse approveList(Page page);

}
