package com.ustack.chat.dto;

import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.chat.entity.SysRoleMenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
public class UpdateUserInfoDto extends BusUserInfoEntity {
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
