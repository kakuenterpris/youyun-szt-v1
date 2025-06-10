package com.ustack.emdedding.dto;

import lombok.Data;

import java.math.BigDecimal;


/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@Data
public class FileUploadRecordDTO {

    private Long id;

    private String guid;

    private String indexStatus;

    private  String fileName;

    private String originName;

    private String path;

    private String previewPath;

    private String suffix;

    private BigDecimal size;

    private Integer status;
}
