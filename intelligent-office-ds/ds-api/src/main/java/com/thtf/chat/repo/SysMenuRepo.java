package com.thtf.chat.repo;

import com.thtf.chat.entity.SysMenuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 86187
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service
* @createDate 2025-04-15 18:33:50
*/
public interface SysMenuRepo extends IService<SysMenuEntity> {
//    根据用户id查看菜单
    List<SysMenuEntity> getMenuByRoleId(Long RoleId);

    List<SysMenuEntity> getUserMenu(String userId);
}
