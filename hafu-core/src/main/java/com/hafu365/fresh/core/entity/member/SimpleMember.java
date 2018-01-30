package com.hafu365.fresh.core.entity.member;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhaihuilin on 2017/10/23  13:13.
 */
public class SimpleMember implements Serializable {

    private String memberId;
    private String username;
    private String state;
    private boolean del = Boolean.FALSE;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNonLocked = true;
    private boolean isCredentialsNonExpired = true;
    private boolean isEnabled = true;
    private List<SimpleRole> roleList;

    public  SimpleMember(Member member){
        this.memberId=member.getMemberId();
        this.username=member.getUsername();
        this.state=member.getState();
        this.del=member.isDel();
        this.isAccountNonExpired=member.isAccountNonExpired();
        this.isAccountNonLocked=member.isAccountNonLocked();
        this.isCredentialsNonExpired=member.isCredentialsNonExpired();
        this.isEnabled=member.isEnabled();
        List<Role> roles=member.getRoleList();
        if (roles !=null && roles.size()>0){
              for (Role role :roles){
                  roleList.add(new SimpleRole(role));
             }
        }
    }
}
