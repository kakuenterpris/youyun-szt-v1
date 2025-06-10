package com.ustack.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ustack.chat.entity.LikeOrDislikeEntity;
import com.ustack.chat.entity.SysDeptEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author zhoufei
* @description 针对表【SysDeptEntity】的数据库操作Mapper
* @createDate 2025-03-24 15:29:16
* @Entity generator.entity.LikeOrDislikeEntity
*/
public interface SysDeptMapper extends BaseMapper<SysDeptEntity> {

    SysDeptEntity selectDeptById(Long deptId);

    int selectNormalChildrenDeptById(Long deptId);

    List<SysDeptEntity> selectChildrenDeptById(Long deptId);

    int updateDeptChildren(@Param("depts") List<SysDeptEntity> children);

    void updateDeptStatusNormal(Long[] deptIds);

    int hasChildByDeptId(Long deptId);

    int checkDeptExistUser(Long deptId);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @param deptCheckStrictly 部门树选择项是否关联显示
     * @return 选中部门列表
     */
    public List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);


}




