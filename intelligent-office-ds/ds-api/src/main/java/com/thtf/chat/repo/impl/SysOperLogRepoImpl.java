package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.SysOperLogEntity;
import com.thtf.chat.mapper.SysOperLogMapper;
import com.thtf.chat.repo.SysOperLogRepo;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogRepoImpl extends ServiceImpl<SysOperLogMapper,SysOperLogEntity> implements SysOperLogRepo {


    @Override
    public boolean save(SysOperLogEntity log) {
        return baseMapper.insert(log) > 0;
    }
}
