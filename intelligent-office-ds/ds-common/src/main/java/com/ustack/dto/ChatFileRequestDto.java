package com.ustack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年02月21日
 */
@Data
public class ChatFileRequestDto {

    @Schema(name = "fileType",description = "文件类型")
    private String fileType;

    @Schema(name = "fileId",description = "文件id")
    private String fileId;

}
