package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.SysRuleExtractEntity;
import com.ustack.op.mapper.SysRuleExtractMapper;
import com.ustack.op.repo.SysRuleExtractRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* @author cheng
 * @description 针对表【SYS_RULE_EXTRACT(知识规则提取配置)】的数据库操作Service实现
* @createDate 2025-05-15 17:47:15
*/
@Service
public class SysRuleExtractRepoImpl extends ServiceImpl<SysRuleExtractMapper, SysRuleExtractEntity>
    implements SysRuleExtractRepo {

    @Autowired
    private SysRuleExtractMapper sysRuleExtractMapper;


}




