package com.hafu365.fresh.goods.controller.goods;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by HuangWeizhen on 2017/10/25.
 */
@Component
public class InitData {

    //初始化角色属性
    @Value("${role.rolename}")
    private String roleName;

    @Value("${role.rolecode}")
    private String roleCode;

    //初始化用户属性
    @Value("${member.username}")
    private String userName;

    @Value("${member.password}")
    private String password;

    @Value("${member.phone}")
    private String phone;

    //初始化店铺属性
    @Value("${store.storename}")
    private String storeName;

    @Value("${store.address}")
    private String address;

    @Value("${store.tel}")
    private String tel;

    @Value("${store.about}")
    private String about;

    @Value("${store.businesslicenseno}")
    private String businesslicenseno;

    //初始化品牌属性
    @Value("${brand.brandtitle}")
    private String brandTitle;

    public String getRoleName() {
        return roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getAddress() {
        return address;
    }

    public String getTel() {
        return tel;
    }

    public String getAbout() {
        return about;
    }

    public String getBusinesslicenseno() {
        return businesslicenseno;
    }

    public String getBrandTitle() {
        return brandTitle;
    }


}
