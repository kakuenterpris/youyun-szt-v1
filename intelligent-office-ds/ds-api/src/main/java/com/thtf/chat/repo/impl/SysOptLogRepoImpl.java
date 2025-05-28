package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.chat.dto.LogInfoDTO;
import com.thtf.chat.entity.SysOptLogEntity;
import com.thtf.chat.mapper.SysOptLogMapper;
import com.thtf.chat.repo.SysOptLogRepo;
import com.thtf.global.common.rest.RestResponse;
import org.springframework.stereotype.Service;

/**
* @author 86187
* @description 针对表【SYS_OPT_LOG(操作日志表)】的数据库操作Service实现
* @createDate 2025-05-28 16:50:16
*/
@Service
public class SysOptLogRepoImpl extends ServiceImpl<SysOptLogMapper, SysOptLogEntity>
    implements SysOptLogRepo {


    @Override
    public RestResponse getAuditLogs(Page<UserInfoDto> page) {
        Page<LogInfoDTO> auditLogs = baseMapper.getAuditLogs(page);
        return RestResponse.success(auditLogs);
    }
}




