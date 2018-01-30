package com.hafu365.usercenter.config;

import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.utils.MD5Util;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.security.FreshDetailsService;
import com.hafu365.fresh.service.security.FreshFilterSecurityInterceptor;
import lombok.extern.log4j.Log4j;
import javax.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by SunHaiyang on 2017/8/4.
 */
@Configuration
@Log4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    FreshFilterSecurityInterceptor freshFilterSecurityInterceptor;

    @Autowired
    FindByIndexNameSessionRepository<? extends ExpiringSession> sessionRepository;


    @Bean
    public UserDetailsService freshUserService() {
        return new FreshDetailsService();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(freshUserService()).passwordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return MD5Util.string2MD5(String.valueOf(charSequence));
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                log.info("Char Sequence : " + charSequence.toString());
                log.info("s : " + s);
                boolean flag = false;
                if (encode(charSequence).equals(s)) {
                    flag = true;
                }
                return flag;
            }
        });
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .anyRequest().permitAll().and()
                .formLogin()
                .successHandler(
                        new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                Gson gson = new Gson();
                                ReturnMessages messages = new ReturnMessages(RequestState.SUCCESS, "登录成功。", null);
                                response.setContentType("text/json;charset=utf-8");
                                response.getWriter().write(gson.toJson(messages));
                            }
                        }
                )
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        Gson gson = new Gson();
                        ReturnMessages messages = new ReturnMessages(RequestState.ERROR, "用户名或密码错误。", null);
                        httpServletResponse.setContentType("text/json;charset=utf-8");
                        httpServletResponse.getWriter().write(gson.toJson(messages));
                    }
                })
                .and()
                .logout().logoutUrl("/logout").permitAll();
        http.addFilterBefore(freshFilterSecurityInterceptor, FilterSecurityInterceptor.class);
        http.addFilterBefore(new KaptchaAuthenticationFilter("/login","/login?error"),UsernamePasswordAuthenticationFilter.class);
    }
}

