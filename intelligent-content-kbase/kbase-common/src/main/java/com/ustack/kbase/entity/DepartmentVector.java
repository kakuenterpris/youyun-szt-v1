package com.ustack.kbase.entity;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年03月26日
 */
@Data
public class DepartmentVector {

    /**
     * 部门名称
     */
    private String departmentName = "";

    /**
     * 部门代码
     */
    private String departmentCode = "";

    /**
     * 所在单位名称
     */
    private String companyName = "";

    /**
     * 所在单位代码
     */
    private String companyCode = "";

    /**
     * 文件名称
     */
    private String fileName = "";

    /**
     * 文件id
     */
    private String fileId = "";

    /**
     * 文件格式类型
     */
    private String fileType = "";

    /**
     * 文件内容类型
     */
    private String fileContentType = "";

    /**
     * 文件上传时间
     */
    private String uploadTime = "";

    /**
     * 文件存储路径
     */
    private String filePath = "";

    /**
     * 文件上传人id
     */
    private String uploadUserId = "";

    /**
     * 文件上传人部门
     */
    private String uploadUserDepartment = "";

    /**
     * 标题文本
     */
    private String title = "";

    /**
     * 章节文本
     */
    private String chapter = "";

    /**
     * 父段文本
     */
    private String sliceParent = "";

    /**
     * 子段文本
     */
    private String sliceChild = "";

    /**
     * 标题向量
     */
    private String titleVector = "";

    /**
     * 章节向量
     */
    private String chapterVector = "";

    /**
     * 父段向量
     */
    private String sliceParentVector = "";

    /**
     * 字段向量
     */
    private String sliceChildVector = "";

    /**
     * 版次
     */
    private String edition = "";

    private String keywords = "";

    private Integer wordCount = 0;
}
