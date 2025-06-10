package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.SysOperLogEntity;

public interface SysOperLogRepo extends IService<SysOperLogEntity> {

    boolean save(SysOperLogEntity entity);
}
