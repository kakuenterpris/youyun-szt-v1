package com.ustack.op.exception;


import com.ustack.global.common.exception.CustomException;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.DefaultErrorCode;
import com.ustack.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: GlobalExceptionHandler
 * @Date: 2023-12-19 16:01
 */
@Hidden
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.profiles.active}")
    private String profile;

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({CustomException.class})
    @ResponseBody
    @Order(1)
    public RestResponse<?> customExceptionHandler(CustomException e) {
        return RestResponse.fail(e.getCode(), e.getMsg());
    }

    @ExceptionHandler({BindException.class})
    @ResponseBody
    @Order(2)
    public RestResponse<?> paramBindExceptionHandler(BindException e) {

        BindingResult bindingResult = e.getBindingResult();
        return getObjectRestResponse(bindingResult);
    }

    @NotNull
    private static RestResponse<Object> getObjectRestResponse(BindingResult bindingResult) {
        String msg = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return RestResponse.fail(400, msg);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    @Order(2)
    public RestResponse<?> methodArgsNotValidExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return getObjectRestResponse(bindingResult);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    @Order(2)
    public RestResponse<?> customExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String msg = violations.stream().map(err -> err.getMessage()).collect(Collectors.joining(";"));
        return RestResponse.fail(400, msg);
    }


    @ExceptionHandler({Exception.class})
    @Order
    public RestResponse<?> InternalExceptionHandler(Exception e) {
        log.error("程序内部异常：", e);
        // 正式话你就能够返回traceId 查询日志
        if (Objects.equals(profile, "prod")){
            if (Objects.nonNull(ContextUtil.context()) && Objects.nonNull(ContextUtil.context().getTraceId())){
                return RestResponse.fail(DefaultErrorCode.SYSTEM_INTERNAL_ERROR_FMT, String.format("traceId：%s", ContextUtil.context().getTraceId()));
            }else {
                return RestResponse.fail(DefaultErrorCode.SYSTEM_INTERNAL_ERROR);
            }
        }
        // 其他环境返回异常信息，方便查找错误原因
        StringBuilder sb = new StringBuilder();
        sb.append(e.toString()).append(" at ");
        for (StackTraceElement element : e.getStackTrace()) {
            String string = element.toString();
            // 只返回第一个异常的位置
            if (StringUtils.contains(string,"com.ustack")){
                sb.append("【").append(string).append("】");
                break;
            }
        }
        return RestResponse.fail(DefaultErrorCode.SYSTEM_INTERNAL_ERROR_FMT, sb.toString());
    }

}
