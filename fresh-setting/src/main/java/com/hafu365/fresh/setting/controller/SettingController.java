package com.hafu365.fresh.setting.controller;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.setting.BasicSetup;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.basicSetup.BasicSetupService;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 基本设置 实现层
 * Created by zhaihuilin on 2017/9/25  17:01.
 */
@RestController
@Log4j
@RequestMapping("/setting")
public class SettingController {
    @Autowired
    private BasicSetupService basicSetupService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberInfoService memberInfoService;
    /**
     * 新增
     * @param keyNames     键的名称
     * @param keyCode   值     格式： [秒] [分] [小时] [日] [月] [周] [年]
     * @return
     */
    @RequestMapping(value = "/saveBasicSetup")
    public ReturnMessages saveBasicSetup(
            @RequestParam(name = "keyNames",required=true,defaultValue = "")   String keyNames,
            @RequestParam(name = "keyCode",required=true,defaultValue = "")   String keyCode,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages=new ReturnMessages();
        //通过session来获取当前的用户
        String username = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(username)){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        Member member = memberService.findMemberByUsername(username);
        if(member == null){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        if(memberInfo == null){
            return new ReturnMessages(RequestState.ERROR,"请完善好用户信息之后在进行相关操作。",null);
        }else{
            String state=memberInfo.getState();
            if(state == null){
                return new ReturnMessages(RequestState.ERROR,"您的账号出现异常，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号还未通过审核请求，联系管理员审核后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号因为某些原因没有通过审核，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_LOCK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号已经被系统锁定，请联系管理员后操作。",null);
            }
        }
        if (keyNames ==null && keyNames.length()<0){
            returnMessages.setMessages("参数输入错误");
            returnMessages.setState(RequestState.ERROR);
            return returnMessages;
        }
        if (keyCode ==null && keyCode.length()<0 ){
            returnMessages.setMessages("参数输入错误");
            returnMessages.setState(RequestState.ERROR);
            return returnMessages;
        }
        if (StringUtils.isNotEmpty(keyNames)){
            if (basicSetupService.existBasicSetupbyKeyNames(keyNames)){
                returnMessages.setMessages("参数名已存在,请重新添加");
                returnMessages.setState(RequestState.ERROR);
                return returnMessages;
            }
        }
            BasicSetup basicSetup=new BasicSetup();
            String CHkeyCode="";
            if (keyCode !=null){
                  String[]  len=keyCode.split(",");
                  if (len !=null && len.length>0){
                        for (int i=0;i<len.length;i++){
                            CHkeyCode=CHkeyCode+len[i]+" ";
                        }
                  }
            }
            basicSetup.setKeyNames(keyNames);
            String SSS=CHkeyCode.substring(0,CHkeyCode.length()-1);
            basicSetup.setKeyCode(CHkeyCode.substring(0,CHkeyCode.length()-1));
            try {
                basicSetup=basicSetupService.saveBasicSetup(basicSetup);
                returnMessages.setMessages("新增成功");
                returnMessages.setState(RequestState.SUCCESS);
                returnMessages.setContent(basicSetup);
            }catch (Exception e){
                returnMessages.setMessages("新增失败");
                returnMessages.setState(RequestState.ERROR);
            }
        return  returnMessages;
    }

    /**
     * 编辑
     * @param id    设置编号
     * @param keyNames   键名称
     * @param keyCode  值
     * @return
     */
    @RequestMapping(value = "/updateBasicSetup")
    public ReturnMessages updateBasicSetup(
            @RequestParam(name = "id",required=true,defaultValue = "")   String id,
            @RequestParam(name = "keyNames",required=false,defaultValue = "")   String keyNames,
            @RequestParam(name = "keyCode",required=false,defaultValue = "")   String keyCode,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages=new ReturnMessages();
        //通过session来获取当前的用户
        String username = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(username)){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        Member member = memberService.findMemberByUsername(username);
        if(member == null){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        if(memberInfo == null){
            return new ReturnMessages(RequestState.ERROR,"请完善好用户信息之后在进行相关操作。",null);
        }else{
            String state=memberInfo.getState();
            if(state == null){
                return new ReturnMessages(RequestState.ERROR,"您的账号出现异常，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号还未通过审核请求，联系管理员审核后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号因为某些原因没有通过审核，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_LOCK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号已经被系统锁定，请联系管理员后操作。",null);
            }
        }
        if (id !=null && id.length()>0){
            BasicSetup basicSetup=basicSetupService.findBasicSetupById(id);
            if (basicSetup !=null){
                if (StringUtils.isNotEmpty(keyNames)){
                    basicSetup.setKeyNames(keyNames);
                }
                String CHkeyCode="";
                if (keyCode !=null){
                    String[]  len=keyCode.split(",");
                    if (len !=null && len.length>0){
                        for (int i=0;i<len.length;i++){
                           CHkeyCode=CHkeyCode+len[i]+" ";
                        }
                    }
                    String SSS=CHkeyCode.substring(0,CHkeyCode.length()-1);
                    basicSetup.setKeyCode(CHkeyCode.substring(0,CHkeyCode.length()-1));
                }
                try {
                    basicSetup=basicSetupService.updateBasicSetup(basicSetup);
                    returnMessages.setContent(basicSetup);
                    returnMessages.setState(RequestState.SUCCESS);
                    returnMessages.setMessages("编辑成功");
                }catch (Exception e){
                    returnMessages.setMessages("编辑失败");
                    returnMessages.setState(RequestState.ERROR);
                }
            }else{
                returnMessages.setMessages("编辑失败");
                returnMessages.setState(RequestState.ERROR);
            }
        }else {
            returnMessages.setMessages("没有此编号");
            returnMessages.setState(RequestState.ERROR);
        }
        return  returnMessages;
    }

    /**
     * 根据编号进行删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteBs")
    public  ReturnMessages deleteBs(
         @RequestParam(name = "id",required = true,defaultValue = "") String  id,
         HttpServletRequest request
    ){
        ReturnMessages returnMessages =new ReturnMessages();
        //通过session来获取当前的用户
        String username = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(username)){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        Member member = memberService.findMemberByUsername(username);
        if(member == null){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        if(memberInfo == null){
            return new ReturnMessages(RequestState.ERROR,"请完善好用户信息之后在进行相关操作。",null);
        }else{
            String state=memberInfo.getState();
            if(state == null){
                return new ReturnMessages(RequestState.ERROR,"您的账号出现异常，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号还未通过审核请求，联系管理员审核后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号因为某些原因没有通过审核，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_LOCK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号已经被系统锁定，请联系管理员后操作。",null);
            }
        }
        if (StringUtils.isNotEmpty(id)){
          BasicSetup basicSetup=basicSetupService.findBasicSetupById(id);
          if (basicSetup !=null){
              boolean flag= basicSetupService.deleteBasicSetupById(id);
              if (flag==true){
                     returnMessages.setMessages("删除成功");
                     returnMessages.setState(RequestState.SUCCESS);
              }else{
                     returnMessages.setMessages("删除失败");
                     returnMessages.setState(RequestState.ERROR);
              }
          }else {
              returnMessages.setMessages("没有该编号,删除失败");
              returnMessages.setState(RequestState.ERROR);
          }
        }
        return  returnMessages;
    }



    /**
     * 条件查询
     * @param id    编号
     * @param keyNames    键
     * @param page     页面
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/findAllBs")
    public  ReturnMessages findAllBs(
            @RequestParam(name = "id",required=false,defaultValue = "")   String id,
            @RequestParam(name = "keyNames",required=false,defaultValue = "")   String keyNames,
            @RequestParam(name = "page" ,required = false,defaultValue = "0") String page,
            @RequestParam(name = "pageSize" ,required = false,defaultValue = "20") String pageSize,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages=new ReturnMessages();
        //通过session来获取当前的用户
        String username = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(username)){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        Member member = memberService.findMemberByUsername(username);
        if(member == null){
            return new ReturnMessages(RequestState.ERROR,"请登录后在进行操作。",null);
        }
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        if(memberInfo == null){
            return new ReturnMessages(RequestState.ERROR,"请完善好用户信息之后在进行相关操作。",null);
        }else{
            String state=memberInfo.getState();
            if(state == null){
                return new ReturnMessages(RequestState.ERROR,"您的账号出现异常，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号还未通过审核请求，联系管理员审核后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号因为某些原因没有通过审核，请联系管理员后在进行操作。",null);
            }
            if(state.equals(StateConstant.USER_STATE_LOCK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号已经被系统锁定，请联系管理员后操作。",null);
            }
        }
        Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize));
        BasicSetup basicSetup=new BasicSetup();
        if (StringUtils.isNotEmpty(keyNames)){
            basicSetup.setKeyNames(keyNames);
        }
        if (StringUtils.isNotEmpty(id)){
            basicSetup.setId(id);
        }
        Page<BasicSetup> basicSetups= basicSetupService.findAll(basicSetup,pageable);
        if (basicSetups !=null && basicSetups.getContent().size()>0){
            returnMessages.setMessages("查询成功");
            returnMessages.setState(RequestState.SUCCESS);
            returnMessages.setContent(basicSetups);
        }else {
            returnMessages.setMessages("没有查询到数据");
            returnMessages.setState(RequestState.ERROR);
        }
        return  returnMessages;
    }
}
