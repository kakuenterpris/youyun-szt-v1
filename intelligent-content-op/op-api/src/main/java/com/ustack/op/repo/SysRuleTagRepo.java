package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.op.entity.SysRuleTagEntity;

/**
* @author cheng
 * @description 针对表【SYS_RULE_Tag】的数据库操作Service
* @createDate 2025-05-15 17:58:23
*/
public interface SysRuleTagRepo extends IService<SysRuleTagEntity> {

    SysRuleTagEntity getById(Long id);
}
