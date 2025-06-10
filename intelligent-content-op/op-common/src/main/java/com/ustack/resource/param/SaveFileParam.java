package com.ustack.resource.param;

import com.ustack.global.common.validation.ValidGroup;
import com.ustack.resource.dto.BusResourceFileDTO;
import com.ustack.resource.dto.BusResourceFolderDTO;
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

    /**
     * 文件
     */
    private List<BusResourceFolderDTO> folderList;
}
