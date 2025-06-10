package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.access.dto.SysMenuDto;
import com.ustack.access.dto.SysRoleDto;
import com.ustack.chat.VO.MenuVO;
import com.ustack.chat.dto.MenuTreeNode;
import com.ustack.chat.dto.UpdateRoleDto;
import com.ustack.chat.entity.SysMenuEntity;
import com.ustack.chat.entity.SysOperLogEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.repo.SysMenuRepo;
import com.ustack.chat.mapper.SysMenuMapper;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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


    public RestResponse deleteMenu(Integer menuId) {
        try {
            SysMenuEntity menu = getById(menuId);
            if(menu.getChildren() != null && !menu.getChildren().isEmpty()){
                return RestResponse.success("删除菜单失败，包含子菜单不能删除");
            }
            removeById(menuId);
            return RestResponse.success("删除菜单成功");
        }catch (Exception e) {
            log.error("删除菜单失败", e);
            return RestResponse.error("删除菜单失败");
        }
    }


    @Override
    public RestResponse pageList(Page<SysMenuEntity> page, SysMenuDto vo) {
        try {
            LambdaQueryWrapper<SysMenuEntity> menuQuery = new LambdaQueryWrapper<>();
            menuQuery.eq(vo.getMenuName() != null, SysMenuEntity::getMenuName, vo.getMenuName());
            menuQuery.eq(vo.getStatus() != null, SysMenuEntity::getStatus, vo.getStatus());
            return RestResponse.success(this.page(page, menuQuery));
        }catch (Exception e){
            e.printStackTrace();
            return RestResponse.error("查询失败");
        }
    }

    @Override
    public RestResponse updateByRoleId(SysMenuEntity menu) {
        try {
            this.updateById(menu);
        }catch (Exception e){
            return RestResponse.fail(1004, "修改失败！" + e.getMessage());
        }
        return RestResponse.success("修改成功");
    }


    /**
     * 获取所有菜单树
     *
     * @return 菜单树
     */
    @Override
    public List<MenuTreeNode> getMenuTree() {
        List<SysMenuEntity> allMenus = sysMenuMapper.selectList(new QueryWrapper());
        return buildTree(allMenus);
    }


    private List<MenuTreeNode> buildTree(List<SysMenuEntity> menus) {
        // 使用LinkedHashMap保持顺序
        Map<Long, MenuTreeNode> nodeMap = new LinkedHashMap<>();

        // 第一次遍历：创建所有节点并缓存
        for (SysMenuEntity menu : menus) {
            MenuTreeNode node = new MenuTreeNode();
            node.setId(menu.getMenuId());
            node.setName(menu.getMenuName());
            node.setParentId(menu.getParentId());
            node.setPath(menu.getPath());
            node.setIcon(menu.getIcon());
            node.setChildren(new ArrayList<>()); // 初始化空子节点
            nodeMap.put(menu.getMenuId(), node);
        }

        // 第二次遍历：构建树形结构
        List<MenuTreeNode> roots = new ArrayList<>();
        for (MenuTreeNode node : nodeMap.values()) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                roots.add(node);
            } else {
                MenuTreeNode parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        // 按sort字段排序（假设SysMenu有sort字段）
        sortTree(roots);
        return roots;
    }


    // 递归排序方法
    private void sortTree(List<MenuTreeNode> nodes) {
        if (nodes == null) return;

        // 当前层排序
//        nodes.sort(Comparator.comparingInt(MenuTreeNode::getSort));

        nodes.sort(Comparator.comparing(
                MenuTreeNode::getSort,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        // 递归排序子节点
        for (MenuTreeNode node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                sortTree(node.getChildren());
            }
        }
    }

}




