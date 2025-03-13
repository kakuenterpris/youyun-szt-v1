package com.thtf.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年02月21日
 */
@Data
public class ChatFileRequestDto {

    private String fileType;

    private String fileId;

}
