package com.ustack.file.dto;

import lombok.Data;

/**
 * @author linxin
 * @Description : base64方式上传文件
 * @ClassName : FileUploadBase64DTO
 * @Date: 2022-10-20 08:59
 */
@Data
public class FileUploadBase64DTO {

    private String base64Str;

    private String path;

    private String fileOriginName;

    private String businessId;

    private String businessType;

}
