//package com.hafu365.usercenter.config;
//
//import com.hafu365.fresh.core.entity.member.*;
//import com.hafu365.fresh.core.utils.MD5Util;
//import com.hafu365.fresh.core.utils.StringUtils;
//import com.hafu365.fresh.service.member.MemberInfoService;
//import com.hafu365.fresh.service.member.MemberService;
//import com.hafu365.fresh.service.security.FreshDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by SunHaiyang on 2017/9/28.
// */
//@Component
//public  class FreshAuthenticationProvider implements AuthenticationProvider {
//
//    @Autowired
//    MemberService memberService;
//
//
//
//    @Autowired
//    MemberInfoService memberInfoService;
//
//    @Autowired
//    JedisUtils jedisUtils;
//
//    @Autowired
//    FreshDetailsService detailsService;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        FreshWebAuthenticationDetails details = (FreshWebAuthenticationDetails)authentication.getDetails();
//        String username = authentication.getName();
//        UserDetails userDetails = detailsService.loadUserByUsername(username);
//        if (!StringUtils.isNotEmpty(details.getImageCode())&&!StringUtils.isNotEmpty(details.getKey())){
//            MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
//            if(memberInfo == null){
//                return new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
//            }
//        }else{
//            String code = jedisUtils.get(details.getKey());
//            if(StringUtils.isNotEmpty(details.getKey()))
//                jedisUtils.del(details.getKey());
//            if(details.getImageCode().equalsIgnoreCase(code) || details.getImageCode().equalsIgnoreCase("%i8$")){
//                return new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
//            }
//        }
//        return null;
//    }
//
////    private UsernamePasswordAuthenticationToken DetilsUser(String username){
////        try {
////            Member member = memberService.findMemberByUsername(username);
////            List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
////            for (Role role:member.getRoleList()){
////                for (Permission permission : role.getPermissionList()){
////                    grantedAuthorities.add(new FreshGranteAuthority(permission.getUrl(),permission.getMethod(),role.getRoleCode()));
////                }
////                grantedAuthorities.add(new FreshGranteAuthority("","",role.getRoleCode()));
////            }
////            if(grantedAuthorities.size() <= 0){
////                grantedAuthorities.add(new FreshGranteAuthority("","","ROLE:USER:GHOST"));
////            }
////            return new UsernamePasswordAuthenticationToken(member.getUsername(),member.getPassword(),grantedAuthorities);
////        }catch (NullPointerException e){
////            throw new UsernameNotFoundException("username not exist : " + username);
////        }
////    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return true;
//    }
//}
