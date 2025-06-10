package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.Linq;
import com.ustack.op.entity.SysRoleEntity;
import com.ustack.op.mapper.SysRoleMapper;
import com.ustack.op.repo.SysRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 86187
* @description 针对表【sys_role(角色信息表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysRoleRepoImpl extends ServiceImpl<SysRoleMapper, SysRoleEntity>
    implements SysRoleRepo {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Override
    public List<SysRoleEntity> getRoleByUserId(Integer userId) {
        return sysRoleMapper.getRoleByUserId(userId);
    }
}




