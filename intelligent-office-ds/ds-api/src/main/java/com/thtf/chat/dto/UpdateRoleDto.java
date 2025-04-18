package com.thtf.chat.dto;

import com.thtf.chat.entity.SysRoleEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleDto extends SysRoleEntity {
    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
}
