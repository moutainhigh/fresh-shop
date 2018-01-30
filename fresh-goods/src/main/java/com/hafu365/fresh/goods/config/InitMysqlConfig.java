package com.hafu365.fresh.goods.config;

import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.MD5Util;
import com.hafu365.fresh.goods.controller.goods.InitData;
import com.hafu365.fresh.repository.goods.BrandRepository;
import com.hafu365.fresh.repository.member.MemberInfoRepository;
import com.hafu365.fresh.repository.member.MemberRepository;
import com.hafu365.fresh.repository.role.RoleRepository;
import com.hafu365.fresh.repository.store.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始化数据
 * Created by HuangWeizhen on 2017/9/27.
 */
@Configuration
@Slf4j
public class InitMysqlConfig {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberInfoRepository memberInfoRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InitData initData;

    @Bean
    public Role initRole() throws Exception{
        long count = roleRepository.count();

        log.info("检查是否拥有数据");
        if(!(count > 0)){
            log.info("暂无数据.");
            log.info("正在初始化角色数据...");
            String roleName = "";
            String roleCode = "";
            if(initData.getRoleName() == null){
                throw new Exception("初始化角色名称不能为空!");
            }else{
                roleName = initData.getRoleName();
            }
            if(initData.getRoleCode() == null){
                throw new Exception("初始化角色编码不能为空!");
            }else{
                roleCode = initData.getRoleCode();
            }
            Role role = new Role();
            role.setName(roleName);
            role.setRoleCode(roleCode);

            Role roleRes = roleRepository.save(role);
            if(roleRes != null){
                log.info("初始化角色数据成功.");
                return roleRes;
            }else{
                throw new Exception("初始化角色数据失败.");
            }
        }else {
            log.info("拥有数据.");
            return null;

        }
    }


    @Bean
    public Member initMember() throws Exception{
        long count = memberRepository.count();
        log.info("检查是否拥有数据");
        if(!(count > 0)){
            log.info("暂无数据.");
            log.info("正在初始化数据...");
            String userName = "";
            String password = "";
            String phone = "";
            String roleName = initData.getRoleName();
            String roleCode = initData.getRoleCode();
            if(initData.getUserName() == null){
                throw new Exception("初始化用户名不能为空!");
            }else{
                userName = initData.getUserName();
            }
            if(initData.getPassword() == null){
                throw new Exception("初始化用户密码不能为空!");
            }else{
                password = initData.getPassword();
            }
            if(initData.getPhone() == null){
                throw new Exception("初始化用户电话不能为空!");
            }else{
                phone = initData.getPhone();
            }
            Member member = new Member();
            member.setUsername(userName);
            member.setPassword(MD5Util.string2MD5(password));   //添加md5加密
            member.setPhone(phone);
            member.setCreateTime(System.currentTimeMillis());
            List<Role> roleList = roleRepository.findByName(roleName);
            Role initRole = null;
            List<Role> memberRole = new ArrayList<Role>();
            if(roleList != null && roleList.size() > 0){
                for(Role role : roleList){
                    if(role != null && role.getName().equals(roleName) && role.getRoleCode().equals(roleCode)){
                        initRole = role;
                        break;
                    }
                }
                if(initRole != null){
                    memberRole.add(initRole);
                    member.setRoleList(memberRole);
                }else{
                    throw new Exception("初始化角色不存在");
                }

            }else{
                throw new Exception("初始化角色不存在");
            }
            Member memberRes = memberRepository.save(member);

            if(memberRes != null){
                log.info("初始化用户数据成功.");
                //初始化用户信息
                MemberInfo memberInfo = new MemberInfo();
                memberInfo.setMember(memberRes);
                memberInfo.setUsername(memberRes.getUsername());
                memberInfo.setCreateTime(System.currentTimeMillis());
                memberInfo.setState(StateConstant.USER_STATE_CHECK_NO.toString());
                memberInfoRepository.save(memberInfo);
                return memberRes;
            }else{
                throw new Exception("初始化用户数据失败.");
            }
        }else {
            log.info("拥有数据.");
            return null;
        }
    }


    @Bean
    public Store initStore() throws Exception{
        long count = storeRepository.count();
        log.info("检查是否拥有数据");
        if(!(count > 0)){
            log.info("暂无数据.");
            log.info("正在初始化数据...");

            String userName = initData.getUserName();
            String storeName = "";
            String address = "";
            String tel = "";
            String about = "";
            String bussinessLicenseNo = "";
            Member member = memberRepository.findMemberByUsername(userName);
            if(initData.getStoreName() == null){
                throw new Exception("初始化店铺名不能为空!");
            }else{
                storeName = initData.getStoreName();
            }
            if(initData.getAddress() == null){
                throw new Exception("初始化店铺地址不能为空!");
            }else{
                address = initData.getAddress();
            }
            if(initData.getTel() == null){
                throw new Exception("初始化店铺电话不能为空!");
            }else {
                tel = initData.getTel();
            }
            if(initData.getAbout() == null){
                throw new Exception("初始化店铺简介不能为空!");
            }else{
                about = initData.getAbout();
            }
            if(initData.getBusinesslicenseno() == null){
                throw new Exception("初始化店铺执照编号不能为空!");
            }else{
                bussinessLicenseNo = initData.getBusinesslicenseno();
            }
            Store store = new Store();
            store.setStoreName(storeName);
            store.setAddressStr(address);
            store.setTelStr(tel);
            store.setAbout(new StringBuffer(about));
            store.setBusinessLicenseNo(bussinessLicenseNo);
            store.setLicensePic("{\"path\":\"\"}");
            store.setState(StateConstant.STORE_STATE_CHECK_ON.toString());
            store.setCreateTime(System.currentTimeMillis());
            store.setTheDefault(true);
            //添加用户
            store.setMember(member);
            Store storeRes = storeRepository.save(store);
            if(storeRes != null){
                log.info("初始化店铺数据成功.");
                return storeRes;
            }else{
                throw new Exception("初始化店铺数据失败.");
            }
        }else {
            log.info("拥有数据.");
            return null;
        }
    }

    @Bean
    public Brand initBrand() throws Exception{
        long count = brandRepository.count();

        log.info("检查是否拥有数据");
        if(!(count > 0)){
            log.info("暂无数据.");
            log.info("正在初始化数据...");
            String brandTitle = "";
            if(initData.getBrandTitle() == null){
                throw new Exception("初始化品牌名不能为空!");
            }else{
                brandTitle = initData.getBrandTitle();
            }
            Brand brand = new Brand();
            brand.setBrandTitle(brandTitle);
            brand.setState(StateConstant.BRAND_STATE_CHECK_ON.toString());
            brand.setPics("[{\"path\":\"\"}]");
            brand.setTheDefault(true);
            brand.setCreateTime(System.currentTimeMillis());
            Brand brandRes = brandRepository.save(brand);
            if(brandRes != null){
                log.info("初始化品牌数据成功.");
                return brandRes;
            }else{
                throw new Exception("初始化品牌数据失败.");
            }
        }else {
            log.info("拥有数据.");
            return null;

        }
    }

   /* public String coding2Utf8(String str){
        try {
            return new String(str.getBytes(getStrCoding(str)),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getStrCoding(String str){
        String[] codings = {"ISO-8859-1","GBK","UTF-8","GB2312","UNICODE","ANSI","ANSII"};
        for (String coding : codings
                ) {
            try {
                if (str.equals(new String(str.getBytes(coding), coding))) {
                    return coding;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/

}
