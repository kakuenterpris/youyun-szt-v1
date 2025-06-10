package com.ustack.chat.config.http.filters;

import cn.hutool.json.JSONUtil;
import com.ustack.chat.util.JwtToken;

import com.ustack.global.common.rest.RestResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.lang.util.StringUtils;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtFilter extends BasicHttpAuthenticationFilter {

    private String errorMsg;

    // 过滤器拦截请求的入口方法
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 判断请求头是否带上“Token”
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("Authorization");
        // 没有认证意愿（可能是登录行为或者为游客访问）,放行
        // 此处放行是因为有些操作不需要权限也可以执行，而对于那些需要权限才能执行的操作自然会因为没有token而在权限鉴定时被拦截
        if (!StringUtils.hasText(token)) {
            return true;
        }
        try {
            // 交给 myRealm
            SecurityUtils.getSubject().login(new JwtToken(token));
            return true;
        } catch (Exception e) {
            errorMsg = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(400);
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.println(JSONUtil.toJsonStr( RestResponse.fail(HttpStatus.UNAUTHORIZED.value(), errorMsg)));
        out.flush();
        out.close();
        return false;
    }

    /**
     * 对跨域访问提供支持
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域发送一个option请求
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }
}
