package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.access.dto.UserInfoDto;
import com.ustack.chat.dto.LogInfoDTO;
import com.ustack.chat.entity.SysOptLogEntity;
import com.ustack.global.common.rest.RestResponse;

/**
* @author 86187
* @description 针对表【SYS_OPT_LOG(操作日志表)】的数据库操作Service
* @createDate 2025-05-28 16:50:16
*/
public interface SysOptLogRepo extends IService<SysOptLogEntity> {

    RestResponse getAuditLogs(Page<UserInfoDto> page, String query,String type);
}
