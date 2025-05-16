package com.thtf.chat.dto;

import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.entity.SysRoleMenuEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
@Data
public class UpdateUserInfoDto extends BusUserInfoEntity {
    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
