package com.hafu365.fresh.order.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

/**
 * 判断当前登录用户的状态
 * Created by zhaihuilin on 2017/11/1  11:37.
 */
@Service
@Transactional
public class CheckUserUtil {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberInfoService memberInfoService;

    /**
     * 获取当前登录的用户
     * @return  member
     */
    public   Member  getMember(HttpServletRequest request){
        String  userName= SecurityUtils.getUsername(request);
        Member member=memberService.findMemberByUsername(userName);
        return  member;
    }

    /**
     * 获取过滤后的当前登录用户
     * @param request
     * @return member
     */
    public    Member   getFiltrationMember(HttpServletRequest request){
        Member member=getMember(request);
        if (member!=null){
            Gson gson = new GsonBuilder().setExclusionStrategies(retrunes()).create();
            String memberStr=gson.toJson(member);
            Type type = new TypeToken<Member>() {}.getType();
            member = gson.fromJson(memberStr,type);
        }
        return  member;
    }

    /**
     * 获取当前登录用户名
     * @param request
     * @return  getuserName
     */
    public  static String getuserName(HttpServletRequest request){
      String  userName= SecurityUtils.getUsername(request);
      return  userName;
    }

    /**
     * 获取当前登录用户名的信息
     * @param request
     * @return    memberInfo
     */
    public    MemberInfo getMemberInfo(HttpServletRequest request){
         Member member=getMember(request);
         MemberInfo memberInfo=memberInfoService.findMemberInfoByMember(member);
         return  memberInfo;
    }

    /**
     * 获取过滤后的当前登录用户信息
     * @param request
     * @return member
     */
    public  MemberInfo  getFiltrationMemberInfo(HttpServletRequest request){
        Member member=getMember(request);
        MemberInfo memberInfo=memberInfoService.findMemberInfoByMember(member);
        if (memberInfo!=null){
            Gson gson = new GsonBuilder().setExclusionStrategies(retrunes()).create();
            String memberInfoStr=gson.toJson(memberInfo);
            Type type = new TypeToken<MemberInfo>() {}.getType();
            memberInfo = gson.fromJson(memberInfoStr,type);
        }
        return  memberInfo;
    }
    /**
     * 获取用户状态
     * @param request
     * @return   状态信息
     */
    public   ReturnMessages  CheckMemberState(HttpServletRequest request){
          ReturnMessages returnMessages=new ReturnMessages();
          MemberInfo memberInfo=getMemberInfo(request);
          if (memberInfo==null){
               returnMessages=new ReturnMessages(RequestState.ERROR,"请完善好用户信息之后在进行相关操作！",null);
          }
          String state =memberInfo.getState();//获取用户状态
          if (state==null){
              returnMessages=new ReturnMessages(RequestState.ERROR,"您的账号出现异常，请联系管理员后在进行操作！",null);
          }
          if(state.equals(StateConstant.USER_STATE_CHECK_ING.toString())){
              returnMessages=new ReturnMessages(RequestState.ERROR,"您的账号还未通过审核请求，联系管理员审核后在进行操作。",null);
          }
          if(state.equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
              returnMessages=new ReturnMessages(RequestState.ERROR,"您的账号因为某些原因没有通过审核，请联系管理员后在进行操作。",null);
          }
          if(state.equals(StateConstant.USER_STATE_LOCK_ING.toString())){
              returnMessages=new ReturnMessages(RequestState.ERROR,"您的账号已经被系统锁定，请联系管理员后操作。",null);
          }
          return returnMessages;
    }

    /**
     * 过滤用户的信息 避免出现堆栈溢出
     * @return
     */
    public ExclusionStrategy retrunes(){
        ExclusionStrategy es = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fa) {
                return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList")||fa.getName().equals("permissionList") ||fa.getName().equals("brandList")||fa.getName().equals("goodsClass") ||fa.getName().equals("brand");
            }
            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };
        return  es;
    }
}
