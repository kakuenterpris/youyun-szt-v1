package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.access.dto.UserInfoDto;
import com.ustack.chat.dto.LogInfoDTO;
import com.ustack.chat.entity.SysOptLogEntity;
import com.ustack.chat.mapper.SysOptLogMapper;
import com.ustack.chat.repo.SysOptLogRepo;
import com.ustack.global.common.rest.RestResponse;
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
    public RestResponse getAuditLogs(Page<UserInfoDto> page,String query,String type) {
        Page<LogInfoDTO> auditLogs = baseMapper.getAuditLogs(page,query,type);
        return RestResponse.success(auditLogs);
    }
}




