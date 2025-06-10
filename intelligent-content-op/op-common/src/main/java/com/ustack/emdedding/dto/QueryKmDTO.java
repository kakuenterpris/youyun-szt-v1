package com.ustack.emdedding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年04月18日
 */
@Data
@Schema(name = "知识库查询实体")
public class QueryKmDTO {

    @Schema(name = "content", description = "查询内容", required = true)
    private String content;

    @Schema(name = "userId", description = "用户ID", required = true)
    private String userId;

    @Schema(name = "isQueryCustom", description = "是否查询个人知识库")
    private boolean isQueryCustom;

    @Schema(name = "isQueryDepartment", description = "是否查询部门知识库")
    private boolean isQueryDepartment;

    @Schema(name = "isQueryCompany", description = "是否查询企业知识库")
    private boolean isQueryCompany;
}
