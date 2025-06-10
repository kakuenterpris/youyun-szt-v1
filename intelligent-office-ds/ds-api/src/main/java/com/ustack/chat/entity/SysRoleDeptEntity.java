package com.ustack.chat.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 角色和部门关联 sys_role_dept
 *
 */

@TableName("sys_role_dept")
@Data
public class SysRoleDeptEntity {

    /** 角色ID */
    private Long roleId;

    /** 部门ID */
    private Long deptId;
}
