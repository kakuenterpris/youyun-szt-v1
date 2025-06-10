package com.ustack.chat.mapper;

import com.ustack.chat.entity.SysRuleTagEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
* @author cheng
 * @description 针对表【SYS_RULE_Tag】的数据库操作Mapper
* @createDate 2025-05-15 17:58:23
 * @Entity com.ustack.chat.entity.SysRuleTagEntity
*/
public interface SysRuleTagMapper extends BaseMapper<SysRuleTagEntity> {

    @Select("SELECT NVL(MAX(sort), 0) FROM SYS_RULE_TAG")
    Integer selectMaxSort();
}




