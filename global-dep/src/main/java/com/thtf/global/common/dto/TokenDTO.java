package com.thtf.global.common.dto;

import lombok.Data;

/**
 * @Description: 用户信息
 * @author：linxin
 * @ClassName: UserInfoDTO
 * @Date: 2023-01-31 13:58
 */
@Data
public class TokenDTO {

    /**
     *
     */
    private String token;

    /**
     *
     */
    private String sessionId;

    /**
     * 会话超时时间
     */
    private Integer timeoutSecond;
}
