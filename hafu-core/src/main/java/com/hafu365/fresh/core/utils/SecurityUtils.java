package com.hafu365.fresh.core.utils;

import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * Security简单的工具
 * Created by SunHaiyang on 2017/8/4.
 */
public class SecurityUtils {

    /**
     * 获取用户名
     * @param request
     * @return
     */
    public static String getUsername(HttpServletRequest request){
        SecurityContextImpl securityContext = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if(securityContext == null){
            return null;
        }
        return securityContext.getAuthentication().getName();
    }


}
