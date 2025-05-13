package com.thtf.resource.param;

import com.thtf.global.common.validation.ValidGroup;
import com.thtf.resource.dto.BusResourceFileDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaveFileParam {

    /**
     * 文件夹ID
     */
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, message = "上级文件夹不能为空")
    private Integer folderId;

    /**
     * 文件
     */
    private List<BusResourceFileDTO> fileList;
}
