package com.ustack.chat.mapper;

import com.ustack.chat.entity.SysMenuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 86187
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Mapper
* @createDate 2025-04-15 18:33:50
* @Entity generator.domain.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenuEntity> {

    List<SysMenuEntity> getMenuByRoleId(Long RoleId);

    List<SysMenuEntity> getUserMenu(String userId);

    List<SysMenuEntity> getUserMenu(String userId, String authType);
}




