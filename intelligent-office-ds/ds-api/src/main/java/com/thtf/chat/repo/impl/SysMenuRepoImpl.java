package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.SysMenuEntity;
import com.thtf.chat.repo.SysMenuRepo;
import com.thtf.chat.mapper.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86187
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysMenuRepoImpl extends ServiceImpl<SysMenuMapper, SysMenuEntity>
    implements SysMenuRepo {
    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenuEntity> getMenuByRoleId(Long RoleId) {
        return sysMenuMapper.getMenuByRoleId(RoleId);
    }

    @Override
    public List<SysMenuEntity> getUserMenu(String userId) {
        return sysMenuMapper.getUserMenu(userId);
    }
}




