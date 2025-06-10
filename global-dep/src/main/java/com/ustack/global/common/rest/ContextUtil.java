package com.ustack.global.common.rest;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.ustack.global.common.dto.SystemUser;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: ContextUtil
 * @Date: 2023-12-19 14:57
 */
public class ContextUtil {

    private static final TransmittableThreadLocal<RequestContext> cache = new TransmittableThreadLocal();

    public ContextUtil() {
    }

    public static void setContext(RequestContext context) {
        cache.set(context);
    }

    /**
     * 获取当前登录人id
     * @author linxin
     * @return String
     * @date 2025/2/20 19:01
     */
    public static String getUserId() {
        RequestContext requestContext = cache.get();
        if (!Objects.isNull(requestContext) && !Objects.isNull(requestContext.getUserInfo())) {
            return Objects.isNull(requestContext.getUserInfo()) ? "" : requestContext.getUserInfo().getUserId();
        } else {
            return "";
        }
    }

    /**
     * 获取当前登录人信息
     * @author linxin
     * @return SystemUser
     * @date 2025/2/20 19:00
     */
    public static SystemUser currentUser() {
        RequestContext requestContext = cache.get();
        return Objects.isNull(requestContext) ? null : requestContext.getUserInfo();
    }

    public static String getUserName() {
        RequestContext requestContext = cache.get();
        return !Objects.isNull(requestContext) && !Objects.isNull(requestContext.getUserInfo()) ? requestContext.getUserInfo().getUserName() : "";
    }

    /**
     * 判断当前人是数据创建人
     * @param: createUserId 数据库表的create_user_id
     * @author linxin
     * @return Boolean
     * @date 2024/10/17 9:54
     */
    public static Boolean currentIsCreator(String createUserId){
        return StringUtils.equals(createUserId, getUserId());
    }

    public static RequestContext context() {
        return cache.get();
    }

    public static void clear() {
        cache.remove();
    }
}
