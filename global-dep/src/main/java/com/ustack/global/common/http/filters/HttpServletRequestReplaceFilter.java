package com.ustack.global.common.http.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @Description: 替换请求体
 * @author：linxin
 * @ClassName: HttpServletRequestReplaceFilter
 * @Date: 2023-12-26 13:54
 */
public class HttpServletRequestReplaceFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpServletRequestReplaceFilter.class);

    @Override
    public void destroy() {
        log.info("--------------请求包装过滤器销毁------------");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        ServletRequest requestWrapper = null;
        if(request instanceof HttpServletRequest) {
            String contentType = request.getContentType();
            if(contentType!=null&&contentType.toLowerCase().contains("application/json")){
                requestWrapper = new HttpRequestWrapper(request);
            }
        }
        if(requestWrapper == null) {
            chain.doFilter(request, response);
        } else {
            chain.doFilter(requestWrapper, response);
        }
    }

}
