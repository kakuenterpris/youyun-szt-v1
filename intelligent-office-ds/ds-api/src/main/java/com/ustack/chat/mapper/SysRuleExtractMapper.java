package com.ustack.chat.mapper;

import com.ustack.chat.entity.SysRuleExtractEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;

/**
* @author cheng
* @description 针对表【SYS_RULE_EXTRACT(知识规则提取配置)】的数据库操作Mapper
* @createDate 2025-05-15 17:47:15
* @Entity com.ustack.chat.entity.SysRuleExtractEntity
*/
public interface SysRuleExtractMapper extends BaseMapper<SysRuleExtractEntity> {

    /**
     * 根据规则提取 ID 查询关联表的数据个数
     *
     * @param ruleExtractId 规则提取 ID
     * @return 关联表的数据个数
     */
    Integer countRuleTagByRuleExtractId(@Param("ruleExtractId") Long ruleExtractId);

}




