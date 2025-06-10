package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.SysMenuEntity;
import com.ustack.op.mapper.SysMenuMapper;
import com.ustack.op.repo.SysMenuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
* @author 86187
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysMenuRepoImpl extends ServiceImpl<SysMenuMapper, SysMenuEntity>
    implements SysMenuRepo {


    @Override
    public List<SysMenuEntity> getMenuByRoleId(Long RoleId) {
        return baseMapper.getMenuByRoleId(RoleId);
    }
}




