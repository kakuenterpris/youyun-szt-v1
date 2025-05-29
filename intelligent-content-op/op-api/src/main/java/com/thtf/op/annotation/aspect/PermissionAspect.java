package com.thtf.op.annotation.aspect;

import com.thtf.global.common.cache.RedisUtil;
import com.thtf.global.common.consts.AuthConstants;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.global.common.rest.DefaultErrorCode;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.utils.JsonUtil;
import com.thtf.op.annotation.RequiresPermission;
import com.thtf.op.entity.SysMenuEntity;
import com.thtf.op.entity.SysRoleEntity;
import com.thtf.op.repo.SysMenuRepo;
import com.thtf.op.repo.SysRoleRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.lang.reflect.Method;
import java.util.List;


@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysMenuRepo sysMenuRepo;
    @Autowired
    private SysRoleRepo sysRoleRepo;


    @Around("@annotation(requiresPermission)||@within(requiresPermission)") // 拦截带有 @RequiresPermission 注解的方法
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        String token = getTokenFromContext();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token获取失败");
        }
        try {
            String userInfoStr = (String) redisUtil.get("token_" + token);
            if (StringUtils.isEmpty(userInfoStr)){
                return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
            }
            SystemUser systemUser = JsonUtil.fromJson(userInfoStr, SystemUser.class);
            //查询用户的角色和权限
            String requiredPermission = requiresPermission.value();
            boolean hasPermission = false;
            List<SysRoleEntity> roleByUserId = sysRoleRepo.getRoleByUserId(Integer.valueOf(systemUser.getId()));
            //检查用户权限
            for (SysRoleEntity role : roleByUserId) {
                List<SysMenuEntity> menuByRoleId = sysMenuRepo.getMenuByRoleId(role.getRoleId());
                for (SysMenuEntity menu : menuByRoleId) {
                    if (menu.getPerms().trim().equals(requiredPermission)) {
                        hasPermission = true;
                        break;
                    }
                }
            }
            if (!hasPermission&&!"SYSTEM_MANAGE".equals(systemUser.getSpecialAuth())) {
                return RestResponse.fail(DefaultErrorCode.PERMISSION_DENIED);
            }
                return joinPoint.proceed();
        }catch (Exception e) {
            throw new RuntimeException("Authentication or authorization failed", e);
        }
    }

    private String getTokenFromContext() {
        HttpServletRequest request = getHttpServletRequest();
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


    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest();
        }
        throw new IllegalStateException("HttpServletRequest is not available in the current context");
    }

    private boolean isSseEmitterResponse(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            return SseEmitter.class.isAssignableFrom(method.getReturnType());
        } catch (Exception e) {
            return false;
        }
    }
}
