package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.chat.entity.SysDeptEntity;
import com.ustack.chat.entity.TreeSelect;
import com.ustack.global.common.rest.RestResponse;

import java.util.List;

/**
* @author zhoufei
* @description 针对表【SysDeptEntity】的数据库操作Service
* @createDate 2025-03-24 15:29:16
*/
public interface SysDeptRepo extends IService<SysDeptEntity> {

    Page<SysDeptEntity> pageList(Page<SysDeptEntity> page, SysDeptEntity dictType,String role);

    List<SysDeptEntity> selectDeptList(SysDeptEntity sysDeptEntity);

    Boolean checkDeptDataScope(Long deptId);

    SysDeptEntity selectDeptById(Long deptId);

    boolean checkDeptNameUnique(SysDeptEntity deptEntity);

    int insertDept(SysDeptEntity deptEntity);

    int selectNormalChildrenDeptById(Long deptId);

    int updateDept(SysDeptEntity deptEntity);

    boolean hasChildByDeptId(Long deptId);

    boolean checkDeptExistUser(Long deptId);

    int deleteDeptById(Long deptId);

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    public List<TreeSelect> selectDeptTreeList(SysDeptEntity dept);

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<SysDeptEntity> buildDeptTree(List<SysDeptEntity> depts);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<SysDeptEntity> depts);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    public List<Long> selectDeptListByRoleId(Long roleId);



}
