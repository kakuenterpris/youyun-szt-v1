package com.ustack.chat.annotation.aspect;

import com.google.gson.Gson;
import com.ustack.chat.entity.SysOperLogEntity;
import com.ustack.chat.repo.SysOperLogRepo;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

//@Aspect
//@Component
public class OperLogAspect {

    @Autowired
    private SysOperLogRepo operLogRepo;

    @Autowired
    private Gson gson;


    // 添加自定义线程池
    private final TaskExecutor taskExecutor = createAsyncExecutor();

    private TaskExecutor createAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Async-Log-");
        executor.initialize();
        return executor;
    }


    @Around("execution(* com.ustack.chat.controller..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLogEntity log = new SysOperLogEntity();

        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // 记录请求信息
            log.setOperTime(new Date());
            log.setOperIp(getClientIP(request));
            log.setRequestMethod(request.getMethod() + " " + request.getRequestURI());

            // 获取用户信息
            SystemUser currentUser = ContextUtil.currentUser();
            if (currentUser != null) {
                log.setOperName(currentUser.getUserName());
                log.setDeptName(currentUser.getDepName());
            }

//            // 记录请求参数
//            log.setParams(parseParams(joinPoint.getArgs()));
//            log.setRequestBody(getRequestBody(request));

            // 执行方法
            Object result = joinPoint.proceed();

            // 记录响应信息
            if (result instanceof RestResponse) {
                RestResponse<?> response = (RestResponse<?>) result;
                log.setStatus(response.getCode());
//                log.setResponse(JsonUtil.toJson(response.getData()));
            }

            return result;
        } catch (Exception e) {
            log.setStatus(500);
            log.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 记录耗时
            long endTime = System.currentTimeMillis();
            log.setCostTime(endTime - startTime);
            // 异步保存日志
            CompletableFuture.runAsync(() -> {
                try {
                    operLogRepo.save(log);
                } catch (Exception e) {
                    // 添加异常日志输出
                    System.err.println("保存操作日志失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }, taskExecutor);
        }
    }


    private String getClientIP(HttpServletRequest request) {
        // 实现获取真实IP的逻辑
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        String ip = Arrays.stream(headers)
                .map(request::getHeader)
                .filter(h -> h != null && !h.isEmpty() && !"unknown".equalsIgnoreCase(h))
                .findFirst()
                .orElse(request.getRemoteAddr());

        // 处理多个代理IP的情况（取第一个有效IP）
        int index = ip.indexOf(",");
        if (index != -1) {
            ip = ip.substring(0, index).trim();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    private String parseParams(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletResponse))
                .map(arg -> {
                    try {
                        return gson.toJson(arg);
                    } catch (Exception e) {
                        return "[Serialization Failed] " + arg.getClass().getName();
                    }
                })
                .collect(Collectors.joining(", "));
    }

    private String getRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            try {
                return new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            } catch (Exception e) {
                return "[error reading request body]";
            }
        }
        return null;
    }

}
