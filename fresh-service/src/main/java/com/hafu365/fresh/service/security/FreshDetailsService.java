package com.hafu365.fresh.service.security;

import com.hafu365.fresh.core.entity.member.*;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by SunHaiyang on 2017/8/4.
 */
@Service
public class FreshDetailsService implements UserDetailsService {
    @Autowired
    MemberService memberService;

    @Autowired
    RoleService roleService;

    /**
     * 登录时获取用户权限,并提交需验证的账号密码
     * @param s
     * @return User
     * @throws UsernameNotFoundException 用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            Member member = memberService.findMemberByUsername(s);
            List<Role> roles = roleService.findRoleByMember(member);
            return new SysUser(member,roles);
        }catch (NullPointerException e){
            throw new UsernameNotFoundException("username not exist : " + s);
        }
    }
}
