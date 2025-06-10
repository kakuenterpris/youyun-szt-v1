package com.ustack.kbase.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年03月26日
 */
@Data
@Schema(name = "企业实体")
public class CompanyVector {

    /**
     * 企业名称
     */
    @Schema(name = "companyName", description = "企业名称")
    private String companyName = "";

    /**
     * 企业代码
     */
    @Schema(name = "companyCode", description = "企业编码")
    private String companyCode = "";

    /**
     * 文件名称
     */
    @Schema(name = "fileName", description = "文件名称")
    private String fileName = "";

    /**
     * 文件id
     */
    @Schema(name = "fileId", description = "文件ID")
    private String fileId = "";

    /**
     * 文件格式类型
     */
    @Schema(name = "fileType", description = "文件类型")
    private String fileType = "";

    /**
     * 文件内容类型
     */
    @Schema(name = "fileContentType", description = "文件内容类型")
    private String fileContentType = "";

    /**
     * 文件上传时间
     */
    @Schema(name = "uploadTime", description = "文件上传时间")
    private String uploadTime = "";

    /**
     * 文件存储路径
     */
    @Schema(name = "filePath", description = "文件存储路径")
    private String filePath = "";

    /**
     * 文件上传人id
     */
    @Schema(name = "uploadUserId", description = "文件上传人id")
    private String uploadUserId = "";

    /**
     * 文件上传人部门
     */
    @Schema(name = "uploadUserDepartment", description = "文件上传人部门")
    private String uploadUserDepartment = "";

    /**
     * 标题文本
     */
    @Schema(name = "title", description = "标题文本")
    private String title = "";

    /**
     * 章节文本
     */
    @Schema(name = "chapter", description = "章节文本")
    private String chapter = "";

    /**
     * 父段文本
     */
    @Schema(name = "sliceParent", description = "父段文本")
    private String sliceParent = "";

    /**
     * 子段文本
     */
    @Schema(name = "sliceChild", description = "子段文本")
    private String sliceChild = "";

    /**
     * 标题向量
     */
    @Schema(name = "titleVector", description = "标题向量")
    private String titleVector = "";

    /**
     * 章节向量
     */
    @Schema(name = "chapterVector", description = "章节向量")
    private String chapterVector = "";

    /**
     * 父段向量
     */
    @Schema(name = "sliceParentVector", description = "父段向量")
    private String sliceParentVector = "";

    /**
     * 字段向量
     */
    @Schema(name = "sliceChildVector", description = "字段向量")
    private String sliceChildVector = "";

    private String edition = "";

    private String keywords = "";

    private Integer wordCount = 0;
}
