package com.ustack.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linxin
 * @Description : 老数据根据 folder path 复制文件到文档表
 * @ClassName : CopyFileByPathDTO
 * @Date: 2022-07-07 09:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CopyFileByPathDTO {

    private String fileOriginName;

    private String fileName;

    private String filePath;

    private Boolean enableDownload;

    private String documentId;

    private String businessType;

}
