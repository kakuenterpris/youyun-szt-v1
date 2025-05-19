package com.thtf.chat.mapper;

import com.thtf.chat.entity.SysRuleTagEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author cheng
 * @description 针对表【SYS_RULE_Tag】的数据库操作Mapper
* @createDate 2025-05-15 17:58:23
 * @Entity com.thtf.chat.entity.SysRuleTagEntity
*/
public interface SysRuleTagMapper extends BaseMapper<SysRuleTagEntity> {

    @Select("SELECT MAX(sort) FROM SYS_RULE_TAG")
    Integer selectMaxSort();
}




