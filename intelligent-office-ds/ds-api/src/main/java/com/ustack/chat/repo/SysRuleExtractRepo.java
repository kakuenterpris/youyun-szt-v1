package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.access.dto.SysRoleDto;
import com.ustack.access.dto.SysRuleExtractDto;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.SysRuleExtractEntity;
import com.ustack.global.common.rest.RestResponse;

/**
* @author cheng
 * @description 针对表【SYS_RULE_EXTRACT(知识规则提取配置)】的数据库操作Service
* @createDate 2025-05-15 17:47:15
*/
public interface SysRuleExtractRepo extends IService<SysRuleExtractEntity> {

    RestResponse pageList(Page<SysRuleExtractEntity> page, SysRuleExtractDto vo);

    RestResponse saveRuleExtract(SysRuleExtractEntity entity);

    RestResponse list(SysRuleExtractDto dto);

    void setRuleExtract(SysRuleExtractDto dto);
}
