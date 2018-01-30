package com.hafu365.fresh.core.entity.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by SunHaiyang on 2017/9/30.
 */
public class SysUser implements UserDetails {


    private String username;
    private String password;
    private List<Role> roleList;
    private List<Permission> permissions;

    public SysUser(Member member,List<Role> roles ) {
        this.username = member.getUsername();
        this.password = member.getPassword();
        this.roleList = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        if(roleList.size()> 0){
            for (Role role:roleList){
                if(role.getPermissionList() != null && role.getPermissionList().size() > 0){
                    for (Permission permission : role.getPermissionList()){
                        grantedAuthorities.add(new FreshGranteAuthority(permission.getUrl(),permission.getMethod(),role.getRoleCode()));
                    }
                }else{
                    grantedAuthorities.add(new FreshGranteAuthority("","",role.getRoleCode()));
                }
            }
        }else{
            grantedAuthorities.add(new FreshGranteAuthority("","","ROLE:USER:GHOST"));
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
