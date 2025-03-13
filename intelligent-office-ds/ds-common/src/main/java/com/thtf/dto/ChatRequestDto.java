package com.thtf.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhangwei
 * @date 2025年02月18日
 */
@Data
public class ChatRequestDto {
    /**
     * 问答内容
     */
    private String question;

    /**
     * 会话id
     */
    private String conversationId;

    /**
     * 是否使用cnki向量库
     */
    private Boolean isUseCnki;

    /**
     * 是否使用个人向量库
     */
    private Boolean isUseCustom;
    /**
     * 是否使用同方向量库
     */
    private Boolean isUseTtkn;

    /**
     * 是否使用机构知识库
     */
    private Boolean isUseOrg;

    /**
     * 是否使用部门知识库
     */
    private Boolean isUseDept;

    /**
     * 是否联网
     */
    private Boolean networking;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 上传文件
     */
    private List<ChatFileRequestDto> files;
}
