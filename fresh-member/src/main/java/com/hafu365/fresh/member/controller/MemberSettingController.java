package com.hafu365.fresh.member.controller;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.member.MemberSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户设置controller
 * Created by HuangWeizhen on 2017/9/24.
 */
@RestController
@RequestMapping("/memberSetting")
public class MemberSettingController {

    @Autowired
    private MemberSettingService memberSettingService;

    @Autowired
    private MemberService memberService;

    /**
     * 新增/修改用户设置
     * @param settlementInterval    出账后多久付账[可空]
     * @param settlementDate        出账日期[可空]
     * @param userName              用户名
     * @return
     */
    @RequestMapping("/save")
    public ReturnMessages saveMemberSetting(@RequestParam(name = "settlementInterval",required = false)int settlementInterval,
                                            @RequestParam(name= "settlementDate",required = false)int settlementDate,
                                            @RequestParam(name = "userName",required = true)String userName
    ){
        ReturnMessages rm = null;
        MemberSetting memberSetting = null;
        Member member = memberService.findMemberByUsername(userName);
        if(member == null){
            return new ReturnMessages(RequestState.ERROR,"用户不存在。",null);
        }
        memberSetting = memberSettingService.findMemberSettingByMember(member);
        String message = "";
        if(memberSetting != null){
            message = "修改设置";
            if(settlementInterval < 0){
                return new ReturnMessages(RequestState.ERROR,"出账时间不能为负数。",null);
            }
            if(settlementDate < 0){
                return new ReturnMessages(RequestState.ERROR,"出账日期不能为负数。",null);
            }

            memberSetting.setSettlementInterval(settlementInterval);
            memberSetting.setSettlementDate(settlementDate);
        }else{
            message = "新增设置";
            memberSetting = new MemberSetting();
            memberSetting.setMember(member);
            if(settlementInterval < 0){
                return new ReturnMessages(RequestState.ERROR,"出账时间不能为负数。",null);
            }
            if(settlementDate < 0){
                return new ReturnMessages(RequestState.ERROR,"出账日期不能为负数。",null);
            }

            memberSetting.setSettlementInterval(settlementInterval);
            memberSetting.setSettlementDate(settlementDate);
        }

        MemberSetting memberSettingRes = memberSettingService.saveMemberSetting(memberSetting);
        if(memberSettingRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,message + "成功.",memberSettingRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,message + "失败.",null);
        }
        return rm;
    }



    /**
     * 新增/修改自身用户设置
     * @param settlementInterval    出账后多久付账[可空]
     * @param settlementDate        出账日期[可空]
     * @return
     */
    @RequestMapping("/saveMe")
    public ReturnMessages saveMemberSetting(@RequestParam(name = "settlementInterval",required = false)int settlementInterval,
                                            @RequestParam(name= "settlementDate",required = false)int settlementDate,
                                            HttpServletRequest request
    ){
        ReturnMessages rm = null;
        MemberSetting memberSetting = null;
        String userName = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(userName);
        memberSetting = memberSettingService.findMemberSettingByMember(member);
        String message = "";
        if(memberSetting != null){
            message = "修改设置";
            if(settlementInterval < 0){
                return new ReturnMessages(RequestState.ERROR,"出账时间不能为负数。",null);
            }
            if(settlementDate < 0){
                return new ReturnMessages(RequestState.ERROR,"出账日期不能为负数。",null);
            }

            memberSetting.setSettlementInterval(settlementInterval);
            memberSetting.setSettlementDate(settlementDate);
        }else{
            message = "新增设置";
            memberSetting = new MemberSetting();
            memberSetting.setMember(member);
            if(settlementInterval < 0){
                return new ReturnMessages(RequestState.ERROR,"出账时间不能为负数。",null);
            }
            if(settlementDate < 0){
                return new ReturnMessages(RequestState.ERROR,"出账日期不能为负数。",null);
            }

            memberSetting.setSettlementInterval(settlementInterval);
            memberSetting.setSettlementDate(settlementDate);
        }

        MemberSetting memberSettingRes = memberSettingService.saveMemberSetting(memberSetting);
        if(memberSettingRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,message + "成功.",memberSettingRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,message + "失败.",null);
        }
        return rm;
    }

    /**
     * 查询用户设置
     * @param memberId  用户id
     * @return
     */
    @RequestMapping("/findByMember")
    public ReturnMessages findByMember(@RequestParam(name = "memberId",required = true)String memberId
    ){
        ReturnMessages rm = null;
        Member member = memberService.findMemberByMemberId(memberId);
        MemberSetting memberSetting = memberSettingService.findMemberSettingByMember(member);
        if(memberSetting != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"查询成功。",memberSetting);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"查询失败。",null);
        }
        return rm;
    }

    /**
     * 根据用户和出账日期查询用户设置
     * @param memberId          用户id
     * @param settlementDate    出账日期
     * @return
     */
    @RequestMapping("/findByMemberAndDay")
    public ReturnMessages findByMemberAndDay(@RequestParam(name = "memberId",required = true)String memberId,
                                             @RequestParam(name = "settlementDate",required = true)int settlementDate
    ){
        ReturnMessages rm = null;
        if(settlementDate < 0){
            return new ReturnMessages(RequestState.ERROR,"出账日期不能为负数。",null);
        }
        Member member = memberService.findMemberByMemberId(memberId);
        MemberSetting memberSetting = memberSettingService.findMemberSettingByMemberAndDay(member,settlementDate);
        if(memberSetting != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"查询成功。",memberSetting);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"查询失败。",null);
        }
        return rm;
    }
}
