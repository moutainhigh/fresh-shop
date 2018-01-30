package com.hafu365.fresh.core.entity.member;

import java.io.Serializable;

/**
 * Created by zhaihuilin on 2017/10/23  13:16.
 */
public class SimpleRole implements Serializable {
    private long id;
    private String roleCode;
    private String name;


    public  SimpleRole(Role role){
        this.id=role.getId();
        this.name=role.getName();
        this.roleCode=role.getRoleCode();
    }
}
