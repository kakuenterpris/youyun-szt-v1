package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ustack.access.dto.SysMenuDto;
import com.ustack.access.dto.SysRoleDto;
import com.ustack.chat.VO.MenuVO;
import com.ustack.chat.dto.MenuTreeNode;
import com.ustack.chat.dto.UpdateRoleDto;
import com.ustack.chat.entity.SysMenuEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.SysOperLogEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.global.common.rest.RestResponse;

import java.util.Date;
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

    List<SysMenuEntity> getUserMenu(String userId, String authType);

    List<MenuVO> listTree(String authType);

    // 新增审计日志方法
    void recordAuditLog(SysOperLogEntity logEntity);

    // 获取审计日志
    List<SysOperLogEntity> getAuditLogs(Date startTime, Date endTime);

    //获取菜单列表
    RestResponse pageList(Page<SysMenuEntity> page, SysMenuDto vo);

    RestResponse updateByRoleId(SysMenuEntity menu);

    List<MenuTreeNode> getMenuTree();
}
