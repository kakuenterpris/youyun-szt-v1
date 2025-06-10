package com.ustack.file.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.file.entity.SysOptLogEntity;

/**
* @author Liyingzheng
* @description 针对表【sys_opt_log(操作日志表)】的数据库操作Service
* @createDate 2025-04-22 16:04:46
*/
public interface SysOptLogRepo extends IService<SysOptLogEntity> {

    boolean saveLog(String operateContent, Long resourceId, Long parentId, String operateType, Integer code);
}
