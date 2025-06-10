package com.ustack.file.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description : 接口返回文件上传信息
 * @Author : LinXin
 * @ClassName : FileUploadRecordDTO
 * @Date: 2021-03-24 10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRecordDTO {

    @JsonIgnore
    private Long id;

    private String guid;

    private String documentId;

    private String batch;

    /**
     * 首次同步文件返回的状态
     */
    private String indexStatus;

    @JsonIgnore
    private  String fileName;

    private String originName;

    @JsonIgnore
    private String path;

    private Long startDate;

    private Long endDate;

    @JsonIgnore
    private String previewPath;

    private String suffix;

    private BigDecimal size;

    private String md5;

    private Integer status;

    private String statusDesc;

    private Boolean enableDownload;

    private Boolean enablePreview;

    private Integer orderBy;

    private Date insertTime;

    private Boolean enableEdit;

    private String editKey;

    @JsonIgnore
    private Boolean deleted;

    private Date updateTime;

}
