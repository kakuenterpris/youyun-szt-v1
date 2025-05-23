package com.thtf.op.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.thtf.global.common.rest.RestResponse;

import com.thtf.op.entity.SysUserRoleEntity;
import com.thtf.op.mapper.SysUserRoleMapper;
import com.thtf.op.repo.SysUserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 86187
* @description 针对表【sys_user_role(用户和角色关联表)】的数据库操作Service实现
* @createDate 2025-04-15 18:33:50
*/
@Service
public class SysUserRoleRepoImpl extends ServiceImpl<SysUserRoleMapper, SysUserRoleEntity>
    implements SysUserRoleRepo {

}




