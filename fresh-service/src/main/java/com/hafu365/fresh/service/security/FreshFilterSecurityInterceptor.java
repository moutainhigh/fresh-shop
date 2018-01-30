package com.hafu365.fresh.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by SunHaiyang on 2017/8/4.
 */
@Service
public class FreshFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

    @Autowired FreshMetadataSourceService metadataSourceService;

    @Autowired
    public void setAccessDecisionManage(FreshAccessDecisionManager freshAccessDecisionManager){
        super.setAccessDecisionManager(freshAccessDecisionManager);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    private static HttpServletRequest getSessionId(ServletRequest request){
        HttpServletRequest httpRequest=(HttpServletRequest)request;
        return httpRequest;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = getSessionId(request);
        FilterInvocation invocation = new FilterInvocation(request,response,chain);
        invoke(invocation);
    }

    public void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getHttpRequest(),filterInvocation.getHttpResponse());
        }finally {
            super.afterInvocation(token,null);
        }
    }


    @Override
    public void destroy() {

    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.metadataSourceService;
    }
}
