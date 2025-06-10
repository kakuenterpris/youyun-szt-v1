package com.ustack.emdedding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年04月18日
 */
@Data
@Schema(name = "知识库删除实体")
public class DeleteKmDTO {

    @Schema(name = "fileId", description = "文件id", required = true)
    private String fileId;

    @Schema(name = "userId", description = "用户ID", required = true)
    private String userId;

    @Schema(name = "isQueryCustom", description = "是否个人知识库")
    private boolean isQueryCustom;

    @Schema(name = "isQueryDepartment", description = "是否部门知识库")
    private boolean isQueryDepartment;

    @Schema(name = "isQueryCompany", description = "是否企业知识库")
    private boolean isQueryCompany;
}
