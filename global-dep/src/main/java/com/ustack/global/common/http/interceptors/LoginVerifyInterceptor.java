package com.ustack.global.common.http.interceptors;

import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.consts.AuthConstants;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.exception.CustomException;
import com.ustack.global.common.properties.AuthConfigProperties;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RequestContext;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.AESUtil;
import com.ustack.global.common.utils.JsonUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: LoginVerifyInterceptor
 * @Date: 2023-12-15 10:47
 */
@Slf4j
public class LoginVerifyInterceptor implements HandlerInterceptor {

    private final AuthConfigProperties properties;
    private final RedisUtil redisUtil;
    public LoginVerifyInterceptor(AuthConfigProperties properties, RedisUtil redisUtil) {
        this.properties = properties;
        this.redisUtil = redisUtil;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String traceId = request.getHeader(AuthConstants.trace_id_key);
        traceId = StringUtils.isNotBlank(traceId) && !"undefined".equals(traceId) ? traceId : RandomStringUtils.randomAlphabetic(8);
        MDC.put(AuthConstants.trace_id_key, traceId);
        String token = getToken(request);
        String contextJson = request.getHeader(AuthConstants.context_key);
        // 通过feign 拦截器透传过来的数据
        if (StringUtils.isNotBlank(contextJson)){
            try {
                String str = AESUtil.decryptAuthInfo(contextJson);
                RequestContext context = JsonUtil.fromJson(str, RequestContext.class);
                if (Objects.nonNull(context)) {
                    ContextUtil.setContext(context);
                    log.info("当前用户:{}（{}）", ContextUtil.getUserName(), ContextUtil.getUserId());
                    return true;
                }
            } catch (Exception e) {
                MDC.clear();
                ContextUtil.clear();
                throw new CustomException(500, "用户信息解析异常");
            }
        }

        if (Objects.isNull(token)) {
            responseInvalidToken(response);
            MDC.clear();
            return false;
        } else {
            try {
                // todo 根据token获取人员信息，获取信息为空，同样返回无效token
                String userInfoStr = (String) redisUtil.get("token_" + token);
                if (StringUtils.isBlank(userInfoStr)){
                    responseRequestFail(response, DefaultErrorCode.INVALID_TOKEN);
                    return false;
                }
                SystemUser userInfo = JsonUtil.fromJson(userInfoStr, SystemUser.class);
                RequestContext context = RequestContext.builder().userInfo(userInfo)
                        .token(token)
                        .traceId(traceId)
                        .build();
                if (Objects.nonNull(context)) {
                    ContextUtil.setContext(context);
                    request.setAttribute(AuthConstants.context_key, JsonUtil.toJson(context));
                    log.info("当前用户:{}（{}）", ContextUtil.getUserName(), ContextUtil.getUserId());
                    return true;
                }
                return false;
            } catch (Exception var11) {
                log.error("登录拦截异常：", var11);
                MDC.clear();
                ContextUtil.clear();
                responseRequestFail(response, DefaultErrorCode.SYSTEM_INTERNAL_ERROR);
                return false;
            }
        }
    }


    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextUtil.clear();
        MDC.clear();
    }

    private static void responseInvalidToken(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JsonUtil.toJson(RestResponse.fail(DefaultErrorCode.INVALID_TOKEN)));
    }

    private static void responseRequestFail(HttpServletResponse response, DefaultErrorCode errorCode) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JsonUtil.toJson(RestResponse.fail(errorCode)));
    }

    private static void responseRequestFail(HttpServletResponse response, RestResponse res) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JsonUtil.toJson(res));
    }

    private String getToken(HttpServletRequest request){

        //        获取cookie 中的sessionId
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {  // 假设token存储在名为"token"的cookie中
                    System.out.println("cookie:" + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        // 从header取
        String fromHeader = request.getHeader(AuthConstants.token_key);
        if (StringUtils.isNotBlank(fromHeader)){
            return fromHeader;
        }
        // 从url param取值
        String fromParameter = request.getParameter(AuthConstants.token_key);
        if (StringUtils.isNotBlank(fromParameter)){
            return fromParameter;
        }
        return null;
    }
}
