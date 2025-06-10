package com.ustack.global.common.rest;

import com.ustack.global.common.dto.SystemUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: RequestContext
 * @Date: 2023-12-19 14:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestContext {

    /**
     * 当前用户token
     */
    private String token;
    /**
     * 客户端ID
     */
    //private String clientId;
    /**
     * 链路ID
     */
    private String traceId;

    /**
     * 用户信息
     */
    private SystemUser userInfo;

}
