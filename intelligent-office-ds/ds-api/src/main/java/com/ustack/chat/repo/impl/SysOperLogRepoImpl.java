package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.SysOperLog;
import com.ustack.chat.entity.SysOperLogEntity;
import com.ustack.chat.mapper.SysOperLogMapper;
import com.ustack.chat.repo.ISysOperLogRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysOperLogRepoImpl extends ServiceImpl<SysOperLogMapper, SysOperLogEntity> implements ISysOperLogRepo {



    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLogEntity operLog)
    {
        save(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLogEntity> selectOperLogList(SysOperLogEntity operLog)
    {
       return selectOperLogList(operLog);
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds)
    {
        return deleteOperLogByIds(operIds);
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId)
    {
        return selectOperLogById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
        ;
    }
}
