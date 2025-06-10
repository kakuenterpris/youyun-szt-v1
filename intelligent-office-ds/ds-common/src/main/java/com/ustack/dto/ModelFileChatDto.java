package com.ustack.dto;

/**
 * @author zhangwei
 * @date 2025年02月18日
 */

import lombok.Data;

/**
 *
 */
@Data
public class ModelFileChatDto {
    private String type = "document";

    private String transfer_method = "local_file";

    private String upload_file_id;
}
