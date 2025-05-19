package com.thtf.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.thtf.access.dto.SysRuleTagDto;
import com.thtf.chat.entity.SysRuleExtractEntity;
import com.thtf.chat.entity.SysRuleTagEntity;
import com.thtf.global.common.rest.RestResponse;

/**
* @author cheng
 * @description 针对表【SYS_RULE_Tag】的数据库操作Service
* @createDate 2025-05-15 17:58:23
*/
public interface SysRuleTagRepo extends IService<SysRuleTagEntity> {


    RestResponse pageList(Page<SysRuleTagEntity> page, SysRuleTagDto vo);

    RestResponse saveRuleTag(SysRuleTagEntity entity);


    RestResponse sortRuleTag(Long id, Boolean isUp);
}
