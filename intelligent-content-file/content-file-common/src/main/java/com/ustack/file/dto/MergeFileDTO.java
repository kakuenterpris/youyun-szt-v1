package com.ustack.file.dto;

import lombok.Data;

/**
 * @author linxin
 * @Description : 文件合并参数
 * @ClassName : MergeFileDTO
 * @Date: 2022-06-27 15:31
 */
@Data
public class MergeFileDTO {
    /**
     * 文件uuid
     */

    private String uuid;
    /**
     * 文件名称
     */

    private String fileName;
    /**
     * 文件md5
     */

    private String fileMd5;
    /**
     * 文件 文件保存路径 （默认是 '/yyyy/MM/dd/'）
     */

    private String path;
    /**
     * 文件是否可预览
     */

    private Boolean enablePreview;
    /**
     * 文件是否可下载
     */

    private Boolean enableDownload;
}
