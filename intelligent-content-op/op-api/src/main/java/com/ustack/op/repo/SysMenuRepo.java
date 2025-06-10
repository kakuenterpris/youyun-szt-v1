package com.ustack.op.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.entity.SysMenuEntity;

import java.util.Date;
import java.util.List;

/**
* @author 86187
* @description 针对表【sys_menu(菜单权限表)】的数据库操作Service
* @createDate 2025-04-15 18:33:50
*/
public interface SysMenuRepo extends IService<SysMenuEntity> {
    List<SysMenuEntity> getMenuByRoleId(Long RoleId);
//
//    List<SysMenuEntity> getUserMenu(String userId);
//
//    List<SysMenuEntity> getUserMenu(String userId, String authType);
//
//    List<MenuVO> listTree(String authType);
//
//    // 新增审计日志方法
//    void recordAuditLog(SysOperLogEntity logEntity);
//
//    // 获取审计日志
//    List<SysOperLogEntity> getAuditLogs(Date startTime, Date endTime);
//
//    //获取菜单列表
//    RestResponse pageList(Page<SysMenuEntity> page, SysMenuDto vo);
//
//    RestResponse updateByRoleId(SysMenuEntity menu);
//
//    List<MenuTreeNode> getMenuTree();
}
