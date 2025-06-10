package com.ustack.global.common.feign;

import com.ustack.global.common.consts.AuthConstants;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RequestContext;
import com.ustack.global.common.utils.AESUtil;
import com.ustack.global.common.utils.JsonUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @Description: feign 请求头透传
 * @author：linxin
 * @ClassName: FeignRequestInterceptor
 * @Date: 2024-02-23 15:45
 */
@RequiredArgsConstructor
public class FeignRequestInterceptor implements RequestInterceptor {

    private final String appName;

    @Override
    public void apply(RequestTemplate requestTemplate) {
//        ServletRequestAttributes attributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //HttpServletRequest request = attributes.getRequest();
        RequestContext context = ContextUtil.context();
//        String headerTokenKey = request.getHeader(AuthConstants.token_key);
//        String traceId = MDC.get(AuthConstants.trace_id_key);
        if (Objects.nonNull(context)){
            requestTemplate.header(AuthConstants.token_key, context.getToken());
            requestTemplate.header(AuthConstants.trace_id_key, context.getTraceId());
            requestTemplate.header(AuthConstants.context_key, AESUtil.encryptAuthInfo(JsonUtil.toJson(context)));
        }
        requestTemplate.header(AuthConstants.req_origin_name, appName);
        requestTemplate.header(AuthConstants.request_type, "feign");
    }
}
