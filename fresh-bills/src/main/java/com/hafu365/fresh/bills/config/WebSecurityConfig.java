package com.hafu365.fresh.bills.config;

import com.hafu365.fresh.core.utils.MD5Util;
import com.hafu365.fresh.service.security.FreshDetailsService;
import com.hafu365.fresh.service.security.FreshFilterSecurityInterceptor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * Created by SunHaiyang on 2017/8/4.
 */
@Configuration
@Log4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    FreshFilterSecurityInterceptor freshFilterSecurityInterceptor;

    @Bean
    public UserDetailsService freshUserService(){
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
                if(encode(charSequence).equals(s)){
                    flag = true;
                }
                return flag;
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .anyRequest().permitAll();
        http.addFilterBefore(freshFilterSecurityInterceptor,FilterSecurityInterceptor.class);
    }
}
