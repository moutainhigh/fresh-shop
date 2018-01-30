package com.hafu365.fresh.goods.controller.store;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.BrandService;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.role.RoleService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺
 * Created by HuangWeizhen on 2017/8/7.
 */

@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private BrandService brandService;

    /**
     * 添加店铺
     * @param storeName         店铺名称
     * @param childMemberIds    店员id拼接的字符串[可空]
     * @param businessLicenseNo 公司执照编号
     * @param imgs              公司执照图片
     * @param address           店铺地址
     * @param tel               店铺电话
     * @param fax               店铺传真[可空]
     * @param about             店铺简介[可空]
     * @param regTime           公司成立时间[可空]
     * @return
     */
    @RequestMapping("/save")
    public ReturnMessages addStore(
                                   @RequestParam(name = "storeName",required = true)String storeName,
                                   @RequestParam(name = "childMemberIds",required = false)String childMemberIds,
                                   @RequestParam(name = "businessLicenseNo",required = true)String businessLicenseNo,
                                   @RequestParam(name = "imgs",required = true)String imgs,
                                   @RequestParam(name = "address",required = true)String address,
                                   @RequestParam(name = "tel",required = true)String tel,
                                   @RequestParam(name = "fax",required = false)String fax,
                                   @RequestParam(name = "about",required = true)String about,
                                   @RequestParam(name = "regTime",required = false)String regTime,
                                   HttpServletRequest request
                                   ){
        ReturnMessages rm = new ReturnMessages();
        Store store = new Store();
        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userName);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

        }
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(userName);
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
        Store userStore = storeService.findByMember(currentMember);
        if(userStore != null){  //用户已有店铺
            return new ReturnMessages(RequestState.ERROR,"您已有店铺!",userStore);
        }else{
            store.setMember(currentMember);
        }

        //获取用户角色
        List<Role> roleList = roleService.findRoleByMember(currentMember);
        String roleCodeStr = "";
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员，不能申请店铺（不能同时具有管理员和商家的角色）
            return new ReturnMessages(RequestState.ERROR,"管理员不能申请店铺!",null);
        }

        //设置店铺名称
        if(StringUtils.isNotEmpty(storeName)){
            Store storeSearch = storeService.findByStoreName(storeName);
            if(storeSearch != null){
                return new ReturnMessages(RequestState.ERROR,"店铺名称已存在!",null);
            }else{
                store.setStoreName(storeName);
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"店铺名称不能为空!",null);
        }

        //设置店员
        if(StringUtils.isNotEmpty(childMemberIds)){
            List<Member> childList = new ArrayList<Member>();
            if(childMemberIds.contains(",")){
                String[] idArray = childMemberIds.split(",");
                if(idArray.length > 0){
                    for(String idStr : idArray){
                        if(StringUtils.isNotEmpty(idStr)){
                            Member member = memberService.findMemberByMemberId(idStr);
                            if(member != null){
                                Store storeSearch = storeService.findBychildMember(idStr);
                                if(storeSearch != null){
                                    return new ReturnMessages(RequestState.ERROR,"店员已有所属店铺！",storeSearch);
                                }else{
                                    childList.add(member);
                                }

                            }else{
                                rm.setState(RequestState.ERROR);
                                rm.setMessages("店员不存在!");
                                return new ReturnMessages(RequestState.ERROR,"店员不存在!",null);
                            }
                        }
                    }
                }
            }else{
                Member member = memberService.findMemberByMemberId(childMemberIds);
                if(member !=  null){
                    Store storeSearch = storeService.findBychildMember(childMemberIds);
                    if(storeSearch != null){
                        return new ReturnMessages(RequestState.ERROR,"店员已有所属店铺！",storeSearch);
                    }else{
                        childList.add(member);
                    }

                }else{
                    return new ReturnMessages(RequestState.ERROR,"店员不存在!",null);
                }
            }
            if(childList.size() > 0){
                store.setChildMember(childList);
            }
        }

        //设置执照编号
        if(StringUtils.isNotEmpty(businessLicenseNo)){
            store.setBusinessLicenseNo(businessLicenseNo);
        }else{
            return new ReturnMessages(RequestState.ERROR,"执照编号不能为空!",null);
        }

        //设置执照图片
        if(StringUtils.isNotEmpty(imgs)){
            Gson gson = new Gson();
            Type type = new TypeToken<Image>() {}.getType();
            try{
                Image image = gson.fromJson(imgs,type);
                store.setBusinessLicense(image);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"执照图片参数有误！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"执照图片不能为空!",null);
        }

        //设置办公地址
        if(StringUtils.isNotEmpty(address)){
            List<String> officeAddress = new ArrayList<String>();
            if(address.contains(",")){
                String[] addressArray = address.split(",");
                for (String addressStr : addressArray){
                    officeAddress.add(addressStr);
                }
            }else{
                officeAddress.add(address);
            }
            store.setOfficeAddress(officeAddress);
        }else{
            return new ReturnMessages(RequestState.ERROR,"公司地址不能为空!",null);
        }
        //设置办公电话
        if(StringUtils.isNotEmpty(tel)){
            List<String> officeTel = new ArrayList<String>();
            if(tel.contains(",")){
                String[] telArray = tel.split(",");
                for(String telStr : telArray){
                    officeTel.add(telStr);
                }

            }else{
                officeTel.add(tel);

            }
            store.setOfficeTel(officeTel);
        }else{
            return new ReturnMessages(RequestState.ERROR,"公司电话不能为空!",null);
        }

        //设置传真
        if(StringUtils.isNotEmpty(fax)){
            List<String> faxes = new ArrayList<String>();
            if(fax.contains(",")){
                String[] faxArray = fax.split(",");
                for(String faxStr : faxArray){
                    faxes.add(faxStr);
                }
            }else {
                faxes.add(fax);
            }
            store.setFaxes(faxes);
        }

        //设置公司简介
        if(StringUtils.isNotEmpty(about)){
            store.setAbout(new StringBuffer(about));
        }else{
            return new ReturnMessages(RequestState.ERROR,"公司简介不能为空!",null);
        }
        //设置创建时间
        store.setCreateTime(System.currentTimeMillis());
        //设置公司成立时间
        if(StringUtils.isNotEmpty(regTime)){
            try{
                store.setRegTime(Long.valueOf(regTime));
            }catch(Exception e){
                return new ReturnMessages(RequestState.ERROR,"注册时间格式有误!",null);
            }
        }
        //设置删除状态
        store.setDel(false);
        //设置审核状态
        store.setState(StateConstant.STORE_STATE_ON_CHECKING.toString());

        Store storeRes = storeService.save(store);
        if(storeRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"保存成功！",storeRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"保存失败！",null);

        }
        return rm;
    }

    /**
     * 编辑店铺
     * @param storeId           店铺id
     * @param storeName         店铺名称[可空]
     * @param memberId          店主id[可空]
     * @param childMemberIds    店员id拼接的字符串[可空]
     * @param businessLicenseNo 公司执照编号[可空]
     * @param imgs               公司执照图片[可空]
     * @param address           店铺地址[可空]
     * @param tel               店铺电话[可空]
     * @param fax               店铺传真[可空]
     * @param about             店铺简介[可空]
     * @param regTime           公司成立时间[可空]
     * @param state             店铺审核状态[可空]["STORE_STATE_CHECK_ON","STORE_STATE_CHECK_OFF"]
     * @return
     */
    @RequestMapping("/update")
    public ReturnMessages updateStore(@RequestParam(name = "storeId",required = true)String storeId,
                                       @RequestParam(name = "storeName",required = false)String storeName,
                                       @RequestParam(name = "memberId",required = false)String memberId,
                                       @RequestParam(name = "childMemberIds",required = false)String childMemberIds,
                                       @RequestParam(name = "businessLicenseNo",required = false)String businessLicenseNo,
                                       @RequestParam(name = "imgs",required = false)String imgs,
                                       @RequestParam(name = "address",required = false)String address,
                                       @RequestParam(name = "tel",required = false)String tel,
                                       @RequestParam(name = "fax",required = false)String fax,
                                       @RequestParam(name = "about",required = false)String about,
                                       @RequestParam(name = "regTime",required = false)String regTime,
                                      @RequestParam(name = "state",required = false)String state,
                                      HttpServletRequest request
    ){

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userName);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

        }
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(userName);
        if(memberInfo == null){
            return new ReturnMessages(RequestState.ERROR,"请完善好用户信息之后在进行相关操作。",null);
        }else{
            String userState=memberInfo.getState();
            if(userState == null){
                return new ReturnMessages(RequestState.ERROR,"您的账号出现异常，请联系管理员后在进行操作。",null);
            }
            if(userState.equals(StateConstant.USER_STATE_CHECK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号还未通过审核请求，联系管理员审核后在进行操作。",null);
            }
            if(userState.equals(StateConstant.USER_STATE_CHECK_OFF.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号因为某些原因没有通过审核，请联系管理员后在进行操作。",null);
            }
            if(userState.equals(StateConstant.USER_STATE_LOCK_ING.toString())){
                return new ReturnMessages(RequestState.ERROR,"您的账号已经被系统锁定，请联系管理员后操作。",null);
            }
        }

        Store storeSearch = null;
        ReturnMessages rm = null;
        Store store = new Store();
        if(StringUtils.isNotEmpty(storeId)){
            storeSearch = storeService.findByStoreId(storeId);
            if(storeSearch == null ){
                return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
            }else{
                BeanUtils.copyProperties(storeSearch,store);
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"店铺id参数有误!",null);
        }
        //获取用户角色
        List<Role> roleList = roleService.findRoleByMember(currentMember);
        String roleCodeStr = "";
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        Store userStore = storeService.findByMember(currentMember);
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员，只能审核店铺
            //审核店铺
            if(StringUtils.isNotEmpty(state)){//设置店铺状态，审核场景
                if(state.equals(StateConstant.STORE_STATE_CHECK_ON.toString()) || state.equals(StateConstant.STORE_STATE_CHECK_OFF.toString())){
                    store.setState(state);
                    Store storeRes = storeService.update(store);
                    if(storeRes != null){
                        return new ReturnMessages(RequestState.SUCCESS,"审核成功！",storeRes);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"审核失败！",null);
                    }
                }else{
                    return new ReturnMessages(RequestState.ERROR,"状态参数有误!",null);
                }
            }else{
                if(!storeSearch.getMember().getUsername().equals(userName))
                    return new ReturnMessages(RequestState.ERROR,"没有权限编辑商家的店铺！",null);
            }

        }else if(roleCodeStr.contains("ROLE:SELLER")){  //是商家,只能编辑自己的店铺
            if(userStore == null){
                return new ReturnMessages(RequestState.ERROR,"您还没有店铺！",null);
            }else{
                String editStoreId = storeSearch.getStoreId();
                String userStoreId = userStore.getStoreId();
                if(!editStoreId.equals(userStoreId)){
                    return new ReturnMessages(RequestState.ERROR,"只能编辑自己的店铺！",null);
                }
            }

        }else{  //是用户，可以编辑自己申请的且审核未通过或待审核或关闭的店铺
            if(!(storeSearch.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) && storeSearch.getMember().getUsername().equals(userName)){

            }else{
                return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
            }

        }




        //设置店铺名称
        if(StringUtils.isNotEmpty(storeName)){
            Store nameSearchStore = storeService.findByStoreName(storeName);
            if(nameSearchStore != null && !(nameSearchStore.getStoreId().equals(storeId))){
                return new ReturnMessages(RequestState.ERROR,"店铺名称已存在!",nameSearchStore);
            }else{
                store.setStoreName(storeName);
            }
        }
        //设置店主
        if(StringUtils.isNotEmpty(memberId)){
            Member newMember = memberService.findMemberByMemberId(memberId);
            Member storeMember = store.getMember();
            if(newMember != null){
                if(storeMember != null){
                    if(!(newMember.getMemberId().equals(storeMember.getMemberId()))){ //新店主和旧店主不一样
                        Store memberStore = storeService.findByMember(newMember);
                        if(memberStore != null){
                            return new ReturnMessages(RequestState.ERROR,"店主已有店铺!",memberStore);
                        }else{
                            store.setMember(newMember);
                        }

                    }

                }else{
                    return new ReturnMessages(RequestState.ERROR,"该店铺没有店主，数据出现错误!",null);
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"用户不存在!",null);

            }
        }
        //设置店员
        if(StringUtils.isNotEmpty(childMemberIds)){
            List<Member> childList = new ArrayList<Member>();
            if(childMemberIds.contains(",")){
                String[] idArray = childMemberIds.split(",");
                if(idArray.length > 0){
                    for(String idStr : idArray){
                        if(StringUtils.isNotEmpty(idStr)){
                            Member member = memberService.findMemberByMemberId(idStr);
                            if(member != null){
                                Store storeRes = storeService.findBychildMember(idStr);
                                if(storeRes != null && !(storeRes.getStoreId().equals(storeId))){
                                    return new ReturnMessages(RequestState.ERROR,"店员已有所属店铺！",storeRes);
                                }else{
                                    childList.add(member);
                                }

                            }else{
                                return new ReturnMessages(RequestState.ERROR,"店员不存在!",null);
                            }
                        }
                    }
                }
            }else{
                Member member = memberService.findMemberByMemberId(childMemberIds);
                if(member !=  null){
                    Store storeRes = storeService.findBychildMember(childMemberIds);
                    if(storeRes != null && !(storeRes.getStoreId().equals(storeId))){
                        return new ReturnMessages(RequestState.ERROR,"店员已有所属店铺！",storeRes);
                    }else{
                        childList.add(member);
                    }

                }else{
                    return new ReturnMessages(RequestState.ERROR,"店员不存在!",null);
                }
            }
            if(childList.size() > 0){
                store.setChildMember(childList);
            }
        }else if(childMemberIds != null){//店员为空
            store.setChildMember(null);
        }

        //设置执照编号
        if(StringUtils.isNotEmpty(businessLicenseNo)){
            store.setBusinessLicenseNo(businessLicenseNo);
        }

        //设置执照图片
        if(StringUtils.isNotEmpty(imgs)){
            Gson gson = new Gson();
            Type type = new TypeToken<Image>() {}.getType();
            try{
                Image image = gson.fromJson(imgs,type);
                store.setBusinessLicense(image);
                store.setLicensePic(imgs);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"执照图片参数有误！",null);
            }

        }

        //设置办公地址
        if(StringUtils.isNotEmpty(address)){
            List<String> officeAddress = store.getOfficeAddress();
            if(officeAddress != null){
                officeAddress.clear();
            }else{
                officeAddress = new ArrayList<String>();
            }
            if(address.contains(",")){
                String[] addressArray = address.split(",");
                for (String addressStr : addressArray){
                    officeAddress.add(addressStr);
                }
            }else{
                officeAddress.add(address);
            }
            store.setOfficeAddress(officeAddress);
            store.setAddressStr(address);
        }
        //设置办公电话
        if(StringUtils.isNotEmpty(tel)){
            List<String> officeTel = new ArrayList<String>();
            if(tel.contains(",")){
                String[] telArray = tel.split(",");
                for(String telStr : telArray){
                    officeTel.add(telStr);
                }

            }else{
                officeTel.add(tel);

            }
            store.setOfficeTel(officeTel);
            store.setTelStr(tel);
        }

        //设置传真
        if(StringUtils.isNotEmpty(fax)){
            List<String> faxes = new ArrayList<String>();
            if(fax.contains(",")){
                String[] faxArray = fax.split(",");
                for(String faxStr : faxArray){
                    faxes.add(faxStr);
                }
            }else {
                faxes.add(fax);
            }
            store.setFaxes(faxes);
            store.setFaxeStr(fax);
        }

        //设置公司简介
        if(StringUtils.isNotEmpty(about)){
            store.setAbout(new StringBuffer(about));
        }

        //设置公司成立时间
        if(StringUtils.isNotEmpty(regTime)){
            try{
                store.setRegTime(Long.valueOf(regTime));
            }catch(Exception e){
                return new ReturnMessages(RequestState.ERROR,"注册时间格式有误!",null);
            }
        }
        //编辑店铺,若店铺审核通过，状态不改变；若店铺为审核未通过或店铺已关闭，状态改为待审核
        String storeState = store.getState();
        if(StringUtils.isNotEmpty(storeState) && (storeState.equals(StateConstant.STORE_STATE_CHECK_OFF.toString()) || storeState.equals(StateConstant.STORE_STATE_ON_CLOSE.toString()))){
            store.setState(StateConstant.STORE_STATE_ON_CHECKING.toString());
        }

        Store storeRes = storeService.update(store);
        if(storeRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"更新成功！",storeRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"更新失败！",null);
        }
        return rm;
    }

    /**
     * 物理删除店铺
     * @param storeId   店铺id
     * @return
     */
    @RequestMapping("/physicalDelete")
    public ReturnMessages physicalDelete(@RequestParam(name = "storeId",required = true)String storeId,
                                         HttpServletRequest request
    ){

        Store store = null;
        if(StringUtils.isNotEmpty(storeId)){
            store = storeService.findByStoreId(storeId);
        }else{
            return new ReturnMessages(RequestState.ERROR,"参数有误！",null);
        }

        if(store == null){
            return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);

        }else {
            Store defaultStore = storeService.findStoreByTheDefaultTrue();
            if (defaultStore != null && store.getStoreName().equals(defaultStore.getStoreName())) {
                return new ReturnMessages(RequestState.ERROR, "默认店铺不能删除！", null);
            } else {

                //获取当前用户
                String userName = SecurityUtils.getUsername(request);
                if (!StringUtils.isNotEmpty(userName)) {   //用户未登录
                    return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
                }
                Member member = memberService.findMemberByUsername(userName);
                if (member == null) { //用户不存在
                    return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

                }
                //获取用户角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                Store userStore = storeService.findByMember(member);
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员,只有管理员能删除店铺

                } else {
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }

                if (storeService.deleteStore(storeId)) {
                    return new ReturnMessages(RequestState.SUCCESS, "删除成功!", null);
                }
                return new ReturnMessages(RequestState.ERROR, "删除失败!", null);
            }
        }
    }

    /**
     * 逻辑删除店铺
     * @param storeId   店铺id
     * @return
     */
    @RequestMapping("/delete")
    public ReturnMessages deleteByStoreId(@RequestParam(name = "storeId",required = true)String storeId,
                                          HttpServletRequest request
                                          ){
        Store store = null;
        if(StringUtils.isNotEmpty(storeId)){
            store = storeService.findByStoreId(storeId);
        }else{
            return new ReturnMessages(RequestState.ERROR,"参数有误！",null);
        }

        if(store == null){
            return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);

        }else{
            Store defaultStore = storeService.findStoreByTheDefaultTrue();
            if(defaultStore != null && store.getStoreName().equals(defaultStore.getStoreName())){
                return new ReturnMessages(RequestState.ERROR,"默认店铺不能删除！",null);
            }else {

                //获取当前用户
                String userName = SecurityUtils.getUsername(request);
                if (!StringUtils.isNotEmpty(userName)) {   //用户未登录
                    return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
                }
                Member member = memberService.findMemberByUsername(userName);
                if (member == null) { //用户不存在
                    return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

                }
                //获取用户角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                Store userStore = storeService.findByMember(member);
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员,只有管理员能删除店铺

                } else {
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }

                if (storeService.deleteByStoreId(storeId)) {
                    return new ReturnMessages(RequestState.SUCCESS, "删除成功!", null);
                }
                return new ReturnMessages(RequestState.ERROR, "删除失败!", null);
            }
        }
    }

    /**
     * 关闭店铺
     * @param storeId   店铺id
     * @return
     */
    @RequestMapping("/close")
    public ReturnMessages closeStore(@RequestParam(name = "storeId",required = true)String storeId,
                                     HttpServletRequest request
                                     ){
        ReturnMessages rm = null;
        Store store = storeService.findByStoreId(storeId);
        if(store != null){
            Store defaultStore = storeService.findStoreByTheDefaultTrue();
            if(defaultStore != null && store.getStoreName().equals(defaultStore.getStoreName())){
                return new ReturnMessages(RequestState.ERROR,"默认店铺不能删除!",null);
            }
            //获取当前用户
            String userName = SecurityUtils.getUsername(request);
            if(!StringUtils.isNotEmpty(userName)){   //用户未登录
                return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
            }
            Member member = memberService.findMemberByUsername(userName);
            if(member == null){ //用户不存在
                return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

            }
            //获取用户角色
            List<Role> roleList = roleService.findRoleByMember(member);
            String roleCodeStr = "";
            if(roleList != null && roleList.size() > 0){
                for(Role role : roleList){
                    String roleCode = role.getRoleCode();
                    roleCodeStr = roleCodeStr + roleCode + ",";
                }
            }
            Store userStore = storeService.findByMember(member);
            if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,可以关闭店铺

            }else if(roleCodeStr.contains("ROLE:SELLER")){  //是商家，可以关闭自己的店铺
                if(userStore != null && userStore.getStoreId().equals(storeId)){

                }else{
                    return new ReturnMessages(RequestState.ERROR,"不能关闭其他商户的店铺！",null);
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
            }
            if(storeService.closeStore(storeId)){

                Member storeMember = store.getMember();
                List<Role> newRoleList = new ArrayList<Role>();
                if(storeMember != null){
                    //获取店主角色
                    List<Role> storeMemberRoleList = roleService.findRoleByMember(storeMember);
                    if(storeMemberRoleList != null && storeMemberRoleList.size() > 0){
                        for(Role role : storeMemberRoleList){
                            if(!role.getRoleCode().contains("ROLE:SELLER")){
                                newRoleList.add(role);
                            }
                        }
                    }
                    //关闭店铺时删除用户的所有商家角色
                    roleService.setMemberRole(storeMember.getUsername(),newRoleList);

                }

                rm = new ReturnMessages(RequestState.SUCCESS,"关闭成功!",null);
            }else{
                rm = new ReturnMessages(RequestState.ERROR,"关闭失败!",null);
            }

        }else{
            rm = new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
        }
        return rm;
    }

    /**
     * 查询用户店铺
     * @param request
     * @return
     */
    @RequestMapping("/findMyStore")
    public ReturnMessages findMyStore(HttpServletRequest request){
        String userName = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(userName);
        List<Role> roleList = roleService.findRoleByMember(member);
        String roleCodeStr = "";
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,不返回店铺
            return new ReturnMessages(RequestState.SUCCESS,"我是管理员！",null);
        }
        Store store = storeService.findByMember(member);
        if(store != null){
            if(store.getState().contains(StateConstant.STORE_STATE_CHECK_ON.toString())){   //店铺审核通过
                return new ReturnMessages(RequestState.SUCCESS,"查询成功！",store);
            }else{
                return new ReturnMessages(RequestState.SUCCESS,"店铺未通过审核！",store);
            }
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",null);
        }
    }

    /**
     * 查询默认店铺
     * @param request
     * @return
     */
    @RequestMapping("/findInitStore")
    public ReturnMessages findInitStore(HttpServletRequest request){
        Store store = null;
        store = storeService.findStoreByTheDefaultTrue();

        return new ReturnMessages(RequestState.SUCCESS,"查询成功！",store);
    }

    /**
     * 查询审核通过的店铺
     * @param request
     * @return
     */
    @RequestMapping("/findCheckedStore")
    public ReturnMessages findCheckedStore(HttpServletRequest request){
        List<Store> storeList = storeService.findByDelFalseAndState(StateConstant.STORE_STATE_CHECK_ON.toString());
        if(storeList != null && storeList.size() > 0 ){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",storeList);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",null);
        }
    }

    //**查询店铺，前后端查询分开，(商家登录前台，可以查看所有前端显示的店铺)(用户不允许登录后台查询店铺)
    /**
     * 分页查询店铺
     * @param storeId           店铺id[可空]
     * @param storeName         店铺名称[可空]
     * @param memberId          店主id[可空]
     * @param businessLicenseNo 公司执照编号[可空]
     * @param regTime           公司成立时间[可空]
     * @param state             店铺审核状态[可空]["STORE_STATE_ON_CHECKING","STORE_STATE_CHECK_ON","STORE_STATE_CHECK_OFF","STORE_STATE_ON_CLOSE"]
     * @param startTime         查询起始时间[可空][搜索店铺创建时间所在区间]
     * @param endTime           查询结束时间[可空][搜索店铺创建时间所在区间]
     * @param pageNum           请求的页码[可空][默认初始页，值为0]
     * @param pageSize          分页大小[可空][默认为5]
     * @param pageSort          分页排序[可空][默认按"createTime"排序]
     * @param sortDirection     分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/selectFrontStore")
    public ReturnMessages selectFrontStore(@RequestParam (name = "storeId",required = false)String storeId,
                                      @RequestParam (name = "storeName",required = false)String storeName,
                                      @RequestParam (name = "memberId",required = false)String memberId,
                                      @RequestParam(name = "businessLicenseNo",required = false)String businessLicenseNo,
                                      @RequestParam(name = "regTime",required = false)String regTime,
                                      @RequestParam(name = "state",required = false)String state,
                                      @RequestParam(name = "startTime",required = false)String startTime,
                                      @RequestParam(name = "endTime",required = false)String endTime,
                                      @RequestParam(name = "pageNum",required = false)String pageNum,
                                      @RequestParam(name = "pageSize",required = false)String pageSize,
                                      @RequestParam(name = "pageSort",required = false)String pageSort,
                                      @RequestParam(name = "sortDirection",required = false)String sortDirection,
                                      HttpServletRequest request
    ){
        ReturnMessages rm = new ReturnMessages();
        Map<String,Object> mapParam = new HashMap<String,Object>();
        Store store = new Store();

        //设置店铺id
        if(StringUtils.isNotEmpty(storeId)){
            Store storeSearch = storeService.findByStoreId(storeId);
            if(storeSearch == null){
                return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
            }else{
                store.setStoreId(storeId);
            }
        }
        //设置店铺名称
        if(StringUtils.isNotEmpty(storeName)){
            store.setStoreName(storeName);
        }
        //设置店主
        if(StringUtils.isNotEmpty(memberId)){
            Member member = memberService.findMemberByMemberId(memberId);
            if(member != null){
                store.setMember(member);
            }else{
                return new ReturnMessages(RequestState.ERROR,"店主不存在!",null);

            }
        }

        //设置执照编号
        if(StringUtils.isNotEmpty(businessLicenseNo)){
            store.setBusinessLicenseNo(businessLicenseNo);
        }

        //设置公司成立时间
        if(StringUtils.isNotEmpty(regTime)){
            try{
                store.setRegTime(Long.valueOf(regTime));
            }catch(Exception e){
                return new ReturnMessages(RequestState.ERROR,"注册时间格式有误!",null);
            }
        }
        //设置店铺状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.STORE_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.STORE_STATE_CHECK_ON.toString()) || state.equals(StateConstant.STORE_STATE_CHECK_OFF.toString()) || state.equals(StateConstant.STORE_STATE_ON_CLOSE.toString())){
                store.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"状态参数有误!",null);
            }
        }

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在，获取用户角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员
                    //do nothing
                }else if(roleCodeStr.contains("ROLE:SELLER")){   //是商家,也可以查询前端店铺
                    /*Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //直接返回我的店铺
                        return new ReturnMessages(RequestState.SUCCESS, "查询成功！", userStore);
                    }*/
                    store.setDel(false);
                    store.setState(StateConstant.STORE_STATE_CHECK_ON.toString());

                }else{  //买家<相当于前端请求，店铺查询默认未删除，审核通过>
                    store.setDel(false);
                    store.setState(StateConstant.STORE_STATE_CHECK_ON.toString());
                }

            }else{  //用户不存在，等同于未登录，可以查看前端店铺
                store.setDel(false);
                store.setState(StateConstant.STORE_STATE_CHECK_ON.toString());
            }

        }else{  //未登录，可以查看前端店铺
            store.setDel(false);
            store.setState(StateConstant.STORE_STATE_CHECK_ON.toString());
        }

        mapParam.put("store",store);

        //设置创建时间条件查询
        long sTime = 0l;
        if(StringUtils.isNotEmpty(startTime)){
            try{
                sTime = Long.valueOf(startTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"起始时间参数格式有误!",null);
            }
        }
        mapParam.put("startTime",sTime);
        long eTime = 0l;
        if(StringUtils.isNotEmpty(endTime)){
            try{
                eTime = Long.valueOf(endTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"结束时间参数格式有误!",null);
            }
        }
        mapParam.put("endTime",eTime);

        //设置查询分页
        UtilPage page = new UtilPage(0,5,"createTime", Sort.Direction.DESC);
        if(StringUtils.isNotEmpty(pageNum)){
            try{
                page.setPageNum(Integer.valueOf(pageNum));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分页页数格式有误!",null);
            }
        }

        if(StringUtils.isNotEmpty(pageSize)){
            try{
                page.setPageSize(Integer.valueOf(pageSize));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分页大小格式有误!",null);
            }
        }

        if(StringUtils.isNotEmpty(pageSort)){
            page.setPageSort(pageSort);
        }

        if(StringUtils.isNotEmpty(sortDirection)){
            if(sortDirection.equals("asc")){
                page.setDirection(Sort.Direction.ASC);
            }else if(sortDirection.equals("desc")){
                page.setDirection(Sort.Direction.DESC);
            }else{
                return new ReturnMessages(RequestState.ERROR,"分页排序参数有误!",null);
            }

        }

        mapParam.put("page",page);

        Page<Store> storePage = storeService.findByCondition(mapParam);

        if(storePage != null && storePage.getContent() != null && storePage.getContent().size() > 0){
            rm.setMessages("查询成功!");
        }else{
            rm.setMessages("暂无数据!");
        }
        rm.setState(RequestState.SUCCESS);
        rm.setContent(storePage);
        return rm;
    }


    //查询店铺，添加后端查询，用户不允许进入
    /**
     * 分页查询店铺
     * @param storeId           店铺id[可空]
     * @param storeName         店铺名称[可空]
     * @param memberId          店主id[可空]
     * @param businessLicenseNo 公司执照编号[可空]
     * @param regTime           公司成立时间[可空]
     * @param state             店铺审核状态[可空]["STORE_STATE_ON_CHECKING","STORE_STATE_CHECK_ON","STORE_STATE_CHECK_OFF","STORE_STATE_ON_CLOSE"]
     * @param startTime         查询起始时间[可空][搜索店铺创建时间所在区间]
     * @param endTime           查询结束时间[可空][搜索店铺创建时间所在区间]
     * @param pageNum           请求的页码[可空][默认初始页，值为0]
     * @param pageSize          分页大小[可空][默认为5]
     * @param pageSort          分页排序[可空][默认按"createTime"排序]
     * @param sortDirection     分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/selectStore")
    public ReturnMessages selectStore(@RequestParam (name = "storeId",required = false)String storeId,
                                      @RequestParam (name = "storeName",required = false)String storeName,
                                      @RequestParam (name = "memberId",required = false)String memberId,
                                      @RequestParam(name = "businessLicenseNo",required = false)String businessLicenseNo,
                                      @RequestParam(name = "regTime",required = false)String regTime,
                                      @RequestParam(name = "state",required = false)String state,
                                      @RequestParam(name = "startTime",required = false)String startTime,
                                      @RequestParam(name = "endTime",required = false)String endTime,
                                      @RequestParam(name = "pageNum",required = false)String pageNum,
                                      @RequestParam(name = "pageSize",required = false)String pageSize,
                                      @RequestParam(name = "pageSort",required = false)String pageSort,
                                      @RequestParam(name = "sortDirection",required = false)String sortDirection,
                                      HttpServletRequest request
    ){
        ReturnMessages rm = new ReturnMessages();
        Map<String,Object> mapParam = new HashMap<String,Object>();
        Store store = new Store();

        //设置店铺id
        if(StringUtils.isNotEmpty(storeId)){
            Store storeSearch = storeService.findByStoreId(storeId);
            if(storeSearch == null){
                return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
            }else{
                store.setStoreId(storeId);
            }
        }
        //设置店铺名称
        if(StringUtils.isNotEmpty(storeName)){
            store.setStoreName(storeName);
        }
        //设置店主
        if(StringUtils.isNotEmpty(memberId)){
            Member member = memberService.findMemberByMemberId(memberId);
            if(member != null){
                store.setMember(member);
            }else{
                return new ReturnMessages(RequestState.ERROR,"店主不存在!",null);

            }
        }

        //设置执照编号
        if(StringUtils.isNotEmpty(businessLicenseNo)){
            store.setBusinessLicenseNo(businessLicenseNo);
        }

        //设置公司成立时间
        if(StringUtils.isNotEmpty(regTime)){
            try{
                store.setRegTime(Long.valueOf(regTime));
            }catch(Exception e){
                return new ReturnMessages(RequestState.ERROR,"注册时间格式有误!",null);
            }
        }
        //设置店铺状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.STORE_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.STORE_STATE_CHECK_ON.toString()) || state.equals(StateConstant.STORE_STATE_CHECK_OFF.toString()) || state.equals(StateConstant.STORE_STATE_ON_CLOSE.toString())){
                store.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"状态参数有误!",null);
            }
        }

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在，获取用户角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员
                    //do nothing
                }else if(roleCodeStr.contains("ROLE:SELLER")){   //是商家
                    Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //直接返回我的店铺
                        return new ReturnMessages(RequestState.SUCCESS, "查询成功！", userStore);
                    }

                }else{  //买家不允许进入后台
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }

            }else{  //用户不存在
                return new ReturnMessages(RequestState.ERROR, "获取用户信息有误！", null);
            }

        }else{  //未登录
            return new ReturnMessages(RequestState.ERROR, "您还未登录！", null);
        }

        mapParam.put("store",store);

        //设置创建时间条件查询
        long sTime = 0l;
        if(StringUtils.isNotEmpty(startTime)){
            try{
                sTime = Long.valueOf(startTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"起始时间参数格式有误!",null);
            }
        }
        mapParam.put("startTime",sTime);
        long eTime = 0l;
        if(StringUtils.isNotEmpty(endTime)){
            try{
                eTime = Long.valueOf(endTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"结束时间参数格式有误!",null);
            }
        }
        mapParam.put("endTime",eTime);

        //设置查询分页
        UtilPage page = new UtilPage(0,5,"createTime", Sort.Direction.DESC);
        if(StringUtils.isNotEmpty(pageNum)){
            try{
                page.setPageNum(Integer.valueOf(pageNum));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分页页数格式有误!",null);
            }
        }

        if(StringUtils.isNotEmpty(pageSize)){
            try{
                page.setPageSize(Integer.valueOf(pageSize));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分页大小格式有误!",null);
            }
        }

        if(StringUtils.isNotEmpty(pageSort)){
            page.setPageSort(pageSort);
        }

        if(StringUtils.isNotEmpty(sortDirection)){
            if(sortDirection.equals("asc")){
                page.setDirection(Sort.Direction.ASC);
            }else if(sortDirection.equals("desc")){
                page.setDirection(Sort.Direction.DESC);
            }else{
                return new ReturnMessages(RequestState.ERROR,"分页排序参数有误!",null);
            }

        }

        mapParam.put("page",page);

        Page<Store> storePage = storeService.findByCondition(mapParam);

        if(storePage != null && storePage.getContent() != null && storePage.getContent().size() > 0){
            rm.setMessages("查询成功!");
        }else{
            rm.setMessages("暂无数据!");
        }
        rm.setState(RequestState.SUCCESS);
        rm.setContent(storePage);
        return rm;
    }


    /**
     * 设置为默认店铺
     * @param storeId 店铺ID
     * @return
     */
    /*@RequestMapping(value = "/setTheDefault")
    public ReturnMessages setTheDefault(
            @RequestParam(value = "storeId",required = true,defaultValue = "")String storeId
    ){
        Store defaultStore = storeService.findStoreByTheDefaultTrue();
        if(defaultStore != null){
            List<Goods> goodsList = goodsService.findByStoreAndDelFalse(defaultStore);  //查询店铺下的商品
            if(goodsList != null && goodsList.size() > 0){  //原默认店铺下有商品
                return new ReturnMessages(RequestState.ERROR,"原默认店铺下有商品，请移除商品后再修改默认店铺",goodsList);
            }
            List<Brand> brandList = brandService.findByStoreAndDelFalse(defaultStore);  //查询店铺下的品牌
            if(brandList != null && brandList.size() > 0){  //原默认店铺下有品牌
                return new ReturnMessages(RequestState.ERROR,"原默认店铺下有品牌，请移除品牌后再修改默认店铺",goodsList);
            }

        }
        Store store = storeService.setStoreDefault(storeId);
        if(store != null){
            return new ReturnMessages(RequestState.SUCCESS,"设置成功。",store);
        }else{
            return new ReturnMessages(RequestState.ERROR,"设置失败。",null);
        }
    }*/

}
