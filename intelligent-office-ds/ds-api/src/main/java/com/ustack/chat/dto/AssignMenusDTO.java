package com.ustack.chat.dto;

import com.ustack.chat.entity.SysRoleMenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssignMenusDTO {
    @Schema(description = "菜单ID列表")
    @NotEmpty
    private List<Long> menuIds;

    @Schema(description = "角色ID列表")
    @NotEmpty
    private List<Long> roleIds;

    @Schema(description = "用户ID列表")
    @NotEmpty
    private List<SysRoleMenuEntity> menuAuth;
}
