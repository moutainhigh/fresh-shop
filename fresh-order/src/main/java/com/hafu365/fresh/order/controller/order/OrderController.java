package com.hafu365.fresh.order.controller.order;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.BillsInfoConstant;
import com.hafu365.fresh.core.entity.constant.OrderStateConstant;
import com.hafu365.fresh.core.entity.constant.PriceConstant;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.goods.SimpleGoods;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.order.OrderDaddress;
import com.hafu365.fresh.core.entity.order.OrderReport;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.*;
import com.hafu365.fresh.order.config.ExcleConfig;
import com.hafu365.fresh.order.config.QRCodeConfig;
import com.hafu365.fresh.service.bills.BillsOperationService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.CheckUserService;
import com.hafu365.fresh.service.order.OrderDaddressService;
import com.hafu365.fresh.service.order.OrderService;
import com.hafu365.fresh.service.role.RoleService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单实现层
 * Created by zhaihuilin on 2017/8/7  17:11.
 */
@Log4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private static final String REPORT_NAME = "reportName";
    private static final String FILE_FORMAT = "format";
    private static final String DATASOURCE = "datasource";
    @Autowired
    private MemberService memberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDaddressService orderDaddressService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private  ExcleConfig excleConfig;
    @Autowired
    private QRCodeConfig qrCodeConfig;
    @Autowired
    private BillsOperationService billsOperationService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CheckUserService checkUserService;


    /**
     * 根据订单编号进行查询
     * @param orderId 订单编号
     * @return
     */
    @RequestMapping(value = "/findOrdersByOrderId")
    public ReturnMessages  findOrdersByOrderId(
            @RequestParam(name = "orderId") String orderId
    ){
        ReturnMessages returnMessages=null;
        if (orderId !=null && orderId.length()>0){
             Orders orders= orderService.findOrdersByOrdersIdAndDelFalse(orderId);
             if (orders !=null){
                int  getOrderState=orders.getOrderState();
                Map<String,Object> map=new HashMap<String,Object>();
                map.put("getOrderState",getOrderState);
                map.put("orders",orders);
                return new ReturnMessages(RequestState.SUCCESS,"有数据！",map);
             }else {
                return new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
             }
        }else {
                return new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
    }

    /**
     * 根据订单编号进行删除    逻辑删除  【可以进行批量删除】
     * @param orderIds  订单编号  【可能为多个】
     * @return
     */
    @RequestMapping(value = "/deleteOrderByordersId")
    public ReturnMessages deleteOrderByordersId(
            @RequestParam(name = "orderId") String[] orderIds,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages=new ReturnMessages();
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
        if (orderIds !=null && orderIds.length>0){
            for (String orderId:orderIds){
               Orders orders=orderService.findOrdersByOrdersIdAndDelFalse(orderId);
               if (orders !=null){
                       boolean flag= orderService.deleteOrderByordersId(orderId);
                       if (flag ==true){
                           returnMessages=new ReturnMessages(RequestState.SUCCESS,"删除成功！",null);
                       }else {
                           returnMessages=new ReturnMessages(RequestState.ERROR,"删除失败！",null);
                       }
               }else{
                   returnMessages=new ReturnMessages(RequestState.ERROR,"删除失败！",null);
               }
            }
        }else {
            return  new ReturnMessages(RequestState.ERROR,"请至少选择一项进行删除！",null);
        }
        return returnMessages;
    }

    /**
     * 根据订单编号进行删除   物理删除  【可以进行批量删除】
     * @param orderIds   订单编号  【可能为多个】
     * @return
     */
    @RequestMapping(value = "/physicallyDeleteByordersId")
    public ReturnMessages physicallyDeleteByordersId(
            @RequestParam(name = "orderId") String[] orderIds,
            HttpServletRequest request
    ){
        ReturnMessages returnMessages=new ReturnMessages();
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
        if (orderIds !=null && orderIds.length>0){
            for (String orderId:orderIds){
                Orders orders=orderService.findOrdersByOrdersIdAndDelFalse(orderId);
                if (orders !=null){
                    boolean flag=orderService.physicallyDeleteByordersId(orderId);
                    if (flag ==true){
                        returnMessages= new ReturnMessages(RequestState.SUCCESS,"删除成功！",flag);
                    }else {
                        returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",flag);
                    }
                }
            }
        }else {
            return  new ReturnMessages(RequestState.ERROR,"请至少选择一项进行删除！",null);
        }
        return returnMessages;
    }


    /**
     * 获取所有预计配送订单
     * 根据 店铺和订单状态【代发货】进行查询  【预计配送订单】
     * @param storeId      店铺编号
     * @param storeName    店铺名称
     * @param  startTime    下单开始时间
     * @param  endTime    下单结束时间
     * @param  ordersId  订单编号   必选
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/findOrdersByStoreAndOrderStart")
    public ReturnMessages findOrdersByStoreAndOrderStart(
            @RequestParam(name = "storeId",required = false,defaultValue = "") String storeId,
            @RequestParam(name = "storeName",required = false,defaultValue = "") String storeName,
            @RequestParam(name = "startTime",required = false,defaultValue = "")String startTime,
            @RequestParam(name = "ordersId",required = true,defaultValue = "") String ordersId,
            @RequestParam(name = "endTime",required = false,defaultValue = "")String endTime,
            @RequestParam(name = "page" ,required = false,defaultValue = "0") String page,
            @RequestParam(name = "pageSize" ,required = false,defaultValue = "20") String pageSize,
            HttpServletRequest request
    ){
            ReturnMessages returnMessages=null;
            Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //排序
            Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize),sort);
            Orders orders=new Orders();
            Store store=null;
            Long startDate=null;
            Long endDate =null;
            String loginUsername=checkUserService.getuserName(request);
            if(!StringUtils.isNotEmpty(loginUsername)){   //用户未登录
                return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
            }
            Member loginmember=checkUserService.getFiltrationMember(request);
            if(loginmember ==null){
                return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
            }
            ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
            if (returnMessagess !=null){
                return  returnMessagess;
            }
            if (loginmember !=null){
                List<Role> roleList=roleService.findRoleByMember(loginmember);//获取该登录用户具有那些权限
                if (roleList !=null && roleList.size()>0){
                    for (Role role : roleList){
                        if (role.getRoleCode().contains("ROLE:ADMIN")){//超级管理员
                            store=null;
                            break;
                        }
                        if (role.getRoleCode().contains("ROLE:SELLER")){//商家
                            store=storeService.findByMember(loginmember);
                        }
                    }
                }
            }
            //店铺
            if (StringUtils.isNotEmpty(storeId) && storeId.length()>0){
                store=storeService.findByStoreId(storeId);
                if (store ==null){
                    return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
                }
            }
            if (storeName !=null && StringUtils.isNotEmpty(storeName) ){
                store=storeService.findByStoreName(storeName);
                if (store ==null){
                    return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
                }
            }
            if (ordersId!=null && ordersId.length()>0){
                orders.setOrdersId(ordersId);
            }
        //时间
        try {
            if (StringUtils.isNotEmpty(startTime)){
                startDate=Long.valueOf(startTime);
            }
            if (StringUtils.isNotEmpty(endTime)){
                endDate=Long.valueOf(endTime);
            }
            if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) &&  Long.valueOf(startTime) >Long.valueOf(endTime)){
                 return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
        }catch (Exception e){
            log.info("时间参数错误:"+e.getMessage());
            return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
        }
           Page<Orders> ordersPage= orderService.findOrdersByStore(orders,store,startDate,endDate,pageable);
          if (ordersPage !=null && ordersPage.getContent().size()>0){
              return  new ReturnMessages(RequestState.SUCCESS,"有数据",ordersPage);
          }else {
              return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
          }
    }
    /**
     * 获取用户自己的订单信息    【买家】
     * @param page
     * @param pageSize
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMeOrders")
    public  ReturnMessages getMeOrders(
            @RequestParam(name = "page" ,required = false,defaultValue = "0") String page,
            @RequestParam(name = "pageSize" ,required = false,defaultValue = "20") String pageSize ,
            HttpServletRequest request){
        ReturnMessages returnMessages=null;
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
        Sort sort = new Sort(Sort.Direction.DESC,"createTime");
        Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize),sort);
        Page<Orders>  ordersPage=orderService.getMeOrders(username,pageable);
        if (ordersPage !=null && ordersPage.getContent().size()>0){
            return  new ReturnMessages(RequestState.SUCCESS,"有数据！",ordersPage);
        }else {
            return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
    }


    /**
     * 过滤    memberList   goodsClassList  permissionList brandList    用于用户
     * @return
     */
    public ExclusionStrategy retrunes1(){
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

    /**
     * 过滤    memberList   goodsClassList member permissionList brandList
     * @return
     */
    public ExclusionStrategy retrunes(){
        ExclusionStrategy es = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fa) {
                return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList") ||fa.getName().equals("roleList") ||fa.getName().equals("permissionList") ||fa.getName().equals("brandList")||fa.getName().equals("goodsClass") ||fa.getName().equals("brand");
            }
            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };
        return  es;
    }


    /**
     * 获取所有的订单信息
     * 条件查询 分页
     * @param username   用户名
     * @param memberId  用户编号
     * @param orderDaddressId  配送地址编号
     * @param ordersId    订单编号
     * @param orderState  订单状态
     * @param storeId    店铺编号
     * @param storeName   店铺名称
     * @param sellerState 退货状态
     * @param startTime   订单生成开始时间
     * @param endTime      订单生结束时间
     * @param page   页码
     * @param pageSize   每页显示的行数
     * @return
     */
    @RequestMapping(value = "/findAllOrders")
    public ReturnMessages findAllOrders(
       @RequestParam(name = "username",required = false,defaultValue = "") String username,
       @RequestParam(name = "memberId",required = false,defaultValue = "") String memberId,
       @RequestParam(name = "orderDaddressId",required = false,defaultValue = "") String orderDaddressId,
       @RequestParam(name = "ordersId",required = false,defaultValue = "") String ordersId,
       @RequestParam(name = "orderState",required = false,defaultValue = "") String orderState,
       @RequestParam(name = "storeId",required = false,defaultValue = "") String storeId,
       @RequestParam(name = "storeName",required = false,defaultValue = "") String storeName,
       @RequestParam(name = "sellerState",required = false,defaultValue = "") String sellerState,
       @RequestParam(name = "startTime",required = false,defaultValue = "")String startTime,
       @RequestParam(name = "endTime",required = false,defaultValue = "")String endTime,
       @RequestParam(name = "page" ,required = false,defaultValue = "0") String page,
       @RequestParam(name = "pageSize" ,required = false,defaultValue = "20") String pageSize,
       HttpServletRequest request
    ){
        ReturnMessages returnMessages=null;
        Member member=null;
        Long startDate=null;
        Long endDate =null;
        Store store=null;
        OrderDaddress orderDaddress=null;
        Orders orders=new Orders();
        Sort sort = new Sort(Sort.Direction.DESC,"createTime"); //排序
        Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize),sort);
        String loginUsername=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(loginUsername)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member loginmember=checkUserService.getFiltrationMember(request);
        if(loginmember ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        if (loginmember !=null){
            List<Role> roleList=roleService.findRoleByMember(loginmember);//获取该登录用户具有那些权限
            if (roleList !=null && roleList.size()>0){
                  for (Role role : roleList){
                        if (role.getRoleCode().contains("ROLE:ADMIN")){//超级管理员
                             store=null;
                             break;
                        }
                        if (role.getRoleCode().contains("ROLE:SELLER")){//商家
                             store=storeService.findByMember(loginmember);
                        }
                  }
            }
        }
        //店铺
        if (StringUtils.isNotEmpty(storeId) && storeId.length()>0){
            store=storeService.findByStoreId(storeId);
            if (store ==null){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
        }
        if (storeName !=null && StringUtils.isNotEmpty(storeName)){
            store=storeService.findByStoreName(storeName);
            if (store ==null){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
        }
        //用户
        if (username !=null && username.length()>0){
            member= memberService.findMemberByUsername(username);
            if (member==null){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
            member=getFiltrationMember(member);
        }
        if (memberId !=null && memberId.length()>0){
            member= memberService.findMemberByMemberId(memberId);
            if (member==null){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
            member=getFiltrationMember(member);
        }
       //时间
        try {
            if (StringUtils.isNotEmpty(startTime)  ){
               startDate=Long.valueOf(startTime);
            }
            if (StringUtils.isNotEmpty(endTime)){
                endDate=Long.valueOf(endTime);
            }
            if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime) &&  Long.valueOf(startTime) >Long.valueOf(endTime)){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
        }catch (Exception e){
            log.info("时间参数错误:"+e.getMessage());
            return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
        }
        /**
         * 配送地址
         */
        if (orderDaddress !=null && orderDaddressId.length()>0 ){
            orderDaddress=orderDaddressService.findOrderDaddressByOrderDaddressId(Long.valueOf(orderDaddressId));
        }
        //订单退货审核状态
        if (sellerState!=null && sellerState.length()>0){
            orders.setSellerState(Integer.parseInt(sellerState));
        }
        if (ordersId!=null && ordersId.length()>0){
            orders.setOrdersId(ordersId);
        }
        //订单状态
        if (orderState!=null && orderState.length()>0 ){
            orders.setOrderState(Integer.parseInt(orderState));
        }
        Page<Orders> ordersPage =orderService.findOrders(username,store,orderDaddress,orders,startDate,endDate,pageable);
        if (ordersPage !=null && ordersPage.getContent().size()>0){
            return  new ReturnMessages(RequestState.SUCCESS,"有数据！",ordersPage);
        }else {
            return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
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
     * 点击签收时,修改订单的状态
     * @param ordersId  订单编号
     * @param inputvalue 前台传过来的值  【订单号后四位】
     * @return
     */
    @RequestMapping(value = "/finshOrders")
    public  ReturnMessages finshOrdersUpdateorderState(
            @RequestParam(name = "ordersId",required = false,defaultValue = "") String ordersId,
            @RequestParam(name = "inputvalue",required = true,defaultValue = "") String inputvalue
    ){
        ReturnMessages returnMessages=null;
        String subordersId=ordersId.substring(14,18);//截取订单号后四位
        if (inputvalue == null && inputvalue.equals("")){
            return  new ReturnMessages(RequestState.ERROR,"验证码不能为空！",null);
        }
        if (inputvalue.trim() ==null  && inputvalue.equals("")){
            return  new ReturnMessages(RequestState.ERROR,"输入的验证码有误,请重新输入！",null);
        }
        if (!StringUtils.isNotEmpty(inputvalue)){
            return  new ReturnMessages(RequestState.ERROR,"输入的验证码有误,请重新输入！",null);
        }
        if (ordersId ==null && ordersId.length()<0){
            return  new ReturnMessages(RequestState.ERROR,"传入的订单参数有误",null);
        }
        if (subordersId.equals(inputvalue)){
            Orders orders=orderService.findOrdersByOrdersIdAndDelFalse(ordersId);
            if (orders !=null){
                int  getOrderState=orders.getOrderState();
                if (getOrderState==OrderStateConstant.ORDER_STATE_FINISH){
                    return  new ReturnMessages(RequestState.ERROR,"该订单已签收!请不要重复操作！",null);
                }
                orders.getGoodsList().get(0).getGoods().getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
                List<GoodsVo> goodsVoList=orders.getGoodsList();
                if (goodsVoList !=null && goodsVoList.size()>0){
                    for (GoodsVo goodsVo:goodsVoList){
                        List<GoodsVo> goodsVoList1=new ArrayList<GoodsVo>();
                        String goodsId= goodsVo.getGoodsId();
                        goodsVo.setGoodsId(goodsId);
                        goodsVo.setTakeTime(new Date().getTime());//商品签收时间
                        goodsVoList1.add(goodsVo);
                        orders.setGoodsList(goodsVoList1);
                    }
                    orders.setOrderState(OrderStateConstant.ORDER_STATE_FINISH);//设置订单的状态
                    orders.setUpdateTime(new Date().getTime());//签收(编辑时间)
                    try {
                        orders=orderService.updateOrders(orders);
                        String message = "订单号：" + orders.getOrdersId()+" 时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        billsOperationService.addBillsInfo(orders.getUsername(),orders.getPrice(), BillsInfoConstant.ENTRY_ACCOUNT,orders,message);
                        return new  ReturnMessages(RequestState.SUCCESS,"签收成功！",orders);
                    }catch (Exception e){
                        log.info("签收失败的原因:"+e.getMessage());
                        return new  ReturnMessages(RequestState.ERROR,"签收失败！",null);
                    }
                }else {
                        return new  ReturnMessages(RequestState.ERROR,"商品存在异常,签收失败,请核对后再进行签收！",null);
                }
            }else {
                return new  ReturnMessages(RequestState.ERROR,"订单不存在,签收失败！",null);
            }
        }else {
            return new  ReturnMessages(RequestState.ERROR,"验证码校验失败,签收失败！",null);
        }
    }

    /**
     * 拒收签收   更改 拒收数量
     * @param ordersId   订单编号
     * @param goodsIds   商品编号
     * @param counts     拒收商品数量
     * @param rejectreason 拒绝收货的原因
     * @param inputvalue 前台传过来的值  【订单号后四位】
     * @return
     */
    @RequestMapping(value ="/returnOrders")
    public  ReturnMessages  returnOrders(
            @RequestParam(name = "ordersId",required = false,defaultValue = "") String ordersId,
            @RequestParam(name = "goodsId",required = false,defaultValue = "")  String[] goodsIds,
            @RequestParam(name = "count",required = false,defaultValue = "") String[] counts,
            @RequestParam(name = "rejectreason",required = false,defaultValue = "") String rejectreason,
            @RequestParam(name = "inputvalue",required = true,defaultValue = "") String inputvalue
    ){
        ReturnMessages returnMessages=null;
        String subordersId=ordersId.substring(14,18);//截取订单号后四位
        if (inputvalue == null && inputvalue.equals("")){
             return  new ReturnMessages(RequestState.ERROR,"验证码不能为空！",null);
        }
        if (inputvalue.trim() ==null  && inputvalue.equals("")){
            return  new ReturnMessages(RequestState.ERROR,"输入的验证码有误,请重新输入！",null);
        }
        if (!StringUtils.isNotEmpty(inputvalue)){
            return  new ReturnMessages(RequestState.ERROR,"输入的验证码有误,请重新输入！",null);
        }
        if (ordersId ==null && ordersId.length()<0){
            return  new ReturnMessages(RequestState.ERROR,"传入的订单参数有误",null);
        }
        if (goodsIds ==null && counts==null && counts.length<0 && goodsIds.length<0){
            return  new ReturnMessages(RequestState.ERROR,"传入的商品参数有误",null);
        }else{
            if(counts.length!=goodsIds.length){
                return  new ReturnMessages(RequestState.ERROR,"传入的商品数量不一致",null);
            }
        }
        if (subordersId.equals(inputvalue)){
            Orders orders=orderService.findOrdersByOrdersIdAndDelFalse(ordersId);
            if (orders !=null){
                int  getOrderState=orders.getOrderState();
                if (getOrderState==OrderStateConstant.ORDER_STATE_FINISH){
                    return  new ReturnMessages(RequestState.ERROR,"该订单已签收!请不要重复操作！",null);
                }
                List<GoodsVo> goodsVoList=orders.getGoodsList();
                if (goodsVoList !=null && goodsVoList.size()>0){
                    for (int i = 0; i < goodsIds.length; i++) {
                        goodsVoList = addGoodsVoList(goodsIds[i], Integer.valueOf(counts[i]), goodsVoList,rejectreason);
                    }
                    orders.setGoodsList(goodsVoList);
                    orders.setSellerState(OrderStateConstant.RETURN_STATE_CHECK_NO);//拒绝收货（退货）审核状态  未审核
                    orders.setOrderState(OrderStateConstant.ORDER_STATE_FINISH);//设置订单的状态
                    orders.setUpdateTime(new Date().getTime());//签收(编辑时间)
                    try {
                        orders=orderService.updateOrders(orders);
                        String message = "订单号：" + orders.getOrdersId()+" 时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                        billsOperationService.addBillsInfo(orders.getUsername(),orders.getPrice(), BillsInfoConstant.ENTRY_ACCOUNT,orders,message);
                        return new  ReturnMessages(RequestState.SUCCESS,"签收成功！",orders);
                    }catch (Exception e){
                        log.info("拒收签收失败的原因:"+e.getMessage());
                        return  new ReturnMessages(RequestState.ERROR,"订单异常,拒收签收失败,请核对后在进行签收！",null);
                    }
                }else {
                        return new  ReturnMessages(RequestState.ERROR,"商品存在异常,签收失败,请核对后再进行签收！",null);
                }
            }else{
                 return new  ReturnMessages(RequestState.ERROR,"订单不存在,签收失败！",null);
            }
        }else{
            return new  ReturnMessages(RequestState.ERROR,"验证码校验失败,请重新输入！",null);
        }
    }

    /**
     * 修改商品列表信息
     * @param goodsId   商品编号
     * @param count     拒收数量
     * @param goodsVoList   商品列表
     * @param rejectreason   拒收原因
     * @return
     */
    public List<GoodsVo> addGoodsVoList(String goodsId, int count, List<GoodsVo> goodsVoList,String rejectreason) {
        int pos = findGoodsVo(goodsVoList, goodsId);
        if (pos >= 0) {
                GoodsVo goodsVo = goodsVoList.get(pos);
                goodsVo.setRejectreason(rejectreason);
                goodsVo.setRejection(count);
                goodsVoList.set(pos, goodsVo);
            }
        return goodsVoList;
    }

    /**
     * 查询商品 在GoodsVoList中的位置
     * @param goodsVoList   商品集合
     * @param goodsId       商品编号
     * @return   数值
     */
    public int findGoodsVo(List<GoodsVo> goodsVoList, String goodsId) {
        if(goodsVoList == null || goodsVoList.size() <= 0){
            return -1;
        }
        int i = 0;
        for (GoodsVo goodsVo : goodsVoList) {
            if (goodsVo != null && goodsVo.getGoodsId().equals(goodsId)) {
                return i;
            }
            i++;
        }
        return -1;
    }
    /**
     * 商家点击 发货按钮时  更改订单状态
     * @param ordersId   订单编号
     * @return
     */
    @RequestMapping(value ="/shipments")
    public ReturnMessages  shipments(
            @RequestParam(name = "ordersId",required = false,defaultValue = "") String ordersId,
            HttpServletRequest request
    ){
         ReturnMessages returnMessages=new ReturnMessages();
        String loginstoreId="";//声明一个店铺编号 用来接收当前登录用户所属店铺的编号
        String loginUsername=checkUserService.getuserName(request);
        if(!StringUtils.isNotEmpty(loginUsername)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录,没有权限！请登录后进行操作", null);
        }
        Member loginmember=checkUserService.getFiltrationMember(request);
        if(loginmember ==null){
            return  new ReturnMessages(RequestState.ERROR,"用户不存在,没有权限！",null);
        }
        ReturnMessages returnMessagess=checkUserService.CheckMemberState(request);
        if (returnMessagess !=null){
            return  returnMessagess;
        }
        if (loginmember !=null){
            /***************获取当前登录用户的店铺*********************************************************************/
            Store  loginstore=storeService.findByMember(loginmember);//获取当前登录用户的店铺
            if (loginstore==null){
                 return  new ReturnMessages(RequestState.ERROR,"当前用户没有店铺,故没有权限操作！",null);
            }
                 loginstoreId=loginstore.getStoreId();//获取当前登录用户的店铺的店铺编号
        }
        /***************获取当前订单所属店铺******************************************************************************/
        if (ordersId !=null && ordersId.length()>0){
            Orders orders= orderService.findOrdersByOrdersIdAndDelFalse(ordersId);
            if (orders==null){
                return  new ReturnMessages(RequestState.ERROR,"该订单不存在",null);
            }
            if (orders.getOrderState()==OrderStateConstant.ORDER_STATE_NOT_RECEIVING){
                return  new ReturnMessages(RequestState.ERROR,"该订单已发货,请不要重复操作！",null);
            }
            Store store=orders.getStore();//获取当前订单所属店铺
            if (store !=null){
                String storeId=store.getStoreId();//获取当前订单所属店铺编号
                if (!storeId.equals(loginstoreId)){
                      return  new ReturnMessages(RequestState.ERROR,"该订单不属于当前登录用户的所属店铺旗下的,故没有权限操作！",null);
                }
                orders.setOrderState(OrderStateConstant.ORDER_STATE_NOT_RECEIVING); //待收货
                try {
//                    Member member = new Member(orders.getMember());
                    orders=orderService.updateOrders(orders);
                    returnMessages=new ReturnMessages(RequestState.SUCCESS,"发货成功！",orders);
                }catch (Exception e){
                    log.info("发货失败的原因:"+e.getMessage());
                    return  new ReturnMessages(RequestState.ERROR,"发货失败！",null);
                }
            }
        }
        return returnMessages;
    }

    /**
     * 商家对退货订单进行审核
     * @param ordersId      订单编号
     * @param sellerState    1:同意  2:不同意
     * @param sellerMessage   审核原因
     * @return
     */
    @RequestMapping(value = "/returnCheckOrder")
    public  ReturnMessages returnCheckOrder(
            @RequestParam(name = "ordersId",required = true,defaultValue = "") String ordersId,
            @RequestParam(name = "sellerState",required = true,defaultValue = "") String sellerState,
            @RequestParam(name = "sellerMessage",required = false,defaultValue = "")String sellerMessage
    ){
        ReturnMessages returnMessages=new ReturnMessages();
        if (ordersId !=null && ordersId.length()>0){
            Orders orders= orderService.findOrdersByOrdersIdAndDelFalse(ordersId);
            if (orders !=null){
                try {
                    //获取商品订单集合信息
                    List<GoodsVo> goodsVoList=orders.getGoodsList();
                    if (goodsVoList !=null && goodsVoList.size()>0){
                        for (GoodsVo goodsVo:goodsVoList){
                            goodsVoList.add(goodsVo);
                        }
                        orders.setGoodsList(goodsVoList);
                    }
                    orders.setSellerMessage(sellerMessage); //审核原因
                    orders.setSellerState(Integer.parseInt(sellerState));//商家对退货审核状态
                    orders=orderService.updateOrders(orders);
                    returnMessages=new ReturnMessages(RequestState.SUCCESS,"发货成功！",orders);
                }catch (Exception e){
                    returnMessages=new ReturnMessages(RequestState.ERROR,"发货失败！",null);
                    log.info("发货失败的原因:"+e.getMessage());
                }
            }
        }
        return  returnMessages;
    }
    /**
     * --配送单二维码生成   生成带logo 的二维码
     * @param  orderId  订单编号
     * @return
     */
    @RequestMapping(value = "/MatrixToImage")
    public ReturnMessages MatrixToImage(
       @RequestParam(name = "orderId") String orderId
    )throws Exception{
        ReturnMessages returnMessages=new ReturnMessages();
        String string="O20170908114850345";
        String textt = "http://192.168.0.17:8080/fresh-order/order/CheckQRCode?orderId="+orderId;
        log.info("------------:"+textt);
        String imgName="hafu.jpg";
        File file=TemplateFileUtil.getQrecodeLogo(imgName);//获取文件
        String  imgPath=file.getPath();//在获取文件所在路劲
        String destPath=qrCodeConfig.QRCODEIMG_PATH;//二维码生成地址路劲
        System.out.println("生成成功");
        QRCodeUtil.encode(textt,imgPath,destPath,true);
        Map<String,Object> returnMap = new HashedMap();
        String fileName=orderId+".jpg";
        String qrcodeUrl=qrCodeConfig.QRCODEBASE_URL+"//"+fileName;
        if (qrcodeUrl !=null){
            returnMap.put("downUrl",qrcodeUrl);
            returnMessages=new ReturnMessages(RequestState.SUCCESS,"生成二维码成功！",returnMap);
        }else{
            returnMessages=new ReturnMessages(RequestState.ERROR,"生成二维码失败！",returnMap);
        }
         return  returnMessages;
    }

    /**
     * 扫描二维码后要跳转的页面 然后显示订单信息
     * @return
     */
    @RequestMapping(value = "/CheckQRCode")
    public ReturnMessages CheckQRCode(
            HttpServletRequest request, HttpSession session,String orderId
    ){
           ReturnMessages returnMessages=new ReturnMessages();
           ModelAndView modelAndView=new ModelAndView("");//跳转地址页面
           log.info("获取到orderId:"+orderId);
           if ( orderId !=null && orderId.length()>0){
                Orders orders= orderService.findOrdersByOrdersIdAndDelFalse(orderId);
                if (orders !=null){
                    returnMessages=new ReturnMessages(RequestState.SUCCESS,"有数据！",orders);
                }else {
                    returnMessages=new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
                }
            }else {
               returnMessages=new ReturnMessages(RequestState.ERROR,"没有此订单编号查询！",null);
           }
         return returnMessages;
    }

    /**
     * 报表导出 【预览】
     * @param orderId
     * @return
     */
     @RequestMapping(value = "/orderReport")
     public  ReturnMessages orderReport(
             HttpServletResponse response,HttpServletRequest request,
             @RequestParam(name = "orderId") String orderId
     )throws Exception{
         ReturnMessages returnMessages=new ReturnMessages();
         if (orderId!=null && orderId.length()>0){
             Orders orders=orderService.findOrdersByOrdersIdAndDelFalse(orderId);
             if (orders !=null){
                 long CreateTime=orders.getCreateTime();
                 orderId=orders.getOrdersId();//订单编号
//                 Member member=orders.getMember();
                 String username=orders.getUsername();
                 String storeName="";
//                 if (member!=null){
//                     username=member.getUsername();//所属用户
//                 }
                 Store store=orders.getStore();
                 if (store !=null){
                     storeName=store.getStoreName();//店铺名称
                 }
                 double  totalPrice=orders.getPrice();
                 int shopNumber=0;//商品总数量
                 List<GoodsVo> goodsVoList=orders.getGoodsList();
                 List<OrderReport> orderReportList =new ArrayList<OrderReport>();
                 if (goodsVoList !=null && goodsVoList.size()>0){
                     for (GoodsVo goodsVo :goodsVoList){
                         OrderReport orderReport=new OrderReport();
                         SimpleGoods goods=goodsVo.getGoods();
                         String  GoodsTitle=goods.getGoodsTitle();//商品名称
                         int num=goodsVo.getNumber();//购买数量
                         shopNumber=shopNumber+num;
                         double goodsPrice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());//商品单价
                         double goodsTotalPrice=num *goodsPrice;//商品总价
                         orderReport.setGoodsName(GoodsTitle);
                         orderReport.setShopNum(num);
                         orderReport.setGoodsPrice(goodsPrice);
                         orderReport.setGoodsTotalPrice(goodsTotalPrice);
                         orderReportList.add(orderReport);
                     }
                 }
                 String strshopNumber=String.valueOf(shopNumber);
                 String strtotalPrice=String.valueOf(totalPrice);
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                 Date date=new Date(CreateTime);
                 Map<String,String> map=new HashMap<String, String>();
                 map.put("title","配送单");
                 map.put("total",strshopNumber);
                 map.put("orderId",orderId);
                 map.put("date",sdf.format(date));
                 map.put("username",username);
                 map.put("totalPrice","￥"+strtotalPrice);
                 try {
                     SimpleDateFormat folder = new SimpleDateFormat("yyyyMMdd");
                     Date date1 = new Date();
                     String dirPath = folder.format(date1);
                     createDirectory(excleConfig.EXCLEFILE_PATH + "\\" + dirPath);
                     String url=excleConfig.EXCLEFILE_PATH +"\\"+dirPath+"\\";    //输出路劲
                     FileOutputStream outputStream=new FileOutputStream(url+storeName+"-"+username+"-"+orderId+".xls");
                     String template="order4.xls";//输入模板
                     ExcelUtil.getInstance().exportObj2ExcelByTemplate(map,template,outputStream,orderReportList,OrderReport.class,true);
                     Map<String,Object> returnMap = new HashedMap();
                     returnMap.put("orderInfo",map);
                     returnMap.put("downUrl",excleConfig.BASE_URL+"//"+dirPath+"//"+storeName+"-"+username+"-"+orderId+".xls");
                     returnMap.put("excleList",orderReportList);
                     returnMessages=new ReturnMessages(RequestState.SUCCESS,"导出成功！",returnMap);
                     log.info("-------------导出成功--------------");
                 }catch (Exception e){
                     e.printStackTrace();
                     returnMessages=new ReturnMessages(RequestState.ERROR,"导出失败！",null);
                 }
             }else{
                     returnMessages=new ReturnMessages(RequestState.ERROR,"订单不存在,导出失败！",null);
             }

         }else{
             returnMessages=new ReturnMessages(RequestState.ERROR,"订单编号不能为空,导出失败！",null);
         }
         return  returnMessages;
     }

    /**
     * 报表导出 【下载】
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/downorderReport")
    public  ReturnMessages downorderReport(
            HttpServletResponse response,HttpServletRequest request,
            @RequestParam(name = "orderId") String orderId
    )throws Exception{
        ReturnMessages returnMessages=new ReturnMessages();
        if (orderId !=null && orderId.length()>0){
            Orders orders=orderService.findOrdersByOrdersIdAndDelFalse(orderId);
            if (orders !=null){
                orderId=orders.getOrdersId();//订单编号
                String username=orders.getUsername();
                String storeName="";
//                Member member=orders.getMember();
//                if (member !=null){
//                    username=member.getUsername();//所属用户
//                }
                Store store=orders.getStore();
                if (store !=null){
                    storeName=store.getStoreName();//店铺名称
                }
                try {
                    SimpleDateFormat folder = new SimpleDateFormat("yyyyMMdd");
                    Date date1 = new Date();
                    String dirPath = folder.format(date1);
                    Map<String,Object> returnMap = new HashedMap();
                    returnMap.put("downUrl",excleConfig.BASE_URL+"//"+dirPath+"//"+storeName+"-"+username+"-"+orderId+".xls");
                    returnMessages=new ReturnMessages(RequestState.SUCCESS,"导出成功！",returnMap);
                    log.info("-------------导出成功--------------");
                }catch (Exception e){
                    e.printStackTrace();
                    returnMessages=new ReturnMessages(RequestState.ERROR,"导出失败！",null);
                }
            }else{
                    returnMessages=new ReturnMessages(RequestState.ERROR,"订单不存在为空,导出失败！",null);
            }
        }else {
            returnMessages=new ReturnMessages(RequestState.ERROR,"订单编号不能为空,导出失败！",null);
        }
        return  returnMessages;
    }

    /**
     * 判断路径是否创建   目录
     * @param path
     * @return
     */
    public boolean createDirectory(String path) {
        boolean falg = true;
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }else{
            if(!file.isDirectory()){
                file.mkdirs();
            }
        }
        return falg;
    }
}
