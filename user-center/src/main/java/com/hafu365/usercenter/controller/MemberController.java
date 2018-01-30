package com.hafu365.usercenter.controller;

import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.utils.MD5Util;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.member.MemberRepository;
import com.hafu365.fresh.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import sun.security.util.SecurityConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 用户中心
 * Created by SunHaiyang on 2017/8/3.
 */
@RestController
@RequestMapping(value = "/member",method = {RequestMethod.GET,RequestMethod.POST})
public class MemberController {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    /**
     * 注册 用户
     * @param username 用户名
     * @param password 密码
     * @param nickName 昵称[可空]
     * @param name 真实姓名
     * @param phone 电话
     * @param eMail 邮箱[可空]
     * @param idCardNo 身份证号[可空]
     * @param idCardPicStr 身份证照[可空]
     * @return 返回消息
     */
    @RequestMapping(value = "/register")
    public ReturnMessages saveMember(
            @RequestParam(value = "username" ,required = true) String username,
            @RequestParam(value = "password" ,required = true) String password,
            @RequestParam(value = "nickName" ,required = false) String nickName,
            @RequestParam(value = "name",required = false)String name,
            @RequestParam(value = "phone" ,required = true) String phone,
            @RequestParam(value = "eMail" , required = false) String eMail,
            @RequestParam(value = "idCardNo" ,required = false) String idCardNo,
            @RequestParam(value = "idCardPicStr" , required = false) String idCardPicStr,
            HttpServletRequest request
            ){
        ReturnMessages returnMessages = null;
        Member member;
        if(memberService.existMemberbyUsername(username)){
            returnMessages = new ReturnMessages(RequestState.ERROR,"用户名已存在。",null);
        }else {
            if(username.length() < 3){
                return new ReturnMessages(RequestState.ERROR,"用户名长度不可低于3个字符。",null);
            }
            member = new Member(username, MD5Util.string2MD5(password),phone);
            if(StringUtils.isNotEmpty(nickName)){
                member.setNickName(nickName);
            }
            if(StringUtils.isNotEmpty(name)){
                member.setName(name);
            }
            if(StringUtils.isNotEmpty(eMail)){
                member.setEMail(eMail);
            }
            if(StringUtils.isNotEmpty(idCardNo)){
                member.setIdCardNo(idCardNo);
            }
            if (StringUtils.isNotEmpty(idCardPicStr)){
                Gson gson = new Gson();
                member.setIdCardPic(gson.fromJson(idCardPicStr, Image.class));
            }
            member.setCreateTime(new Date().getTime());
            member = memberService.saveMember(member);
//            request.getSession().setAttribute("member",member);
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"注册成功。",member);
        }
        return returnMessages;
    }

    /**
     * 修改用户信息
     * @param memberId 用户ID
     * @param password 密码[可空]
     * @param nickName 昵称[可空]
     * @param name 真实姓名
     * @param phone 电话[可空]
     * @param eMail 邮箱[可空]
     * @param idCardNo 身份证号[可空]
     * @param idCardPicStr 身份证图片[可空]
     * @return 返回消息
     */
    @RequestMapping(value = "/update")
    public ReturnMessages updateMember(
            @RequestParam(value = "memberId",required = true)String memberId,
            @RequestParam(value = "password" ,required = false) String password,
            @RequestParam(value = "nickName" ,required = false) String nickName,
            @RequestParam(value = "name",required = false)String name,
            @RequestParam(value = "phone" ,required = false) String phone,
            @RequestParam(value = "eMail" , required = false) String eMail,
            @RequestParam(value = "idCardNo" ,required = false) String idCardNo,
            @RequestParam(value = "idCardPicStr" , required = false) String idCardPicStr,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = null;
        HttpSession session = request.getSession();
        Member member = memberService.findMemberByMemberId(memberId);
        if(password != null && password.length() > 0){
            member.setPassword(MD5Util.string2MD5(password));
        }
        if (nickName != null && nickName.length() > 0){
            member.setNickName(nickName);
        }
        if(StringUtils.isNotEmpty(name)){
            member.setName(name);
        }
        if(phone != null && phone.length() > 0){
            member.setPhone(phone);
        }
        if(eMail != null && eMail.length() > 0){
            member.setEMail(eMail);
        }
        if(idCardNo != null && idCardNo.length() > 0){
            member.setIdCardNo(idCardNo);
        }
        if(idCardPicStr != null && idCardPicStr.length() > 0){
            Gson gson = new Gson();
            member.setIdCardPic(gson.fromJson(idCardPicStr,Image.class));
        }
        member.setUpdateTime(new Date().getTime());
        if(memberService.saveMember(member) != null){
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"修改成功。",member);
        }else{
            returnMessages = new ReturnMessages(RequestState.ERROR,"修改失败。",null);
        }
        return returnMessages;
    }


    /**
     * 查询用户
     * @param memberId 用户ID[可空]
     * @param username 用户名[可空]
     * @param nickName 昵称[可空]
     * @param phone 电话[可空]
     * @param eMail 邮箱[可空]
     * @param idCardNo 身份证号[可空]
     * @param state 用户状态[可空]
     * @param del 是否逻辑删除[可空]
     * @param sort 排序方式[可空:默认="0 memberId";0=asc 1=desc]
     * @param page 页码[可空:默认=0]
     * @param pageSize 页面大小[可空:默认=20]
     * @return 返回消息
     */
    @RequestMapping(value = "/find")
    public ReturnMessages findMember(
            @RequestParam(value = "memberId" ,required = false,defaultValue = "") String memberId,
            @RequestParam(value = "username" ,required = false,defaultValue = "") String username,
            @RequestParam(value = "nickName" ,required = false,defaultValue = "") String nickName,
            @RequestParam(value = "name",required = false)String name,
            @RequestParam(value = "email",required = false)String email,
            @RequestParam(value = "phone" ,required = false,defaultValue = "") String phone,
            @RequestParam(value = "eMail" , required = false,defaultValue = "") String eMail,
            @RequestParam(value = "idCardNo" ,required = false,defaultValue = "") String idCardNo,
            @RequestParam(value = "state" ,required = false,defaultValue = "") String state,
            @RequestParam(value = "del" ,required = false,defaultValue = "false") boolean del,
            @RequestParam(value = "sort" ,required = false,defaultValue = "0 createTime")String sort,
            @RequestParam(value = "page" ,required = false,defaultValue = "0") int page,
            @RequestParam(value = "pageSize" ,required = false,defaultValue = "20") int pageSize
    ){
        ReturnMessages returnMessages = null;
        Member member = new Member();
        if(memberId.length() > 0){
            member.setMemberId(memberId);
        }
        if(username.length() > 0){
            member.setUsername(username);
        }
        if(nickName.length() > 0){
            member.setNickName(nickName);
        }
        if(phone.length() > 0){
            member.setPhone(phone);
        }
        if(eMail.length() > 0){
            member.setEMail(eMail);
        }
        if(idCardNo.length() > 0){
            member.setIdCardNo(idCardNo);
        }
        if(state.length() > 0){
            member.setState(state);
        }
        if(StringUtils.isNotEmpty(name)){
            member.setName(name);
        }
        member.setDel(del);
        String[] tempStr = sort.split(" ");
        Sort.Direction direction = null;
        if (tempStr.equals(0)){
            direction = Sort.Direction.ASC;
        }else{
            direction = Sort.Direction.DESC;
        }
        Sort pageSort = new Sort(Sort.Direction.ASC,tempStr[1]);
        Pageable pageable = new PageRequest(page,pageSize,pageSort);
        Page<Member> members = memberService.findAllByMember(member,pageable);
        if(members.getSize() > 0){
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"查询成功。",members);
        }else {
            returnMessages = new ReturnMessages(RequestState.ERROR,"未查询到任何信息。",members);
        }
        return returnMessages;
    }

    /**
     * 逻辑删除用户
     * @param memberId 用户ID
     * @return 消息反馈
     */
    @RequestMapping(value = "/delete")
    public ReturnMessages delMember(
            @RequestParam(value = "memberId")String memberId
    ){
        ReturnMessages returnMessages = null;
        if(memberService.deleteByMemberId(memberId)){
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"删除成功。",null);
        }else{
            returnMessages = new ReturnMessages(RequestState.ERROR,"删除失败。",null);
        }
        return returnMessages;
    }

    /**14
     * 物理删除用户
     * @param memberId 用户ID
     * @return 消息反馈
     */
    @RequestMapping(value = "/physicallyDelete")
    public ReturnMessages physicallyDeleteMember(
            @RequestParam(value = "memberId") String memberId
    ){
        ReturnMessages returnMessages = null;
        if(memberService.physicallyDeleteByMemberId(memberId)){
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"删除成功。",null);
        }else{
            returnMessages = new ReturnMessages(RequestState.ERROR,"删除失败。",null);
        }
        return returnMessages;
    }


    /**
     * 或者登录用户信息
     * @return 消息反馈
     */
    @RequestMapping(value = "/getMe")
    public ReturnMessages getMe(HttpServletRequest request){
        ReturnMessages returnMessages = null;
        String username = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(username);
        if(member != null){
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"查询成功。",member);
        }else{
            returnMessages = new ReturnMessages(RequestState.ERROR,"查询失败。",member);
        }
        return returnMessages;
    }

    /**
     * 修改用户信息
     * @param password 密码[可空]
     * @param nickName 密码[可空]
     * @param phone 电话[可空]
     * @param eMail 邮箱[可空]
     * @param idCardNo 身份证号[可空]
     * @param idCardPicStr 身份证图片[可空]
     * @return 返回消息
     */
    @RequestMapping(value = "/updateMe")
    public ReturnMessages updateMe(
            @RequestParam(value = "password" ,required = false) String password,
            @RequestParam(value = "nickName" ,required = false) String nickName,
            @RequestParam(value = "phone" ,required = false) String phone,
            @RequestParam(value = "eMail" , required = false) String eMail,
            @RequestParam(value = "idCardNo" ,required = false) String idCardNo,
            @RequestParam(value = "idCardPicStr" , required = false) String idCardPicStr,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = null;
        HttpSession session = request.getSession();
        String username = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(username);
        if(password != null && password.length() > 0){
            member.setPassword(MD5Util.string2MD5(password));
        }
        if (nickName != null && nickName.length() > 0){
            member.setNickName(nickName);
        }
        if(phone != null && phone.length() > 0){
            member.setPhone(phone);
        }
        if(eMail != null && eMail.length() > 0){
            member.setEMail(eMail);
        }
        if(idCardNo != null && idCardNo.length() > 0){
            member.setIdCardNo(idCardNo);
        }
        if(idCardPicStr != null && idCardPicStr.length() > 0){
            Gson gson = new Gson();
            member.setIdCardPic(gson.fromJson(idCardPicStr,Image.class));
        }
        member.setUpdateTime(new Date().getTime());
        if(memberService.saveMember(member) != null){
            returnMessages = new ReturnMessages(RequestState.SUCCESS,"修改成功。",member);
        }else{
            returnMessages = new ReturnMessages(RequestState.ERROR,"修改失败。",null);
        }
        return returnMessages;
    }

    /**
     * 修改自身密码
     * @param password 密码
     * @param newPassword 新面膜
     * @param request request
     * @return 访问状态
     */
    @PostMapping(value = "/updatePassword")
    public ReturnMessages updatePassword(
            @RequestParam(name = "password")String password,
            @RequestParam(name = "newPassword")String newPassword,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = null;
        String username = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(username);
        if(StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(newPassword)){
            if(member.getPassword().equals(MD5Util.string2MD5(password))){
                member.setPassword(MD5Util.string2MD5(newPassword));
                member = memberService.saveMember(member);
                if(member != null){
                    returnMessages = new ReturnMessages(RequestState.SUCCESS,"密码修改成功。",member);
                }else{
                    returnMessages = new ReturnMessages(RequestState.ERROR,"密码修改失败。",null);
                }

            }else{
                returnMessages = new ReturnMessages(RequestState.ERROR,"密码错误。",null);
            }
        }else {
            returnMessages = new ReturnMessages(RequestState.ERROR,"密码不可为空。",null);
        }
        return returnMessages;
    }



}
