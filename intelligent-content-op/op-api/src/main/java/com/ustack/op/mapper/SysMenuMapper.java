package com.ustack.op.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.op.entity.SysMenuEntity;


import java.util.List;

/**
* @author 86187
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Mapper
* @createDate 2025-04-15 18:33:50
* @Entity generator.domain.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenuEntity> {

    List<SysMenuEntity> getMenuByRoleId(Long RoleId);

}




