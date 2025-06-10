package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.SysLogininfor;
import com.ustack.chat.mapper.SysLogininforMapper;
import com.ustack.chat.repo.ISysLogininforRepo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhoufei
 * @description 针对表【sys_logininfor】的数据库操作Service实现
 * @createDate 2025-03-24 15:29:16
 */

@Service
public class ISysLogininforRepoImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforRepo {
    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        save(logininfor);
    }

    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        return list();
    }

    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        return deleteLogininforByIds(infoIds);
    }

    @Override
    public void cleanLogininfor() {
    }
}
