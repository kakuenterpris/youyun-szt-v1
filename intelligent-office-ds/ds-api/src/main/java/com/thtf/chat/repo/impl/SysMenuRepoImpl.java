package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.VO.MenuVO;
import com.thtf.chat.entity.SysMenuEntity;
import com.thtf.chat.entity.SysOperLogEntity;
import com.thtf.chat.repo.SysMenuRepo;
import com.thtf.chat.mapper.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Override
    public List<SysMenuEntity> getUserMenu(String userId, String authType) {
        return sysMenuMapper.getUserMenu(userId, authType);
    }


    @Override
    public List<MenuVO> listTree(String authType) {
        List<SysMenuEntity> menuList = lambdaQuery()
                .eq(SysMenuEntity::getStatus, 1)
                .eq(SysMenuEntity::getAuthType, authType)
                .orderByAsc(SysMenuEntity::getOrderNum)
                .list();
        if (CollectionUtils.isEmpty(menuList)) {
            return null;
        }
        return menuList.stream().filter(e -> Objects.equals(0L, e.getParentId())).map(e -> {
            MenuVO vo = new MenuVO();
            vo.setMenuId(e.getMenuId());
            vo.setMenuName(e.getMenuName());
            vo.setChildren(getChild(vo.getMenuId(), vo.getMenuName(), menuList));
            return vo;
        }).toList();
    }

    /**
     * 新增审计日志方法
     * @param logEntity
     */
    @Override
    public void recordAuditLog(SysOperLogEntity logEntity) {

    }

    /**
     * 获取审计日志
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public List<SysOperLogEntity> getAuditLogs(Date startTime, Date endTime) {
        return List.of();
    }


    /**
     * 获取子节点
     */
    protected List<MenuVO> getChild(Long id, String parentName, List<SysMenuEntity> sysResourceList) {
        // 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
        List<SysMenuEntity> childList = sysResourceList.stream().filter(e -> Objects.equals(id, e.getParentId())).toList();
        if (childList.isEmpty()) {
            // 没有子节点，返回一个空 List（递归退出）
            return null;
        }
        // 递归
        return childList.stream().map(e -> {
            MenuVO vo = new MenuVO();
            vo.setMenuId(e.getMenuId());
            vo.setMenuName(e.getMenuName());
            vo.setParentName(parentName);
            vo.setChildren(getChild(vo.getMenuId(), vo.getMenuName(), sysResourceList));
            return vo;
        }).toList();
    }


    /**
     * 获取所有菜单
     *
     * @param menu 是否菜单
     * @return 菜单列表
     */
    private List<SysMenuEntity> listAll(Boolean menu){
        List<SysMenuEntity> list = lambdaQuery().eq(SysMenuEntity::getStatus, 1)
                .ne(menu, SysMenuEntity::getMenuType, 3)
                .orderByAsc(SysMenuEntity::getOrderNum).list();
        return list;
    }



}




