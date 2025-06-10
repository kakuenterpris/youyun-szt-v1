package com.ustack.chat.repo.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.SysDeptEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.TreeSelect;
import com.ustack.chat.mapper.SysDeptMapper;
import com.ustack.chat.mapper.SysRoleMapper;
import com.ustack.chat.repo.SysDeptRepo;
import com.ustack.chat.repo.SysRoleRepo;
import com.ustack.chat.utils.StringUtils;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author
 * @description 针对表【sys_dept】的数据库操作Service实现
 * @createDate 2025-05-17 14:43:43
 */
@Service
public class SysDeptRepoImpl extends ServiceImpl<SysDeptMapper, SysDeptEntity> implements SysDeptRepo {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    private SysRoleMapper sysRoleMapper;


    @Override
    public Page<SysDeptEntity> pageList(Page<SysDeptEntity> page, SysDeptEntity deptEntity,String role) {

        LambdaQueryWrapper<SysDeptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptEntity::getDelFlag, "0");
        queryWrapper.eq(SysDeptEntity::getDeptId, deptEntity.getDeptId());
        queryWrapper.eq(SysDeptEntity::getParentId, deptEntity.getParentId());
        queryWrapper.like(SysDeptEntity::getDeptName, deptEntity.getDeptName());
        queryWrapper.eq(SysDeptEntity::getStatus, deptEntity.getStatus());
//        if(role.equals("admin")){
//
//        } else if (role.equals("user")) {
//            queryWrapper.eq(SysDeptEntity::getDeptId, deptEntity.getDeptId());
//        } else if (role.equals("guest")) {
//            queryWrapper.eq(SysDeptEntity::getDeptId, deptEntity.getDeptId());
//        } else {
//            queryWrapper.eq(SysDeptEntity::getDeptId, deptEntity.getDeptId());
//
//        }

        queryWrapper.orderBy(true, true, SysDeptEntity::getParentId, SysDeptEntity::getOrderNum);
        return page(page, queryWrapper);
    }


    /**
     * 查询部门管理数据
     *
     * @param deptEntity 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptEntity> selectDeptList(SysDeptEntity deptEntity) {
        LambdaQueryWrapper<SysDeptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptEntity::getDelFlag, "0");
        queryWrapper.eq(SysDeptEntity::getDeptId, deptEntity.getDeptId());
        queryWrapper.eq(SysDeptEntity::getParentId, deptEntity.getParentId());
        queryWrapper.like(SysDeptEntity::getDeptName, deptEntity.getDeptName());
        queryWrapper.eq(SysDeptEntity::getStatus, deptEntity.getStatus());
        queryWrapper.orderBy(true, true, SysDeptEntity::getParentId, SysDeptEntity::getOrderNum);
        return list(queryWrapper);
    }

    /**
     * 数据权限校验部门是否有数据权限
     * @param deptId
     */
    // todo
    @Override
    public Boolean checkDeptDataScope(Long deptId) {
        SystemUser systemUser = ContextUtil.currentUser();
        systemUser.getUserId();
//        if(!isAdmin && StringUtils.isNotNull(deptId)){
        if(StringUtils.isNotNull(deptId)){
            SysDeptEntity dept = new SysDeptEntity();
            dept.setDeptId(deptId);
            List<SysDeptEntity> depts = selectDeptList(dept);
            if(StringUtils.isEmpty(depts)){
                return false;
            }
        }
        return true;
    }


    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Override
    public SysDeptEntity selectDeptById(Long deptId) {
        SysDeptEntity deptEntity = sysDeptMapper.selectDeptById(deptId);
        return deptEntity;
    }


    /**
     * 校验部门名称是否唯一
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDeptEntity deptEntity) {
        Long deptId = deptEntity.getDeptId() == null ? -1L : deptEntity.getDeptId();

        LambdaQueryWrapper<SysDeptEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDeptEntity::getDelFlag, "0");
        queryWrapper.eq(SysDeptEntity::getDeptName, deptEntity.getDeptName());
        queryWrapper.eq(SysDeptEntity::getParentId, deptEntity.getDeptName());
        SysDeptEntity sysDeptEntity = getOne(queryWrapper);
        if (sysDeptEntity != null && sysDeptEntity.getDeptId().longValue() != deptId.longValue()) {
            return false;
        }
        return false;
    }


    /**
     * 新增保存部门信息
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(SysDeptEntity deptEntity) {
        SysDeptEntity parentDept = selectDeptById(deptEntity.getParentId());
        if (parentDept != null && !parentDept.getStatus().equals("0")) {
            return -1;
        }
        deptEntity.setAncestors(parentDept.getAncestors() + "," + deptEntity.getParentId());
        boolean save = save(deptEntity);
        if (save) {
            return 1;
        }
        return -1;
    }


    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return sysDeptMapper.selectNormalChildrenDeptById(deptId);
    }


    /**
     * 修改保存部门信息
     *
     * @param deptEntity 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(SysDeptEntity deptEntity) {
        SysDeptEntity newParentDept = selectDeptById(deptEntity.getParentId());
        SysDeptEntity oldDept = selectDeptById(deptEntity.getDeptId());
        if (newParentDept!=null && oldDept!=null) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            deptEntity.setAncestors(newAncestors);
             updateDeptChildren(deptEntity.getDeptId(), newAncestors, oldAncestors);
        }
        LambdaQueryWrapper<SysDeptEntity> queryWrapper = new LambdaQueryWrapper<>();

        Boolean result = lambdaUpdate()
                .eq(SysDeptEntity::getDeptId, deptEntity.getDeptId())
                .update(deptEntity);
        if (deptEntity.getStatus().equals("0") && StringUtils.isNotEmpty(deptEntity.getAncestors())
                && !deptEntity.getAncestors().equals("0")) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(deptEntity);
        }

        return 1;
    }


    /**
     * 修改该部门的父级部门状态
     *
     * @param deptEntity 当前部门
     */
    private void updateParentDeptStatusNormal(SysDeptEntity deptEntity) {

        String ancestors = deptEntity.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);
        sysDeptMapper.updateDeptStatusNormal(deptIds);
    }


    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        int result = sysDeptMapper.hasChildByDeptId(deptId);
        return result>0;
    }


    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = sysDeptMapper.checkDeptExistUser(deptId);
        return result>0;
    }


    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public int deleteDeptById(Long deptId) {
        boolean update = lambdaUpdate().set(SysDeptEntity::getDelFlag, "2")
                .eq(SysDeptEntity::getDeptId, deptId)
                .update();
        return update ? 1 : -1;
    }

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<TreeSelect> selectDeptTreeList(SysDeptEntity dept) {
        List<SysDeptEntity> sysDeptEntities = selectDeptList(dept);
        return buildDeptTreeSelect(sysDeptEntities);
    }


    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<SysDeptEntity> buildDeptTree(List<SysDeptEntity> depts) {
        List<SysDeptEntity> returnList = new ArrayList<SysDeptEntity>();
        List<Long> tempList = depts.stream().map(SysDeptEntity::getDeptId).collect(Collectors.toList());
        for (SysDeptEntity dept : depts)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId()))
            {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = depts;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<SysDeptEntity> depts) {
        List<SysDeptEntity> deptTrees = buildDeptTree(depts);
        return deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleEntity::getRoleId, roleId);
        SysRoleEntity roleEntity = sysRoleMapper.selectOne(queryWrapper);
//        return sysDeptMapper.selectDeptListByRoleId(roleId, roleEntity.isDeptCheckStrictly());
        // todo
        return sysDeptMapper.selectDeptListByRoleId(roleId, false);
    }


    /**
     * 修改子元素关系
     *
     * @param deptId 被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors)
    {
        List<SysDeptEntity> children = sysDeptMapper.selectChildrenDeptById(deptId);
        for (SysDeptEntity child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            sysDeptMapper.updateDeptChildren(children);
        }
    }




    /**
     * 递归列表
     */
    private void recursionFn(List<SysDeptEntity> list, SysDeptEntity t)
    {
        // 得到子节点列表
        List<SysDeptEntity> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDeptEntity tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDeptEntity> getChildList(List<SysDeptEntity> list, SysDeptEntity t)
    {
        List<SysDeptEntity> tlist = new ArrayList<SysDeptEntity>();
        Iterator<SysDeptEntity> it = list.iterator();
        while (it.hasNext())
        {
            SysDeptEntity n = (SysDeptEntity) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getDeptId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDeptEntity> list, SysDeptEntity t)
    {
        return getChildList(list, t).size() > 0;
    }
}
