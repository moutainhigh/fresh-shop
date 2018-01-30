package com.hafu365.fresh.order.controller.orderDaddress;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.OrderDaddress;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.CheckUserService;
import com.hafu365.fresh.service.order.OrderDaddressService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zhaihuilin on 2017/8/7  17:49.
 */
@Log4j
@RestController
@RequestMapping("/orderDaddress")
public class OrderDaddressController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderDaddressService orderDaddressService;

    @Autowired
    private CheckUserService checkUserService;

    /**
     * 新增配送信息      【使用于全部】
     * @param memberId   用户编号
     * @param address    配送地址
     * @param name       联系人姓名
     * @param phone   联系电话
     * @return
     */
    @RequestMapping(value = "/saveOrderDaddress")
    public ReturnMessages saveOrderDaddress(
           @RequestParam(name = "memberId") String  memberId,
           @RequestParam(name = "address") String  address,
           @RequestParam(name = "name") String  name,
           @RequestParam(name = "phone") String  phone
    ){
        OrderDaddress orderDaddress=new OrderDaddress();
        ReturnMessages returnMessages = null;
        if (memberId !=null && memberId.length()>0){
               Member member= memberService.findSimpleMemberByMemberId(memberId);
               if(member !=null){
                   member=getFiltrationMember(member);
                   orderDaddress.setUsername(member.getUsername());
               }else{
                   return new ReturnMessages(RequestState.ERROR,"用户不存在,新增失败！",null);
               }
        }else{
            return new ReturnMessages(RequestState.ERROR,"用户编号不存在,新增失败！",null);
        }
        if (StringUtils.isNotEmpty(address)){
            orderDaddress.setAddress(address);
        }else{
            return new ReturnMessages(RequestState.ERROR,"地址不能为空,新增失败！",null);
        }
        if (StringUtils.isNotEmpty(name)){
            orderDaddress.setName(name);
        }else{
            return new ReturnMessages(RequestState.ERROR,"用户名不能为空,新增失败！",null);
        }
        if (StringUtils.isNotEmpty(phone)){
            orderDaddress.setPhone(phone);
        }else{
            return new ReturnMessages(RequestState.ERROR,"联系方式不能为空,新增失败！",null);
        }
        try {
            orderDaddress= orderDaddressService.saveOrderDaddress(orderDaddress);
            if (orderDaddress !=null){
                return new ReturnMessages(RequestState.SUCCESS,"新增成功！",orderDaddress);
            }else{
                return new ReturnMessages(RequestState.ERROR,"新增失败！",null);
            }
        }catch (Exception e){
            return new ReturnMessages(RequestState.ERROR,"新增失败！",null);
        }
    }

    /**
     * 新增 自己的订单地址信息  【当前用户】
     * @param address   地址
     * @param name   姓名
     * @param phone   电话
     * @param request
     * @return
     */
    @RequestMapping(value ="/saveMeOrderDaddress")
    public  ReturnMessages saveMeOrderDaddress(
            @RequestParam(name = "address") String  address,
            @RequestParam(name = "name") String  name,
            @RequestParam(name = "phone") String  phone,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages=null;
        OrderDaddress orderDaddress=new OrderDaddress();
        String username=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(username)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member member=checkUserService.getFiltrationMember(request);
        if(member ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        orderDaddress.setUsername(username);
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        if (StringUtils.isNotEmpty(address)){
            orderDaddress.setAddress(address);
        }else{
            return  new ReturnMessages(RequestState.ERROR,"地址不能为空！",null);
        }
        if (StringUtils.isNotEmpty(name)){
            orderDaddress.setName(name);
        }else{
            return  new ReturnMessages(RequestState.ERROR,"用户名不能为空！",null);
        }
        if (StringUtils.isNotEmpty(phone)){
            orderDaddress.setPhone(phone);
        }else{
            return  new ReturnMessages(RequestState.ERROR,"联系方式不能为空！",null);
        }
        try {
            orderDaddress= orderDaddressService.saveOrderDaddress(orderDaddress);
            if (orderDaddress !=null){
                return  new ReturnMessages(RequestState.SUCCESS,"新增成功！",orderDaddress);
            }else{
                return  new ReturnMessages(RequestState.ERROR,"新增失败！",null);
            }
        }catch (Exception e){
                return  new ReturnMessages(RequestState.ERROR,"新增失败！",null);
        }
    }

    /**
     * 编辑配送地址信息  【编辑当前用户的配送地址信息】
     * @param address    地址
     * @param name     联系人姓名
     * @param phone   联系人电话
     * @param orderDaddressId   配送地址编号
     * @return
     */
    @RequestMapping(value = "/updateMeorderDaddress")
    public ReturnMessages updateMeorderDaddress(
            @RequestParam(name = "address",required = false,defaultValue = "") String  address,
            @RequestParam(name = "name",required = false,defaultValue = "") String  name,
            @RequestParam(name = "phone",required = false,defaultValue = "") String  phone,
            @RequestParam(name = "orderDaddressId") String  orderDaddressId,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = null;
        if (orderDaddressId !=null && orderDaddressId.length()>0){
            OrderDaddress orderDaddress=orderDaddressService.findOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
            if (orderDaddress ==null){
                return  new ReturnMessages(RequestState.ERROR,"配送地址不存在！",null);
            }
            if (StringUtils.isNotEmpty(address)){
                orderDaddress.setAddress(address);
            }
            if (StringUtils.isNotEmpty(name)){
                orderDaddress.setName(name);
            }
            if (StringUtils.isNotEmpty(phone)){
                orderDaddress.setPhone(phone);
            }
            String username=checkUserService.getuserName(request);
            if(!StringUtils.isNotEmpty(username)){   //用户未登录
                return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
            }
            Member member=checkUserService.getFiltrationMember(request);
            if(member ==null){
                return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
            }
            ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
            if (returnMessagess !=null){
                return  returnMessagess;
            }
            try {
                orderDaddress= orderDaddressService.updateOrderDaddress(orderDaddress);
                if (orderDaddress !=null){
                    return  new ReturnMessages(RequestState.SUCCESS,"新增成功！",orderDaddress);
                }else{
                    return  new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
                }
            }catch (Exception e){
                return  new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
            }
        }else{
            return  new ReturnMessages(RequestState.ERROR,"配送地址不能为空,编辑失败！",null);
        }
    }

    /**
     * 编辑配送地址信息
     * @param memberId   用户名
     * @param address    地址
     * @param name     联系人姓名
     * @param phone   联系人电话
     * @param orderDaddressId   配送地址编号
     * @return
     */
    @RequestMapping(value = "/updateorderDaddress")
    public ReturnMessages updateorderDaddress(
            @RequestParam(name = "memberId",required = false,defaultValue = "") String  memberId,
            @RequestParam(name = "address",required = false,defaultValue = "") String  address,
            @RequestParam(name = "name",required = false,defaultValue = "") String  name,
            @RequestParam(name = "phone",required = false,defaultValue = "") String  phone,
            @RequestParam(name = "orderDaddressId") String  orderDaddressId
    ){
        ReturnMessages returnMessages =null;
        if (orderDaddressId !=null && orderDaddressId.length()>0){
                OrderDaddress orderDaddress=orderDaddressService.findOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
                if (orderDaddress ==null){
                    return  new ReturnMessages(RequestState.ERROR,"配送地址不存在",null);
                }
                if (StringUtils.isNotEmpty(address)){
                    orderDaddress.setAddress(address);
                }
                if (StringUtils.isNotEmpty(name)){
                    orderDaddress.setName(name);
                }
                if (StringUtils.isNotEmpty(phone)){
                    orderDaddress.setPhone(phone);
                }
              if (StringUtils.isNotEmpty(memberId)){
                   Member member=  memberService.findSimpleMemberByMemberId(memberId);
                  /***********对用户信息进行处理,过滤掉不需要的数据*****************/
                  if (member!=null){
                      member=getFiltrationMember(member);
                  }
                   orderDaddress.setUsername(member.getUsername());
              }
              try {
                 orderDaddress= orderDaddressService.updateOrderDaddress(orderDaddress);
                 if (orderDaddress !=null){
                     return  new ReturnMessages(RequestState.SUCCESS,"编辑成功！",orderDaddress);
                 }else{
                     return  new ReturnMessages(RequestState.ERROR,"配送地址不存在！",null);
                 }
              }catch (Exception e){
                     return  new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
              }
        }else{
             return  new ReturnMessages(RequestState.ERROR,"配送地址编号不能为空！",null);
        }
    }

    /**
     * 用户数据处理
     * @param member
     * @return
     */
    public  Member getFiltrationMember(Member member){
        Gson gson = new GsonBuilder().setExclusionStrategies(retrunes1()).create();
        String memberStr=gson.toJson(member);
        Type type = new TypeToken<Member>() {}.getType();
        member = gson.fromJson(memberStr,type);
        return  member;
    }
    /**
     * 配送地址默认
     * @param orderDaddressId   配送地址编号
     * @return
     */
    @RequestMapping(value = "/updateorderDaddressDef")
    public ReturnMessages updateorderDaddressDef(
            @RequestParam(name = "orderDaddressId",required = true,defaultValue = "") String  orderDaddressId,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = new ReturnMessages();
        String username=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(username)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member member=checkUserService.getFiltrationMember(request);
        if(member ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        List<OrderDaddress> orderDaddressList=orderDaddressService.findOrderDaddressByMember(username);
        if (orderDaddressList !=null && orderDaddressList.size()>0){
                 for (OrderDaddress orderDaddress:orderDaddressList){
                      orderDaddress.setIsdefault(Boolean.FALSE);
                      orderDaddress.setUsername(username);
                      orderDaddressService.updateOrderDaddress(orderDaddress);
                 }
         }else {
            return  new ReturnMessages(RequestState.ERROR,"没有配送地址,设置默认失败！",null);
        }
         if ( orderDaddressId !=null && orderDaddressId.length()>0){
                 OrderDaddress orderDaddress=orderDaddressService.findOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
                 orderDaddress.setUsername(username);
                 orderDaddress.setIsdefault(Boolean.TRUE);
                 try {
                     orderDaddress= orderDaddressService.updateOrderDaddress(orderDaddress);
                     if (orderDaddress !=null){
                         returnMessages=new ReturnMessages(RequestState.SUCCESS,"设置默认地址成功！",orderDaddress);
                     }else{
                         returnMessages=new ReturnMessages(RequestState.ERROR,"设置默认地址失败！",null);
                     }
                 }catch (Exception e){
                         returnMessages=new ReturnMessages(RequestState.ERROR,"设置默认地址失败！",null);
                 }
         }else{
             returnMessages=new ReturnMessages(RequestState.ERROR,"配送地址为空,设置默认地址失败！",null);
         }
        return  returnMessages;
    }

    /**
     * 根据 配送地址编号进行删除
     * @param orderDaddressIds  配送地址编号  【可以为多个】
     * @return
     */
    @RequestMapping(value = "/deleteOrderDaddressByorderDaddressId")
    public  ReturnMessages  deleteOrderDaddressByorderDaddressId(
            @RequestParam(name = "orderDaddressId") String[]  orderDaddressIds,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = new ReturnMessages();
        String username=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(username)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member member=checkUserService.getFiltrationMember(request);
        if(member ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        if (orderDaddressIds !=null && orderDaddressIds.length>0){
            for (String  orderDaddressId:orderDaddressIds){
                OrderDaddress orderDaddress=  orderDaddressService.findOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
                if (orderDaddress !=null){
                  boolean flag=  orderDaddressService.deleteOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
                  if (flag ==true){
                      returnMessages= new ReturnMessages(RequestState.SUCCESS,"删除成功！",flag);
                  }else {
                      returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",flag);
                  }
                }else{
                      returnMessages= new ReturnMessages(RequestState.ERROR,"配送地址不存在,删除失败！",null);
                }
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"请至少选择一项进行删除！",null);
        }
        return returnMessages;
    }

    /**
     * 根据地址编号进行查询
     * @param orderDaddressId  地址编号
     * @return
     */
    @RequestMapping(value = "/findOrderDaddressByOrderDaddressId")
    public ReturnMessages  findOrderDaddressByOrderDaddressId(
            @RequestParam(name = "orderDaddressId") String  orderDaddressId,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages = new ReturnMessages();
        String username=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(username)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member member=checkUserService.getFiltrationMember(request);
        if(member ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        if (orderDaddressId !=null && orderDaddressId.length()>0){
                OrderDaddress orderDaddress=  orderDaddressService.findOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
                if (orderDaddress !=null){
                    returnMessages= new ReturnMessages(RequestState.SUCCESS,"有数据！",orderDaddress);
                }else {
                    returnMessages= new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
                }
         }else{
            returnMessages= new ReturnMessages(RequestState.ERROR,"配送地址不能为空！",null);
        }
        return returnMessages;
    }

    /**
     * 条件查询
     * @param memberId   用户编号
     * @param username   用户名
     * @param name    联系人姓名
     * @param phone   联系人电话
     * @param address  配送地址
     * @param orderDaddressId  配送地址编号
     * @param page   每页显示的行数
     * @param pageSize  每页显示的行数
     * @return
     */
    @RequestMapping(value = "/findOrderAddress")
    public  ReturnMessages findOrderAddress(
            @RequestParam(name = "memberId" ,required = false,defaultValue = "") String memberId,
            @RequestParam(name = "username" ,required = false,defaultValue = "") String username,
            @RequestParam(name = "name" ,required = false,defaultValue = "") String name,
            @RequestParam(name = "phone" ,required = false,defaultValue = "") String phone,
            @RequestParam(name = "address" ,required = false,defaultValue = "") String address,
            @RequestParam(name = "orderDaddressId",required = false,defaultValue = "") String orderDaddressId,
            @RequestParam(name = "page" ,required = false,defaultValue = "0") String page,
            @RequestParam(name = "pageSize" ,required = false,defaultValue = "20") String pageSize
    ){
        OrderDaddress orderDaddress=new OrderDaddress();
        ReturnMessages returnMessages = new ReturnMessages();
         Member member=null;
        if (StringUtils.isNotEmpty(orderDaddressId)){
            orderDaddress.setOrderDaddressId(Long.valueOf(orderDaddressId));
        }
        if (StringUtils.isNotEmpty(memberId)){
             member=memberService.findSimpleMemberByMemberId(memberId);
             if (member==null){
                 return new ReturnMessages(RequestState.ERROR,"用户不存在！",null);
             }
        }
        if (StringUtils.isNotEmpty(username)){
            member=memberService.findSimpleMemberByUsername(username);
            if (member==null){
                return new ReturnMessages(RequestState.ERROR,"用户不存在！",null);
            }
        }
        Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize));
        if (StringUtils.isNotEmpty(name)){
            orderDaddress.setName(name);
        }
        if (StringUtils.isNotEmpty(phone)){
            orderDaddress.setPhone(phone);
        }
        if (StringUtils.isNotEmpty(address)){
            orderDaddress.setAddress(address);
        }
        Page<OrderDaddress> orderDaddressPage= orderDaddressService.findAllOrderDaddress(username,orderDaddress,pageable);
        if (orderDaddressPage !=null && orderDaddressPage.getContent().size()>0){
           returnMessages=new ReturnMessages(RequestState.SUCCESS,"有数据！",orderDaddressPage);
        }else {
           returnMessages=new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
        return returnMessages;
    }


    /**
     * 获取用户自己的订单地址信息
     * 根据用户查询订单地址列表
     * @return
     */
    @RequestMapping(value = "/getMeOrderDaddress")
    public  ReturnMessages findOrderDaddressByMember(
          HttpServletRequest request,
          @RequestParam(name = "page" ,required = false,defaultValue = "0") String page,
          @RequestParam(name = "pageSize" ,required = false,defaultValue = "20") String pageSize
    ){
        ReturnMessages returnMessages=null;
        Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize));
        String username=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(username)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member member=checkUserService.getFiltrationMember(request);
        if(member ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        Page<OrderDaddress>  orderDaddressList=orderDaddressService.findAllOrderDaddressByMember(username,pageable);
        if (orderDaddressList !=null && orderDaddressList.getContent().size()>0){
             returnMessages=new ReturnMessages(RequestState.SUCCESS,"有数据！",orderDaddressList);
        }else {
             returnMessages=new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
        return  returnMessages;
    }

    /**
     * 过滤    memberList   goodsClassList  permissionList brandList    用于用户
     * @return
     */
    public ExclusionStrategy  retrunes1(){
        ExclusionStrategy es = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fa) {
                return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList") ||fa.getName().equals("permissionList") ||fa.getName().equals("brandList")||fa.getName().equals("goodsClass") ||fa.getName().equals("brand");
            }
            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };
        return  es;
    }




}
