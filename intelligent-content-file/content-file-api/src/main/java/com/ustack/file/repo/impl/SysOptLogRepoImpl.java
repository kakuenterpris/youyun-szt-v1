package com.ustack.file.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.file.entity.SysOptLogEntity;
import com.ustack.file.mapper.SysOptLogMapper;
import com.ustack.file.repo.SysOptLogRepo;
import org.springframework.stereotype.Service;

/**
* @author Liyingzheng
* @description 针对表【sys_opt_log(操作日志表)】的数据库操作Service实现
* @createDate 2025-04-22 16:04:46
*/
@Service
public class SysOptLogRepoImpl extends ServiceImpl<SysOptLogMapper, SysOptLogEntity>
    implements SysOptLogRepo {

    @Override
    public boolean saveLog(String operateContent, Long resourceId, Long parentId, String operateType, Integer code) {
        SysOptLogEntity sysOptLogEntity = SysOptLogEntity.builder()
                .resourceId(resourceId)
                .parentId(parentId)
                .operateType(operateType)
                .operateContent(operateContent)
                .fileType(code)
                .build();
        return this.save(sysOptLogEntity);
    }
}




