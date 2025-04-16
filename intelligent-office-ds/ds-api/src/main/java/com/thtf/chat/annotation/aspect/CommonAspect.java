package com.thtf.chat.annotation.aspect;

import com.thtf.chat.annotation.CommonPermission;
import com.thtf.chat.annotation.RequiresPermission;
import com.thtf.chat.entity.SysMenuEntity;
import com.thtf.chat.entity.SysRoleEntity;
import com.thtf.chat.repo.SysMenuRepo;
import com.thtf.chat.repo.SysRoleRepo;
import com.thtf.chat.utils.RedisUtil;
import com.thtf.dto.ChatRequestDto;
import com.thtf.global.common.consts.AuthConstants;
import com.thtf.global.common.dto.SystemUser;
import com.thtf.global.common.rest.DefaultErrorCode;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Aspect
@Component
public class CommonAspect {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysMenuRepo sysMenuRepo;
    @Autowired
    private SysRoleRepo sysRoleRepo;


    @Around("@annotation(commonPermission)") // 拦截带有@RequiresPermission注解的方法
    public Object checkPermission(ProceedingJoinPoint joinPoint, CommonPermission commonPermission) throws Throwable {
        String token = getTokenFromContext();
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token is missing");
        }
        try {
            String userInfoStr = (String) redisUtil.get("token_" + token);
            if (StringUtils.isEmpty(userInfoStr)){
                if (joinPoint.proceed() instanceof SseEmitter){
                    SseEmitter sseEmitter = new SseEmitter((long) Integer.MAX_VALUE);
                    sseEmitter.send(DefaultErrorCode.INVALID_TOKEN);
                    return sseEmitter;
                }
                return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
            }
            SystemUser systemUser = JsonUtil.fromJson(userInfoStr, SystemUser.class);
            //查询用户的角色和权限
            ChatRequestDto chatRequestDto = (ChatRequestDto) joinPoint.getArgs()[0];
            boolean hasPermission = false;
            List<SysRoleEntity> roleByUserId = sysRoleRepo.getRoleByUserId(Integer.valueOf(systemUser.getUserId()));
            //检查用户权限
            outerLoop:
            for (SysRoleEntity role : roleByUserId) {
                List<SysMenuEntity> menuByRoleId = sysMenuRepo.getMenuByRoleId(role.getRoleId());
                for (SysMenuEntity menu : menuByRoleId) {
                    if (menu.getPerms().trim().equals("chat:"+chatRequestDto.getSceneType())) {
                        hasPermission = true;
                        break outerLoop;
                    }
                }
            }
            if (!hasPermission) {
                if (joinPoint.proceed() instanceof SseEmitter){
                    SseEmitter sseEmitter = new SseEmitter((long) Integer.MAX_VALUE);
                    sseEmitter.send(DefaultErrorCode.INVALID_TOKEN);
                    return sseEmitter;
                }
                return RestResponse.fail(DefaultErrorCode.INVALID_TOKEN);
            }
            return joinPoint.proceed();
        }catch (Exception e) {
            throw new RuntimeException("Authentication or authorization failed", e);
        }
    }

    private String getTokenFromContext() {
        HttpServletRequest request = getHttpServletRequest();
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
}
