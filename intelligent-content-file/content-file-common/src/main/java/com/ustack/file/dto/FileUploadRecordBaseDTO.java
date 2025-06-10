package com.ustack.file.dto;

import lombok.Data;

/**
 * @Description : 文件操作
 * @Author : LinXin
 * @ClassName : FileOperateDTO
 * @Date: 2021-03-24 13:43
 */
@Data
public class FileUploadRecordBaseDTO {


    private String guid;

    private String documentId;

    private String batch;

    private Long startDate;

    private Long endDate;

    private String editKey;

    private Boolean enableDownload;

}
