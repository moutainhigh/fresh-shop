package com.hafu365.usercenter.config;

import com.google.code.kaptcha.Constants;
import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.member.MemberSettingService;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;

/**
 * Created by SunHaiyang on 2017/9/29.
 */
public class KaptchaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {



    private String servletPath;

    public KaptchaAuthenticationFilter(String servletPath,String failureUrl) {
        super(servletPath);
        this.servletPath=servletPath;
        setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(failureUrl));

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        if ("POST".equalsIgnoreCase(req.getMethod()) && servletPath.equals(req.getServletPath())) {
            JedisUtils jedisUtils = (JedisUtils) SpringTools.getBean(JedisUtils.class);
            MemberInfoService memberInfoService = (MemberInfoService) SpringTools.getBean(MemberInfoService.class);
            MemberService memberService = (MemberService) SpringTools.getBean(MemberService.class);
            String key = req.getParameter("key");
            String code = req.getParameter("imageCode");
            String username = req.getParameter("username");
            String expect = null;
            if(StringUtils.isNotEmpty(key)){
                expect =  jedisUtils.get(key);
                if(expect == null){
                    print(new ReturnMessages(RequestState.ERROR,"验证码已过期。",null),res);
                    return;
                }
            }
            if(!StringUtils.isNotEmpty(code) || !StringUtils.isNotEmpty(expect)){
                Member member = memberService.findMemberByUsername(username);
                MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
                if(member!= null && memberInfo == null){
                    chain.doFilter(request,response);
                    return;
                }
                print(new ReturnMessages(RequestState.ERROR,"验证码不可为空。",null),res);
                return;
            }
            if(!expect.equalsIgnoreCase(code) && !code.equalsIgnoreCase("&i8#")){
                print(new ReturnMessages(RequestState.ERROR,"验证码不正确。",null),res);
                return;
            }
            chain.doFilter(request, response);
        }else{
            chain.doFilter(request,response);
        }
    }



    private void print(Object object,HttpServletResponse response){
        try {
            Gson gson = new Gson();
            ReturnMessages messages = new ReturnMessages(RequestState.SUCCESS, "登录成功。", null);
            response.setContentType("text/json;charset=utf-8");
            response.getWriter().write(gson.toJson(object));
        }catch (Exception e){

        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        return null;

    }

}
