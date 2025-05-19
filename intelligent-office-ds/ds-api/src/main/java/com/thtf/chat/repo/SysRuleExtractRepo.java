package com.thtf.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.access.dto.SysRoleDto;
import com.thtf.access.dto.SysRuleExtractDto;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.entity.SysRuleExtractEntity;
import com.thtf.global.common.rest.RestResponse;

/**
* @author cheng
 * @description 针对表【SYS_RULE_EXTRACT(知识规则提取配置)】的数据库操作Service
* @createDate 2025-05-15 17:47:15
*/
public interface SysRuleExtractRepo extends IService<SysRuleExtractEntity> {

    RestResponse pageList(Page<SysRuleExtractEntity> page, SysRuleExtractDto vo);

    RestResponse saveRuleExtract(SysRuleExtractEntity entity);
}
