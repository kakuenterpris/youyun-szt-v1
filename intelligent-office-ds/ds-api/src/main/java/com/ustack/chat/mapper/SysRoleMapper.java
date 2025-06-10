package com.ustack.chat.mapper;

import com.ustack.chat.entity.SysRoleEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 86187
* @description 针对表【sys_role(角色信息表)】的数据库操作Mapper
* @createDate 2025-04-15 18:33:50
* @Entity generator.domain.SysRole
*/
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRoleEntity> {

    List<SysRoleEntity> getRoleByUserId(Integer userId);
}




