package com.ustack.global.common.rest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

/**
 * @Description: 统一响应对象
 * @author：linxin
 * @ClassName: RestResponse
 * @Date: 2023-12-15 10:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class RestResponse<T> {

    public static final String SUCCESS_MSG = "请求成功";
    public static final String CREATE_SUCCESS_MSG = "添加成功";
    public static final String EDIT_SUCCESS_MSG = "编辑成功";
    public static final String DELETE_SUCCESS_MSG = "删除成功";
    public static final String TRACE_KEY = "traceId";
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    private String msg;
    private T data;
    private int code;
    private boolean success;
    private long total;
    private String traceId;

    public static final RestResponse<?> SUCCESS = new RestResponse("请求成功", null, 200, true, 0, MDC.get(TRACE_KEY));


    /**
     * 成功返回自定义提示信息
     * @param: msg
     * @author linxin
     * @return RestResponse<T>
     * @date 2024/7/2 10:50
     */
    public static RestResponse okWithMsg(String msg){
        RestResponse response = new RestResponse();
        response.setSuccess(true);
        response.setCode(200);
        response.setMsg(msg);
        response.setTotal(0);
        response.setData(null);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    /*
     * @author   syh
     * @desc     添加成功
     * @date     2024/8/1 16:53
     **/
    public static <T> RestResponse<T> createSuccess(T data) {
        return new RestResponse(CREATE_SUCCESS_MSG,data,200,
                true,0,MDC.get(TRACE_KEY));
    }

    /*
     * @author   syh
     * @desc     编辑成功
     * @date     2024/8/1 16:53
     **/
    public static <T> RestResponse<T> updateSuccess(T data) {
        return new RestResponse(EDIT_SUCCESS_MSG,data,200,
                true,0,MDC.get(TRACE_KEY));
    }

    /*
     * @author   syh
     * @desc     删除成功
     * @date     2024/8/1 16:53
     **/
    public static <T> RestResponse<T> deleteSuccess(T data) {
        return new RestResponse(DELETE_SUCCESS_MSG,data,200,
                true,0,MDC.get(TRACE_KEY));
    }

    /**
     * 成功返回自定义提示信息
     * @param: msg
     * @author linxin
     * @return RestResponse<T>
     * @date 2024/7/2 10:50
     */
    public static <T> RestResponse<T> okWithMsg(T data, String msg){
        RestResponse<T> response = new RestResponse();
        response.setSuccess(true);
        response.setCode(200);
        response.setMsg(msg);
        response.setTotal(0);
        response.setData(data);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    public static <T> RestResponse<T> success(T data) {
        RestResponse<T> response = new RestResponse();
        response.setSuccess(true);
        response.setCode(200);
        response.setMsg("请求成功");
        response.setTotal(0);
        response.setData(data);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    public static <T> RestResponse<T> success(T data, long total) {
       RestResponse<T> response = new RestResponse();
        response.setSuccess(true);
        response.setCode(200);
        response.setMsg(SUCCESS_MSG);
        response.setTotal(total);
        response.setData(data);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    public static <T> RestResponse<T> success(T data, int total) {
        RestResponse<T> response = new RestResponse();
        response.setSuccess(true);
        response.setCode(200);
        response.setMsg(SUCCESS_MSG);
        response.setTotal(total);
        response.setData(data);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    public static <T> RestResponse<T> fail(ErrorCode errorCode) {
       return fail(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> RestResponse<T> fail(ErrorCode errorCode, String ...params) {
        return fail(errorCode.getCode(), errorCode.fmtMsg(params));
    }

    public static <T> RestResponse<T> error(String msg) {
        RestResponse<T> response = new RestResponse();
        response.setSuccess(false);
        response.setCode(ERROR_CODE);
        response.setMsg(msg);
        response.setTotal(0);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    public static <T> RestResponse<T> fail( int code, String msg) {
        RestResponse<T> response = new RestResponse();
        response.setSuccess(false);
        response.setCode(code);
        response.setMsg(msg);
        response.setTotal(0);
        response.setTraceId(MDC.get(TRACE_KEY));
        return response;
    }

    public boolean ok(){
        return this.success;
    }

}
