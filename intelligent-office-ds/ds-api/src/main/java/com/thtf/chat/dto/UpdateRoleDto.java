package com.thtf.chat.dto;

import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.entity.SysRoleMenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleDto extends SysRoleEntity {
    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;

    @Schema(description = "菜单ID列表")
    private List<SysRoleMenuEntity> menuAuth;

    @Schema(description = "知识库列表")
    private List<Long> dataAuth;
}
