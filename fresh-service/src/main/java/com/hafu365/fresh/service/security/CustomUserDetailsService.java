//package com.hafu365.fresh.service.security;
//
//import com.hafu365.fresh.core.entity.member.FreshGranteAuthority;
//import com.hafu365.fresh.core.entity.member.Member;
//import com.hafu365.fresh.core.entity.member.Permission;
//import com.hafu365.fresh.core.entity.member.Role;
//import com.hafu365.fresh.service.member.MemberService;
//import lombok.extern.log4j.Log4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
//import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Created by SunHaiyang on 2017/8/11.
// */
//@Log4j
//@Service
//public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {
//
//    @Autowired
//    MemberService memberService;
//
//    @Override
//    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
//        String username = token.getName();
//        log.info("校验成功的用户名为: "+username);
//        Member member = memberService.findMemberByUsername(username);
//        if(member != null){
//            Set<FreshGranteAuthority> granteAuthorities = new HashSet<FreshGranteAuthority>();
//            for (Role role : member.getRoleList()){
//                for(Permission permission : role.getPermissionList()){
//                    granteAuthorities.add(new FreshGranteAuthority(permission.getUrl(),permission.getMethod(),role.getRoleCode()));
//                }
//                granteAuthorities.add(new FreshGranteAuthority("","",role.getRoleCode()));
//            }
//            member.setAuthorities(granteAuthorities);
//            return member;
//        }else{
//            throw new UsernameNotFoundException("username is not exsited.");
//        }
//
//    }
//}
