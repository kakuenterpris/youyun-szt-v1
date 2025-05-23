package com.thtf.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.op.entity.SysRuleTagEntity;
import com.thtf.op.mapper.SysRuleTagMapper;
import com.thtf.op.repo.SysRuleTagRepo;
import org.springframework.stereotype.Service;

/**
* @author cheng
 * @description 针对表【SYS_RULE_Tag】的数据库操作Service实现
* @createDate 2025-05-15 17:58:23
*/
@Service
public class SysRuleTagRepoImpl extends ServiceImpl<SysRuleTagMapper, SysRuleTagEntity>
    implements SysRuleTagRepo {

    @Override
    public SysRuleTagEntity getById(Long id) {
        return baseMapper.selectById(id);
    }
}




