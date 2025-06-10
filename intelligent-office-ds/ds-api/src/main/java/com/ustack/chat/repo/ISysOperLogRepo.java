package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.chat.entity.SysOperLog;
import com.ustack.chat.entity.SysOperLogEntity;

import java.util.List;

public interface ISysOperLogRepo extends IService<SysOperLogEntity> {


    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    public void insertOperlog(SysOperLogEntity operLog);

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<SysOperLogEntity> selectOperLogList(SysOperLogEntity operLog);

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public SysOperLog selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();
}
