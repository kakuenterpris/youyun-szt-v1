package com.ustack.access.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysRoleDto {

    List<Long> roleIds;
//    角色名称
    private String roleName;
//    角色状态
    private String status;
}
