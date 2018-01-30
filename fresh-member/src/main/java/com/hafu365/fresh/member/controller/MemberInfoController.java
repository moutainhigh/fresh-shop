package com.hafu365.fresh.member.controller;

import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.Permission;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.utils.Constants;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.MakeOrderService;
import com.hafu365.fresh.service.role.RoleService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 用户配置信息 控制层
 * Created by HuangXueheng on 2017/9/19.
 */
@Log4j
@RestController
public class MemberInfoController implements Constants {

    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private MemberService memberService;

    @Autowired
    RoleService roleService;

    @Autowired
    private MakeOrderService makeOrderService;


    /**
     * 查询用户信息
     * @param username 用户名 [可空]
     * @param page 页码 [可空] (默认:0)
     * @param pageSize 页面大小 [可空] (默认:20)
     * @return
     */
    @PostMapping(value = "/memberInfo/find")
    public ReturnMessages findMemberInfo(
            @RequestParam(name = "username",required = false)String username,
            @RequestParam(name = "page",required = false,defaultValue = "0")int page,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20")int pageSize
    ){
        if(StringUtils.isNotEmpty(username)){
            MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
            if(memberInfo != null){
                return new ReturnMessages(RequestState.SUCCESS,"查询成功.",memberInfo);
            }else{
                return new ReturnMessages(RequestState.ERROR,"查询失败.",null);
            }
        }else{
            Sort sort = new Sort(Sort.Direction.DESC,"updateTime");
            PageRequest pageable = new PageRequest(page,pageSize,sort);
            Page<MemberInfo> memberInfoPage =  memberInfoService.findMemberInfo(pageable);
            if (memberInfoPage.getContent() != null && memberInfoPage.getContent().size() > 0){
                return new ReturnMessages(RequestState.SUCCESS,"查询成功.",memberInfoPage);
            }else{
                return new ReturnMessages(RequestState.ERROR,"查询失败.",null);
            }
        }
    }

    /**
     * 验证是否创建
     * @param username 用户名
     * @return
     */
    @PostMapping(value = "/memberInfo/exist")
    public ReturnMessages existMemberInfo(
            @RequestParam(name = "username")String username
    ){
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        if (memberInfo != null){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功.",memberInfo);
        }else{
            return new ReturnMessages(RequestState.ERROR,"查询失败.",null);
        }
    }

    /**
     * 修改用户信息
     * @param username 用户名
     * @param businessLicenseSN 营业执照编号 [可空]
     * @param businessLicensePicStr 营业执照图片 [可空]  例:{name:'adsf',path:'http://localhsot/123.jpg',url:'http://localhsot'}
     * @param organizationCode 组织机构代码 [可空]
     * @param organizationCodePicStr 组织机构代码证图片 [可空] 例:{name:'adsf',path:'http://localhsot/123.jpg',url:'http://localhsot'}
     * @param portraitStr 用户头像 [可空] 例:{name:'adsf',path:'http://localhsot/123.jpg',url:'http://localhsot'}
     * @param officeAddress 用户办公室地址 [可空]
     * @param officeTel 用户办公室电话 [可空]
     * @param apartment 用户住址 [可空]
     * @param homePhone 用户家庭电话 [可空]
     * @param birthdate 用户出生日期 [可空]
     * @param birthplace 用户出生地址 [可空]
     * @param userDetail 用户个人简介 [可空]
     * @param education 用户学历 [可空]
     * @param graduatedFrom 用户毕业学校 [可空]
     * @param workUnit 用户工作单位 [可空]
     * @param blood 用户血型 [可空]
     * @return
     */
    @PostMapping(value = "/memberInfo/save")
    public ReturnMessages saveMemberInfo(
           @RequestParam(name = "username")String username,
           @RequestParam(name = "businessLicenseSN",required = false)String businessLicenseSN,
           @RequestParam(name = "businessLicensePicStr",required = false)String businessLicensePicStr,
           @RequestParam(name = "organizationCode",required = false)String organizationCode,
           @RequestParam(name = "organizationCodePicStr",required = false)String organizationCodePicStr,
           @RequestParam(name = "officeAddress",required = false)String officeAddress,
           @RequestParam(name = "portraitStr",required = false)String portraitStr,
           @RequestParam(name = "officeTel",required = false)String officeTel,
           @RequestParam(name = "apartment",required = false)String apartment,
           @RequestParam(name = "homePhone",required = false)String homePhone,
           @RequestParam(name = "birthdate",required = false ,defaultValue = "-1")Long birthdate,
           @RequestParam(name = "birthplace",required = false)String birthplace,
           @RequestParam(name = "userDetail",required = false)String userDetail,
           @RequestParam(name = "education",required = false)String education,
           @RequestParam(name = "graduatedFrom",required = false)String graduatedFrom,
           @RequestParam(name = "workUnit",required = false)String workUnit,
           @RequestParam(name = "blood",required = false)String blood,
           @RequestParam(name = "roleId",required = false)long[] roleIds,
           @RequestParam(name = "state",required = false)String state
    ){
        MemberInfo memberInfo = null;
        memberInfo = memberInfoService.findMemberInfoByUsername(username);
        String message = "";
        Gson gson = new Gson();
        Date nowDate = new Date();
        message = "更新信息";
        if (StringUtils.isNotEmpty(portraitStr)){
            memberInfo.setPortraitStr(null);
            memberInfo.setPortrait(null);
            Image protrait = gson.fromJson(portraitStr,Image.class);
            memberInfo.setPortrait(protrait);
        }
        if(StringUtils.isNotEmpty(businessLicenseSN)){
            memberInfo.setBusinessLicenseSN(businessLicenseSN);
        }
        if(StringUtils.isNotEmpty(organizationCode)){
            memberInfo.setOrganizationCode(organizationCode);
        }
        if(StringUtils.isNotEmpty(businessLicensePicStr)){
            memberInfo.setBusinessLicensePic(null);
            memberInfo.setBusinessLicensePicStr(null);
            Image businessLicensePric = gson.fromJson(businessLicensePicStr,Image.class);
            memberInfo.setBusinessLicensePic(businessLicensePric);
        }
        if(StringUtils.isNotEmpty(organizationCodePicStr)){
            memberInfo.setOrganizationCodePic(null);
            memberInfo.setOrganizationCodePicStr(null);
            Image organizationCodePic = gson.fromJson(organizationCodePicStr,Image.class);
            memberInfo.setOrganizationCodePic(organizationCodePic);
        }
        if(StringUtils.isNotEmpty(officeAddress)){
            memberInfo.setOfficeAddress(officeAddress);
        }
        memberInfo.setUpdateTime(nowDate.getTime());
        if (StringUtils.isNotEmpty(officeTel)){
            memberInfo.setOfficeTel(officeTel);
        }
        if(StringUtils.isNotEmpty(apartment)){
            memberInfo.setApartment(apartment);
        }
        if(StringUtils.isNotEmpty(homePhone)){
            memberInfo.setHomePhone(homePhone);
        }
        if(birthdate > 0){
            memberInfo.setBirthdate(birthdate);
        }
        if (StringUtils.isNotEmpty(birthplace)){
            memberInfo.setBirthplace(birthplace);
        }
        if(StringUtils.isNotEmpty(userDetail)){
            memberInfo.setUserDetail(userDetail);
        }
        if(StringUtils.isNotEmpty(education)){
            memberInfo.setEducation(education);
        }
        if(StringUtils.isNotEmpty(graduatedFrom)){
            memberInfo.setGraduatedFrom(graduatedFrom);
        }
        if(StringUtils.isNotEmpty(workUnit)){
            memberInfo.setWorkUnit(workUnit);
        }
        if(StringUtils.isNotEmpty(blood)){
            memberInfo.setBlood(blood);
        }
        if(StringUtils.isNotEmpty(state)){
            memberInfo.setState(state);
        }
        if(roleIds != null){
            List<Role> roles = new ArrayList<Role>();
            for (long roleId:roleIds){
                Role role = roleService.findRoleById(roleId);
                if(role != null){
                    roles.add(role);
                }
            }
            roleService.setMemberRole(username,roles);
        }
        memberInfo = memberInfoService.saveMemberInfo(memberInfo);
        if(memberInfo != null){
            return new ReturnMessages(RequestState.SUCCESS,message + "成功.",memberInfo);
        }else{
            return new ReturnMessages(RequestState.ERROR,message + "失败.",null);
        }
    }

    /**
     * 新增/修改自身用户信息
     * @param businessLicenseSN 营业执照编号 [可空]
     * @param businessLicensePicStr 营业执照图片 [可空]  例:{name:'adsf',path:'http://localhsot/123.jpg',url:'http://localhsot'}
     * @param organizationCode 组织机构代码 [可空]
     * @param organizationCodePicStr 组织机构代码证图片 [可空] 例:{name:'adsf',path:'http://localhsot/123.jpg',url:'http://localhsot'}
     * @param portraitStr 用户头像 [可空] 例:{name:'adsf',path:'http://localhsot/123.jpg',url:'http://localhsot'}
     * @param officeAddress 用户办公室地址 [可空]
     * @param officeTel 用户办公室电话 [可空]
     * @param apartment 用户住址 [可空]
     * @param homePhone 用户家庭电话 [可空]
     * @param birthdate 用户出生日期 [可空]
     * @param birthplace 用户出生地址 [可空]
     * @param userDetail 用户个人简介 [可空]
     * @param education 用户学历 [可空]
     * @param graduatedFrom 用户毕业学校 [可空]
     * @param workUnit 用户工作单位 [可空]
     * @param blood 用户血型 [可空]
     * @return
     */
    @PostMapping(value = "/memberInfo/saveMe")
    public ReturnMessages saveMemberInfo(
            @RequestParam(name = "businessLicenseSN",required = false)String businessLicenseSN,
            @RequestParam(name = "businessLicensePicStr",required = false)String businessLicensePicStr,
            @RequestParam(name = "organizationCode",required = false)String organizationCode,
            @RequestParam(name = "organizationCodePicStr",required = false)String organizationCodePicStr,
            @RequestParam(name = "officeAddress",required = false)String officeAddress,
            @RequestParam(name = "portraitStr",required = false)String portraitStr,
            @RequestParam(name = "officeTel",required = false)String officeTel,
            @RequestParam(name = "apartment",required = false)String apartment,
            @RequestParam(name = "homePhone",required = false)String homePhone,
            @RequestParam(name = "birthdate",required = false ,defaultValue = "-1")Long birthdate,
            @RequestParam(name = "birthplace",required = false)String birthplace,
            @RequestParam(name = "userDetail",required = false)String userDetail,
            @RequestParam(name = "education",required = false)String education,
            @RequestParam(name = "graduatedFrom",required = false)String graduatedFrom,
            @RequestParam(name = "workUnit",required = false)String workUnit,
            @RequestParam(name = "blood",required = false)String blood,
            HttpServletRequest request
    ){
        MemberInfo memberInfo = null;
        String username = SecurityUtils.getUsername(request);
        memberInfo = memberInfoService.findMemberInfoByUsername(username);
        boolean flag = false;
        Gson gson = new Gson();
        Date nowDate = new Date();
        if(memberInfo != null){
            flag = true;
            if (StringUtils.isNotEmpty(portraitStr)){
                memberInfo.setPortraitStr(null);
                memberInfo.setPortrait(null);
                Image protrait = gson.fromJson(portraitStr,Image.class);
                memberInfo.setPortrait(protrait);
            }
            if(StringUtils.isNotEmpty(businessLicenseSN)){
                memberInfo.setBusinessLicenseSN(businessLicenseSN);
            }
            if(StringUtils.isNotEmpty(organizationCode)){
                memberInfo.setOrganizationCode(organizationCode);
            }
            if(StringUtils.isNotEmpty(businessLicensePicStr)){
                memberInfo.setBusinessLicensePic(null);
                memberInfo.setBusinessLicensePicStr(null);
                Image businessLicensePric = gson.fromJson(businessLicensePicStr,Image.class);
                memberInfo.setBusinessLicensePic(businessLicensePric);
            }
            if(StringUtils.isNotEmpty(organizationCodePicStr)){
                memberInfo.setOrganizationCodePic(null);
                memberInfo.setOrganizationCodePicStr(null);
                Image organizationCodePic = gson.fromJson(organizationCodePicStr,Image.class);
                memberInfo.setOrganizationCodePic(organizationCodePic);
            }
            memberInfo.setUpdateTime(nowDate.getTime());
            if (StringUtils.isNotEmpty(officeTel)){
                memberInfo.setOfficeTel(officeTel);
            }
            if(StringUtils.isNotEmpty(officeAddress)){
                memberInfo.setOfficeAddress(officeAddress);
            }
            if(StringUtils.isNotEmpty(apartment)){
                memberInfo.setApartment(apartment);
            }
            if(StringUtils.isNotEmpty(homePhone)){
                memberInfo.setHomePhone(homePhone);
            }
            if(birthdate > 0){
                memberInfo.setBirthdate(birthdate);
            }
            if (StringUtils.isNotEmpty(birthplace)){
                memberInfo.setBirthplace(birthplace);
            }
            if(StringUtils.isNotEmpty(userDetail)){
                memberInfo.setUserDetail(userDetail);
            }
            if(StringUtils.isNotEmpty(education)){
                memberInfo.setEducation(education);
            }
            if(StringUtils.isNotEmpty(graduatedFrom)){
                memberInfo.setGraduatedFrom(graduatedFrom);
            }
            if(StringUtils.isNotEmpty(workUnit)){
                memberInfo.setWorkUnit(workUnit);
            }
            if(StringUtils.isNotEmpty(blood)){
                memberInfo.setBlood(blood);
            }
            if(memberInfo.getState().equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
                memberInfo.setState(StateConstant.USER_STATE_CHECK_ING);
            }
        }else{
            memberInfo = new MemberInfo();
            Member member = memberService.findMemberByUsername(username);
            memberInfo.setUsername(member.getUsername());
            memberInfo.setMember(member);
            if (StringUtils.isNotEmpty(portraitStr)){
                memberInfo.setPortraitStr(null);
                memberInfo.setPortrait(null);
                Image protrait = gson.fromJson(portraitStr,Image.class);
                memberInfo.setPortrait(protrait);
            }
            if(StringUtils.isNotEmpty(businessLicenseSN)){
                memberInfo.setBusinessLicenseSN(businessLicenseSN);
            }
            if(StringUtils.isNotEmpty(officeAddress)){
                memberInfo.setOfficeAddress(officeAddress);
            }
            if(StringUtils.isNotEmpty(organizationCode)){
                memberInfo.setOrganizationCode(organizationCode);
            }
            if(StringUtils.isNotEmpty(businessLicensePicStr)){
                memberInfo.setBusinessLicensePic(null);
                memberInfo.setBusinessLicensePicStr(null);
                Image businessLicensePric = gson.fromJson(businessLicensePicStr,Image.class);
                memberInfo.setBusinessLicensePic(businessLicensePric);
            }
            if(StringUtils.isNotEmpty(organizationCodePicStr)){
                memberInfo.setOrganizationCodePic(null);
                memberInfo.setOrganizationCodePicStr(null);
                Image organizationCodePic = gson.fromJson(organizationCodePicStr,Image.class);
                memberInfo.setOrganizationCodePic(organizationCodePic);
            }
            memberInfo.setCreateTime(nowDate.getTime());
            memberInfo.setUpdateTime(nowDate.getTime());
            if (StringUtils.isNotEmpty(officeTel)){
                memberInfo.setOfficeTel(officeTel);
            }
            if(StringUtils.isNotEmpty(apartment)){
                memberInfo.setApartment(apartment);
            }
            if(StringUtils.isNotEmpty(homePhone)){
                memberInfo.setHomePhone(homePhone);
            }
            if(birthdate > 0){
                memberInfo.setBirthdate(birthdate);
            }
            if (StringUtils.isNotEmpty(birthplace)){
                memberInfo.setBirthplace(birthplace);
            }
            if(StringUtils.isNotEmpty(userDetail)){
                memberInfo.setUserDetail(userDetail);
            }
            if(StringUtils.isNotEmpty(education)){
                memberInfo.setEducation(education);
            }
            if(StringUtils.isNotEmpty(graduatedFrom)){
                memberInfo.setGraduatedFrom(graduatedFrom);
            }
            if(StringUtils.isNotEmpty(workUnit)){
                memberInfo.setWorkUnit(workUnit);
            }
            if(StringUtils.isNotEmpty(blood)){
                memberInfo.setBlood(blood);
            }

        }
        memberInfo = memberInfoService.saveMemberInfo(memberInfo);
        String message = flag ? "修改信息":"创建信息";
        if(memberInfo != null){
            if(!flag){
                Role role = roleService.getRoleByDefaule();
                Member member = memberInfo.getMember();
                List<Role> roles = new ArrayList<Role>();
                roles.add(role);
                roleService.setMemberRole(username,roles);
            }
            return new ReturnMessages(RequestState.SUCCESS,message + "成功.",memberInfo);
        }else{
            return new ReturnMessages(RequestState.ERROR,message + "失败.",null);
        }
    }


    /**
     * 获取自身信息
     * @return
     */
    @PostMapping(value = "/memberInfo/findMe")
    public ReturnMessages findMeMemberInfo(
            HttpServletRequest request
    ){
        MemberInfo memberInfo = null;
        String username = SecurityUtils.getUsername(request);
        if(username != null){
            memberInfo = memberInfoService.findMemberInfoByUsername(username);
        }else {
            return new ReturnMessages(RequestState.ERROR,"未登录。",null);
        }
        if(memberInfo != null){
            return new ReturnMessages(RequestState.SUCCESS,"已登录。",memberInfo);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"未完善资料。",username);
        }

    }

    /**
     * 获取自身的角色
     * @param request
     * @return
     */
    @PostMapping(value = "/memberInfo/findMyRole")
    public ReturnMessages findMyRole(
            HttpServletRequest request
    ){
        String username = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(username)){
            return new ReturnMessages(RequestState.ERROR,"请先登录用户。",null);
        }
        Member member = memberService.findMemberByUsername(username);
        List<Role> roles = roleService.findRoleByMember(member);
        if(roles == null && roles.size() <= 0){
            return new ReturnMessages(RequestState.ERROR,"你的用户权限不够。",null);
        }else {
            for (Role role : roles){

                if(role.getRoleCode().contains("ROLE:ADMIN")){
                    List<Permission> permissions = role.getPermissionList();
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("role",role);
                    map.put("permission",permissions);
                    return new ReturnMessages(RequestState.SUCCESS,"你是管理员用户。",map);
                }
                if(role.getRoleCode().contains("ROLE:SELLER")){
                    List<Permission> permissions = role.getPermissionList();
                    Map<String,Object> map = new HashMap<String, Object>();
                    map.put("role",role);
                    map.put("permission",permissions);
                    return new ReturnMessages(RequestState.SUCCESS,"你是商家用户。",map);
                }
            }
            return new ReturnMessages(RequestState.ERROR,"你的用户权限不够。",null);
        }
    }

    /**
     * 根据状态查询用户
     * @param state 用户状态
     * @param page 页码
     * @param pageSize 页面大小
     * @return
     */
    @PostMapping(value = "/memberInfo/findByState")
    public ReturnMessages findByState(
            @RequestParam(value = "state")String state,
            @RequestParam(value = "page",required = false,defaultValue = "0")int page,
            @RequestParam(value = "pageSize",required = false,defaultValue = "20")int pageSize
    ){
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        PageRequest pageRequest = new PageRequest(page,pageSize,sort);
        Page<MemberInfo> memberInfoPage = memberInfoService.findMemberInfoByState(state,pageRequest);
        if(memberInfoPage.getContent() != null && memberInfoPage.getContent().size() > 0){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功。",memberInfoPage);
        }else{
            return new ReturnMessages(RequestState.ERROR,"未查询到用户信息。",null);
        }
    }

    /**
     * 审核用户        [用户审核通过的提示创建预订单信息]
     * @param username 用户名
     * @param state 状态
     * @return
     */
    @PostMapping(value = "/memberInfo/check")
    public ReturnMessages checkMember(
            @RequestParam(value = "username")String username,
            @RequestParam(value = "state")String state
    ){
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        memberInfo.setState(state);
        memberInfo = memberInfoService.updateMemberInfo(memberInfo);
        if(memberInfo != null){
            MakeOrder makeOrder = makeOrderService.findMakeOrderByMember(username);
            if(makeOrder == null){
                makeOrder= new MakeOrder(username);//实例化预订单实体
                makeOrderService.saveMakeOrder(makeOrder);
            }
            return new ReturnMessages(RequestState.SUCCESS,"操作成功。",memberInfo);
        }else{
            return new ReturnMessages(RequestState.ERROR,"操作失败。",null);
        }
    }


//
//    /**
//     * 根据用户 查找用户配置信息
//     * @param memberId
//     * @return
//     */
//    @RequestMapping("/findMemberInfoByMember")
//    public ReturnMessages findMemberInfoByMember(String memberId){
//
//        ReturnMessages rm = new ReturnMessages();
//        try {
//            if (memberId!=null){
//                Member member = memberService.findMemberByMemberId(memberId);
//               MemberInfo memberInfo =  memberInfoService.findMemberInfoByMember(member);
//               if (memberInfo!=null) {
//                   rm.setContent(memberInfo);
//                   rm.setMessages("用户配置信息查询成功");
//                   rm.setState(RequestState.SUCCESS);
//                   return rm;
//               }else{
//                   rm.setState(RequestState.ERROR);
//                   rm.setMessages("没有查询到用户配置信息");
//                   return rm;
//               }
//            }else{
//                rm.setMessages("参数请求错误");
//                rm.setState(RequestState.ERROR);
//                return rm;
//            }
//        }catch (Exception e){
//            rm.setState(RequestState.ERROR);
//            rm.setMessages("服务器请求异常");
//            return rm;
//        }
//    }
//
//    /**
//     * 增加用户配置信息
//     * @param portraitStr 用户头像 可为空
//     * @param officeTel 用户办公室电话 可为空
//     * @param apartment 用户住址 可为空
//     * @param homePhone 用户家庭电话 可为空
//     * @param birthdate 用户出生日期 可为空
//     * @param birthplace 用户出生地址 可为空
//     * @param userDetail 用户个人简介 可为空
//     * @param education 用户学历 可为空
//     * @param graduatedFrom 用户毕业学校 可为空
//     * @param workUnit 用户工作单位 可为空
//     * @param blood 用户血型 可为空
//     * @param memberId 配置信息对应用户 不为空
//     * @return
//     */
//    @RequestMapping("/saveMemberInfo")
//    public ReturnMessages saveMemberInfo(@RequestParam(name = "portraitStr",required = false)String portraitStr,
//                                         @RequestParam(name = "officeTel",required = false)String officeTel,
//                                         @RequestParam(name = "apartment",required = false)String apartment,
//                                         @RequestParam(name = "homePhone",required = false)String homePhone,
//                                         @RequestParam(name = "birthdate",required = false)Long birthdate,
//                                         @RequestParam(name = "birthplace",required = false)String birthplace,
//                                         @RequestParam(name = "userDetail",required = false)String userDetail,
//                                         @RequestParam(name = "education",required = false)String education,
//                                         @RequestParam(name = "graduatedFrom",required = false)String graduatedFrom,
//                                         @RequestParam(name = "workUnit",required = false)String workUnit,
//                                         @RequestParam(name = "blood",required = false)String blood,
//                                         @RequestParam(name = "memberId",required = true)String memberId){
//        ReturnMessages rm = new ReturnMessages();
//        MemberInfo memberInfo = new MemberInfo();
//        try {
//            if (StringUtils.isNotEmpty(portraitStr)){
//                Gson gson = new Gson();
//                Image image = gson.fromJson(portraitStr,Image.class);
//                memberInfo.setPortrait(image);
//            }
//            if (StringUtils.isNotEmpty(officeTel)) {
//                memberInfo.setOfficeTel(officeTel);
//            }
//            if (StringUtils.isNotEmpty(apartment)){
//                memberInfo.setApartment(apartment);
//            }
//            if (StringUtils.isNotEmpty(homePhone)){
//                memberInfo.setHomePhone(homePhone);
//            }
//            if (birthdate!=null){
//                memberInfo.setBirthdate(birthdate);
//            }
//            if(StringUtils.isNotEmpty(birthplace)){
//                memberInfo.setBirthplace(birthplace);
//            }
//            if(StringUtils.isNotEmpty(userDetail)){
//                memberInfo.setUserDetail(userDetail);
//            }
//            if (StringUtils.isNotEmpty(education)){
//                memberInfo.setEducation(education);
//            }
//            if (StringUtils.isNotEmpty(graduatedFrom)){
//                memberInfo.setGraduatedFrom(graduatedFrom);
//            }
//            if (StringUtils.isNotEmpty(workUnit)){
//                memberInfo.setWorkUnit(workUnit);
//            }
//            if (StringUtils.isNotEmpty(blood)){
//                memberInfo.setBlood(blood);
//            }
//            if (StringUtils.isNotEmpty(memberId)){
//                Member member = memberService.findMemberByMemberId(memberId);
//                memberInfo.setMember(member);
//            }else {
//                rm.setState(RequestState.ERROR);
//                rm.setMessages("参数请求异常");
//                return rm;
//            }
//            MemberInfo memberInfoRes = new MemberInfo();
//            memberInfoRes = memberInfoService.saveMemberInfo(memberInfo);
//            if (memberInfoRes!=null){
//                rm.setMessages("用户配置信息保存成功");
//                rm.setState(RequestState.SUCCESS);
//                rm.setContent(memberInfoRes);
//                return rm;
//            }else {
//                rm.setMessages("用户信息保存失败");
//                rm.setState(RequestState.ERROR);
//                return rm;
//            }
//        }catch (Exception e){
//            rm.setState(RequestState.ERROR);
//            rm.setMessages("服务器请求异常");
//            return rm;
//        }
//    }
//
//    /**
//     * 修改用户配置信息
//     * @param portraitStr
//     * @param officeTel
//     * @param apartment
//     * @param homePhone
//     * @param birthdate
//     * @param birthplace
//     * @param userDetail
//     * @param education
//     * @param graduatedFrom
//     * @param workUnit
//     * @param blood
//     * @return
//     */
//    @RequestMapping("/updateMemberInfo")
//    public ReturnMessages updateMemberInfo(@RequestParam(name = "portraitStr",required = false)String portraitStr,
//                                           @RequestParam(name = "officeTel",required = false)String officeTel,
//                                           @RequestParam(name = "apartment",required = false)String apartment,
//                                           @RequestParam(name = "homePhone",required = false)String homePhone,
//                                           @RequestParam(name = "birthdate",required = false)Long birthdate,
//                                           @RequestParam(name = "birthplace",required = false)String birthplace,
//                                           @RequestParam(name = "userDetail",required = false)String userDetail,
//                                           @RequestParam(name = "education",required = false)String education,
//                                           @RequestParam(name = "graduatedFrom",required = false)String graduatedFrom,
//                                           @RequestParam(name = "workUnit",required = false)String workUnit,
//                                           @RequestParam(name = "blood",required = false)String blood,
//                                           @RequestParam(name = "memberId",required = true)String memberId){
//        ReturnMessages rm = new ReturnMessages();
//        Member member = memberService.findMemberByMemberId(memberId);
//        MemberInfo memberInfo = memberInfoService.findMemberInfoByMember(member);
//        if (memberInfo!=null) {
//            try {
//                if (StringUtils.isNotEmpty(portraitStr)) {
//                    Gson gson = new Gson();
//                    Image image = gson.fromJson(portraitStr, Image.class);
//                    memberInfo.setPortrait(image);
//                }
//                if (StringUtils.isNotEmpty(officeTel)) {
//                    memberInfo.setOfficeTel(officeTel);
//                }
//                if (StringUtils.isNotEmpty(apartment)) {
//                    memberInfo.setApartment(apartment);
//                }
//                if (StringUtils.isNotEmpty(homePhone)) {
//                    memberInfo.setHomePhone(homePhone);
//                }
//                if (birthdate != null) {
//                    memberInfo.setBirthdate(birthdate);
//                }
//                if (StringUtils.isNotEmpty(birthplace)) {
//                    memberInfo.setBirthplace(birthplace);
//                }
//                if (StringUtils.isNotEmpty(userDetail)) {
//                    memberInfo.setUserDetail(userDetail);
//                }
//                if (StringUtils.isNotEmpty(education)) {
//                    memberInfo.setEducation(education);
//                }
//                if (StringUtils.isNotEmpty(graduatedFrom)) {
//                    memberInfo.setGraduatedFrom(graduatedFrom);
//                }
//                if (StringUtils.isNotEmpty(workUnit)) {
//                    memberInfo.setWorkUnit(workUnit);
//                }
//                if (StringUtils.isNotEmpty(blood)) {
//                    memberInfo.setBlood(blood);
//                }
//                MemberInfo memberInfoRes = new MemberInfo();
//                memberInfoRes = memberInfoService.updateMemberInfo(memberInfo);
//                if (memberInfoRes != null) {
//                    rm.setMessages("用户配置信息修改成功");
//                    rm.setState(RequestState.SUCCESS);
//                    rm.setContent(memberInfoRes);
//                    return rm;
//                } else {
//                    rm.setMessages("用户信息修改失败");
//                    rm.setState(RequestState.ERROR);
//                    return rm;
//                }
//            } catch (Exception e) {
//                rm.setState(RequestState.ERROR);
//                rm.setMessages("服务器请求异常");
//                return rm;
//            }
//        }else {
//            rm.setMessages("该用户配置信息不存在");
//            rm.setState(RequestState.ERROR);
//            return rm;
//        }
//    }
}
