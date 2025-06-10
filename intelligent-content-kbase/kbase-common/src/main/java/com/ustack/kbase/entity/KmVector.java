package com.ustack.kbase.entity;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2025年04月23日
 */
@Data
public class KmVector {

    /**
     * 文件id
     */
    private String fileId = "";

    private String folderId = "";

    /**
     * 文件名称
     */
    private String fileName = "";

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

    private String userId = "";

    private String userName = "";

    /**
     * 所在一级部门名称
     */
    private String departmentName = "";

    private String departmentNum = "";

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

    private String edition = "";

    private String keywords = "";

    private Integer wordCount = 0;

    private Integer year;

    private Integer month;

    private Integer valid = 1;

    private String chunkId = "";
}
