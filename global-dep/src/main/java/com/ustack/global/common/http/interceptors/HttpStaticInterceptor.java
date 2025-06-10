package com.ustack.global.common.http.interceptors;


import com.ustack.global.common.http.filters.HttpRequestWrapper;
import com.ustack.global.common.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description: 接口请求统计，设置traceId 等等
 * @author：linxin
 * @ClassName: HttpStaticInterceptor
 * @Date: 2023-12-15 10:46
 */
public class HttpStaticInterceptor implements HandlerInterceptor  {
    private static final Logger log = LoggerFactory.getLogger(HttpStaticInterceptor.class);
    /**
     * 每个URI的请求数计数器
     */
    public static final Map<String, AtomicLong> URI_COUNTER = new ConcurrentHashMap<>();
    private static final String HTTP_REQUEST_START_TIMESTAMP = "HTTP_REQUEST_START_TIMESTAMP";
    private static final String HTTP_REQUEST_STOP_WATCH = "HTTP_REQUEST_STOP_WATCH";
    private static final String REQUEST_BODY_STR = "HTTP_REQUEST_BODY_STR";
    private static final String REQUEST_QUERY_STR = "HTTP_REQUEST_QUERY_STR";
    private static final String RESPONSE_BODY_WRAPPER = "RESPONSE_BODY_WRAPPER";

    // 日志保存线程数量
    private static final int SAVE_WORKER = 2;
    // 请求日志队列

    private final String env;
    private final String startTime;

    public HttpStaticInterceptor( String env, String startTime) {
        this.env = env;
        this.startTime = startTime;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //此行日志不可删除，删除后，用 x-www-from-urlencoded 传参会丢失参数值
        log.info("接口拦截器：{}", JsonUtil.toJson(request.getParameterMap()));
        MDC.put("httpMethod", request.getMethod());
        MDC.put("env", this.env);
        MDC.put("startTime", Objects.toString(this.startTime, "0"));
        //写入系统启动时间
        request.setAttribute(HTTP_REQUEST_START_TIMESTAMP, System.currentTimeMillis());
        request.setAttribute(HTTP_REQUEST_STOP_WATCH, new Slf4JStopWatch());
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
        String bodyString = requestWrapper.getBodyString(request);
        request.setAttribute(REQUEST_BODY_STR, bodyString);
        //更新URI 请求数计数器
        String uri = request.getRequestURI();
        if (!URI_COUNTER.containsKey(uri)) {
            URI_COUNTER.put(uri, new AtomicLong(1));
        } else {
            AtomicLong atomicLong = URI_COUNTER.get(uri);
            atomicLong.incrementAndGet();
        }
        //输出URI及常规的请求参数
        if (this.supportPrintParams(request)) {
            StringBuffer queryString = new StringBuffer();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames != null && paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                queryString.append(paramName + "=");
                if (paramValues != null) {
                    if (paramValues.length == 1) {
                        queryString.append(paramValues[0]);
                    } else {
                        queryString.append(Arrays.asList(paramValues));
                    }
                }
                if (paramNames.hasMoreElements()) {
                    queryString.append("&");
                }
            }
            request.setAttribute(REQUEST_QUERY_STR, queryString.toString());
            log.info("[STATIS] " + uri + " ,QS: " + queryString);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //读出初始时间
        long startTimestamp = (Long) request.getAttribute(HTTP_REQUEST_START_TIMESTAMP);
        StopWatch stopWatch = (Slf4JStopWatch) request.getAttribute(HTTP_REQUEST_STOP_WATCH);
        // 用于获取响应体
        String uri = request.getRequestURI();
        //URI 请求数计数器
        AtomicLong atomicLong = URI_COUNTER.get(uri);
        Long reqCost =  (System.currentTimeMillis() - startTimestamp);
        log.info("[STATIS] " + uri + " ,COST: " + reqCost + "ms, TOTAL_REQUEST: " + atomicLong.get());
        stopWatch.stop(uri);
        //记录请求日志
        request.removeAttribute(HTTP_REQUEST_START_TIMESTAMP);
        request.removeAttribute(HTTP_REQUEST_STOP_WATCH);
        request.removeAttribute(REQUEST_BODY_STR);
        request.removeAttribute(RESPONSE_BODY_WRAPPER);
        MDC.clear();
        stopWatch = null;
    }

    /**
     * 是否支持打印请求参数
     **/
    private boolean supportPrintParams(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String contentType = request.getContentType();
            if (contentType != null && (contentType.toLowerCase().indexOf("application/json") >= 0 || contentType.toLowerCase().indexOf("application/xml") >= 0)) {
                //若有参数，则打印，否则不打印
                Enumeration<String> parameterNames = request.getParameterNames();
                if (parameterNames != null && parameterNames.hasMoreElements()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }
        return true;
    }

    private static boolean isWebRequest(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent!= null) {
            // 常见的Web浏览器User-Agent中包含的关键字
            String[] browserKeywords = {"Chrome", "Firefox", "Safari", "Edge", "Opera", "MSIE"};
            for (String keyword : browserKeywords) {
                if (userAgent.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }
}