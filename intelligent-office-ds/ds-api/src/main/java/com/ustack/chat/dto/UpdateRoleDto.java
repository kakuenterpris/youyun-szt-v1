package com.ustack.chat.dto;

import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.SysRoleMenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleDto extends SysRoleEntity {

    @Schema(description = "菜单ID列表")
    private List<SysRoleMenuEntity> menuAuth;

    @Schema(description = "知识库列表")
    private List<Long> dataAuth;
}
