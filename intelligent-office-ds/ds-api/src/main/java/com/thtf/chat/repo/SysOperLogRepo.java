package com.thtf.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.chat.entity.SysOperLogEntity;

public interface SysOperLogRepo extends IService<SysOperLogEntity> {

    boolean save(SysOperLogEntity entity);
}
