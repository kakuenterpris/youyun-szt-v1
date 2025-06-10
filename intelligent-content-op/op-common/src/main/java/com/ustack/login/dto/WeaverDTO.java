package com.ustack.login.dto;

import lombok.Data;

/**
 * @Description: 泛微登录
 * @author：qkh
 * @ClassName: WeaverDTO
 * @Date: 2025-05-12 13:58
 */
@Data
public class WeaverDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 流程类型id
     */
    private String workflowId;

    /**
     * ecology系统发放的授权许可证(appid)
     */
    private String appId;

    /**
     * 泛微token
     */
    private String token;

    /**
     * 加密后用户id
     */
    private String encryptUserid;

    /**
     * 流程鉴权结果：true(表示有权限创建流程，false则无权限)
     */
    private Boolean existPermission;
}
