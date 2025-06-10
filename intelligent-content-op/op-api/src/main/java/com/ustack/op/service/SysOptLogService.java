package com.ustack.op.service;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.resource.dto.SystemLogDTO;

/**
* @author Liyingzheng
* @description 针对表【sys_opt_log(操作日志表)】的数据库操作Service
* @createDate 2025-04-22 16:04:46
*/
public interface SysOptLogService {

    RestResponse get(SystemLogDTO dto);
}
