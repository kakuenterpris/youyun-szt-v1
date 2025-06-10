package com.ustack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年02月18日
 */
@Data
@Schema(name = "对话交互实体")
public class ChatRequestDto {
    /**
     * 问答内容
     */
    @Schema(name = "question", description = "问答内容")
    private String question;

    /**
     * 会话id
     */
    @Schema(name = "conversationId", description = "会话id")
    private String conversationId;

    /**
     * 是否使用个人向量库
     */
    @Schema(name = "isUseCustom", description = "是否使用个人向量库（是：true,否：false）")
    private Boolean isUseCustom;
    /**
     * 是否使用企业向量库
     */
    @Schema(name = "isUseTtkn", description = "否使用企业向量库（是：true,否：false）")
    private Boolean isUseTtkn;

    /**
     * 是否使用机构知识库
     */
    @Schema(name = "isUseOrg", description = "是否使用机构知识库（是：true,否：false）")
    private Boolean isUseOrg;

    /**
     * 是否使用部门知识库
     */
    @Schema(name = "isUseDept", description = "是否使用部门知识库（是：true,否：false）")
    private Boolean isUseDept;

    /**
     * 是否联网
     */
    @Schema(name = "networking", description = "是否联网（是：true,否：false）")
    private Boolean networking;

    /**
     * 场景类型
     */
    @Schema(name = "sceneType", description = "场景类型")
    private String sceneType;

    /**
     * 上传文件
     */
    @Schema(name = "files", description = "上传文件（目前最多5个）")
    private List<ChatFileRequestDto> files;
}
