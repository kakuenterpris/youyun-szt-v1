package com.ustack.chat.annotation.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ustack.chat.annotation.RequiresPermission;
import com.ustack.chat.entity.BusUserInfoEntity;
import com.ustack.chat.entity.SysMenuEntity;
import com.ustack.chat.entity.SysRoleEntity;
import com.ustack.chat.entity.SysRoleMenuEntity;
import com.ustack.chat.repo.SysMenuRepo;
import com.ustack.chat.repo.SysRoleMenuRepo;
import com.ustack.chat.repo.SysRoleRepo;
import com.ustack.chat.utils.RedisUtil;
import com.ustack.global.common.consts.AuthConstants;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysMenuRepo sysMenuRepo;
    @Autowired
    private SysRoleRepo sysRoleRepo;
    @Autowired
    private SysRoleMenuRepo sysRoleMenuRepo;



    @Around("@annotation(requiresPermission)") // 拦截带有 @RequiresPermission 注解的方法
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
            Integer authtype = requiresPermission.authtype();
            boolean hasPermission = false;
            List<SysRoleEntity> roleByUserId = sysRoleRepo.getRoleByUserId(Integer.valueOf(systemUser.getId()));
            //检查用户权限
            for (SysRoleEntity role : roleByUserId) {
                List<SysMenuEntity> menuByRoleId = sysMenuRepo.getMenuByRoleId(role.getRoleId());
                List<SysRoleMenuEntity> list = sysRoleMenuRepo.list(new LambdaQueryWrapper<SysRoleMenuEntity>().eq(SysRoleMenuEntity::getRoleId, role.getRoleId()));
                for (SysMenuEntity menu : menuByRoleId) {
                    Optional<SysRoleMenuEntity> first = list.stream()
                            .filter(sysRoleMenu -> sysRoleMenu.getMenuId().equals(menu.getMenuId()))
                            .findFirst();
                    SysRoleMenuEntity sysRoleMenu = first.get();
                    if (menu.getPerms().trim().equals(requiredPermission)&&sysRoleMenu.getManageAuth()>=authtype) {
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
