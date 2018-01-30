package com.hafu365.fresh.order.controller.markOrder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.entity.order.SimpleDayOrder;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.CheckUserService;
import com.hafu365.fresh.service.order.DayOrderService;
import com.hafu365.fresh.service.order.MakeOrderService;
import lombok.extern.log4j.Log4j;
import org.olap4j.impl.ArrayMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 预约订单实现层
 * Created by zhaihuilin on 2017/8/3  17:32.
 */
@RestController
@Log4j
@RequestMapping("/makeOrder")
public class MakeOrderController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MakeOrderService makeOrderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private DayOrderService dayOrderService;
    @Autowired
    private MemberInfoService memberInfoService;
    @Autowired
    private CheckUserService checkUserService;

    /**
     * 编辑预约订单
     * @param makeOrderId   预约订单编号
     * @param  dayOrderListStr  天订单信息
     * @return   信息反馈
     */
    @RequestMapping(value = "/update")
    public ReturnMessages update(
            @RequestParam(name = "makeOrderId",required = true,defaultValue = "") String makeOrderId,
            @RequestParam(name = "dayOrderList",required = true,defaultValue = "") String dayOrderListStr,
            HttpServletRequest request
    ){
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
            if (makeOrderId != null && makeOrderId.length() > 0 ) {//0
                MakeOrder makeOrder = makeOrderService.findMakeOrderByMakeOrderId(makeOrderId);
                Gson gson = new Gson();
                if (makeOrder != null) {
                    if (dayOrderListStr != null) {
                        List<DayOrder> dayOrderArrayList = new ArrayList<DayOrder>();
                        Type type = new TypeToken<List<DayOrder>>() {
                        }.getType();
                        dayOrderArrayList = gson.fromJson(dayOrderListStr, type);
                        Map<String,List<GoodsVo>> map = new ArrayMap<String,List<GoodsVo>>();
                        for (int u = 0 ; u < dayOrderArrayList.size();u++){
                            List<Integer> indexs = new ArrayList<Integer>();
                            List<GoodsVo> goodsVos = dayOrderArrayList.get(u).getGoodsVoList();
                            for (int i = 0 ; i < goodsVos.size() ; i ++){
                                if(goodsVos.get(i).getNumber() <= 0){
                                    indexs.add(i);
                                }
                            }
                            Collections.reverse(indexs);
                            for (int index : indexs){
                                goodsVos.remove(index);
                            }
                            if(goodsVos.size() > 0){
                                map.put(dayOrderArrayList.get(u).getDayOrderId(),goodsVos);
                            }else{
                                List<GoodsVo>goodsVoList = new ArrayList<GoodsVo>();
                                map.put(dayOrderArrayList.get(u).getDayOrderId(),goodsVoList );
                        }
                    }
                    List<DayOrder> dayOrders = dayOrderService.findDayOrderByMakeOrder(makeOrder);
                    List<Integer> indexs = new ArrayList<Integer>();
                    List<String>delDayOrderId = new ArrayList<String>();
                    for (int i = 0 ; i < dayOrders.size();i++){
                        String key = dayOrders.get(i).getDayOrderId();
                        if(map.get(key)!=null){
                            if(map.get(key).size()>0){
                                dayOrders.get(i).setGoodsVoList(map.get(key));
                            }else{
                                indexs.add(i);
                            }
                        }
                    }
                    Collections.reverse(indexs);
                        for (int index:indexs){
                            delDayOrderId.add(dayOrders.get(index).getDayOrderId());
                            dayOrders.remove(index);
                        }
                        try {
                            makeOrder = makeOrderService.updateMakeOrder(makeOrder);
                            for (String dayOrderId:delDayOrderId){
                                dayOrderService.deleteDayOrdersByDayOrderId(dayOrderId);
                            }
                            returnMessages= new ReturnMessages(RequestState.SUCCESS,"编辑成功!",makeOrder);
                        } catch (Exception e) {
                            returnMessages= new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
                        }
                    }
                }
            } else {
                returnMessages= new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
            }
        return  returnMessages;
    }
    /**
     * 根据预约订单编号查询预约订单
     * @param makeOrderId  预约订单编号
     * @return
     */
    @RequestMapping(value = "/findMakeOrderBymakeOrderId")
    public  ReturnMessages findMakeOrderBymakeOrderId(
            @RequestParam(name = "makeOrderId") String makeOrderId,
            HttpServletRequest request
    ){
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
        if (makeOrderId != null && makeOrderId.length()>0) {
                MakeOrder makeOrder = makeOrderService.findMakeOrderByMakeOrderId(makeOrderId);
                if (makeOrder != null) {
                    return new ReturnMessages(RequestState.SUCCESS,"有数据！",makeOrder);
                } else {
                    return new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
                }
        }else{
                    return new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
    }

    /**
     * 根据预约订单删除
     * @param makeOrderIds  makeOrderId  （可能会有多个）
     * @return
     */
    @RequestMapping(value = "/deleteMakeOrderByMakeOrderId")
    public  ReturnMessages deleteMakeOrderByMakeOrderId(
            @RequestParam(name = "makeOrderId") String[] makeOrderIds,
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
        if (makeOrderIds !=null && makeOrderIds.length > 0) {
            for (String makeOrderId : makeOrderIds) {
                if (makeOrderId != null) {
                    MakeOrder makeOrder = makeOrderService.findMakeOrderByMakeOrderId(makeOrderId);
                    if (makeOrder != null) {
                        boolean flag = makeOrderService.deleteMakeOrderByMakeOrderId(makeOrderId);
                        if (flag == true) {
                            returnMessages= new ReturnMessages(RequestState.SUCCESS,"删除成功！",flag);
                        } else {
                            returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",flag);
                        }
                    } else {
                            returnMessages= new ReturnMessages(RequestState.ERROR,"没有此编号,删除失败！",null);
                    }
                }
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"删除失败！",null);
        }
        return  returnMessages;
    }

    /**
     * 条件查询
     * @param makeOrderId  预约订单编号         [可空]
     * @param memberId     用户编号             [可空]
     * @param username     用户名              [可空]
     * @param CstartTime   预约创建开始时间     [可空]
     * @param CendTime     预约创建结束时间     [可空]
     * @param UstartTime   预约编辑开始时间     [可空]
     * @param UendTime     预约编辑结束时间     [可空]
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/findAllMakeOrders")
    public  ReturnMessages findAllMakeOrders(
         @RequestParam(name = "makeOrderId",required = false,defaultValue = "") String makeOrderId,
         @RequestParam(name = "memberId",required = false,defaultValue = "") String memberId,
         @RequestParam(name = "username",required = false,defaultValue = "") String username,
         @RequestParam(name = "CstartTime",required = false,defaultValue = "")String CstartTime,
         @RequestParam(name = "CendTime",required = false,defaultValue = "")String CendTime,
         @RequestParam(name = "UstartTime",required = false,defaultValue = "")String UstartTime,
         @RequestParam(name = "UendTime",required = false,defaultValue = "")String UendTime,
         @RequestParam(name = "page",required = false,defaultValue = "0")String page,
         @RequestParam(name = "pageSize",required = false,defaultValue = "20")String pageSize
         ){
        ReturnMessages returnMessages=new ReturnMessages();
        Member member=null;
        MakeOrder makeOrder= new MakeOrder();
        Long CstartDate=null;
        Long CendDate=null;
        Long UstartDate=null;
        Long UendDate=null;
        Pageable pageable=new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize));
        //预约订单编号
         if (makeOrderId !=null && makeOrderId.length()>0){
             makeOrder.setMakeOrderId(makeOrderId);
         }
        //用户
        if (memberId !=null && memberId.length()>0){
            member=memberService.findMemberByMemberId(memberId);
            if (member==null){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
        }
        if (username !=null && username.length()>0){
            member=memberService.findMemberByUsername(username);
            if (member==null){
                return  new ReturnMessages(RequestState.ERROR,"暂无数据",null);
            }
        }
        try {
            //创建时间
            if (StringUtils.isNotEmpty(CstartTime)) {
                CstartDate=Long.valueOf(CstartTime);
            }
            if (StringUtils.isNotEmpty(CendTime)){
                CendDate=Long.valueOf(CendTime);
            }
            //编辑时间
            if (StringUtils.isNotEmpty(UstartTime)) {
                UstartDate=Long.valueOf(UstartTime);
            }
            if (StringUtils.isNotEmpty(UendTime)){
                UendDate=Long.valueOf(UendTime);
            }

        }catch (Exception e){
            log.info("时间参数有误:"+e.getMessage());
        }
        Page<MakeOrder> newmakeOrders= makeOrderService.findAllMakeOrder(makeOrder,username,CstartDate,CendDate,UstartDate,UendDate,pageable);
        if (newmakeOrders !=null && newmakeOrders.getContent().size()>0){
              for (MakeOrder makeOrder1 : newmakeOrders){
                  List<DayOrder> dayOrderList=dayOrderService.findDayOrderByMakeOrder(makeOrder);
                  if (dayOrderList.size()>0){
                      for (DayOrder dayOrder:dayOrderList){
                          List<GoodsVo> goodsVoList=dayOrder.getGoodsVoList();
                          if (goodsVoList !=null && goodsVoList.size()>0){
                              List<Integer> nList = new ArrayList<Integer>();
                              for(int i = 0 ; i < goodsVoList.size() ; i++){
                                  Goods goods = goodsService.findByGoodsId(goodsVoList.get(i).getGoodsId());
                                  if (goods !=null){
                                      if(goods.isDel() || !goods.getState().equals(StateConstant.GOODS_STATE_CHECK_ON.toString())){
                                          nList.add(i);
                                      }
                                  }else{
                                      goodsVoList.remove(i);//删除商品
                                  }
                              }
                              Collections.reverse(nList);//对集合进行重新排序
                              for (int i : nList){
                                  goodsVoList.remove(i);
                              }
                          }
                          dayOrder.setGoodsVoStr(null);
                          dayOrder.setGoodsVoList(goodsVoList);
                          dayOrderService.update(dayOrder);
                      }
                  }
              }
        }
        Page<MakeOrder> makeOrders= makeOrderService.findAllMakeOrder(makeOrder,username,CstartDate,CendDate,UstartDate,UendDate,pageable);
        if (makeOrders !=null && makeOrders.getContent().size()>0){
            returnMessages=new ReturnMessages(RequestState.SUCCESS,"有数据！",makeOrder);
        }else {
            returnMessages=new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
        return returnMessages;
    }

    /**
     * 查询用户自己的
     * @return   信息反馈  【状态、对象、信息】
     */
    @RequestMapping(value ="/getMeMakeOrder")
    public ReturnMessages getMeMakeOrder(
            HttpServletRequest request
    ){
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
        MakeOrder makeOrder = makeOrderService.findMakeOrderByMember(username);
        List<SimpleDayOrder> dayOrderList = dayOrderService.findSimpleDayOrderByMakeOrder(makeOrder);
        if (dayOrderList != null && dayOrderList.size()>0) {
            return  new ReturnMessages(RequestState.SUCCESS,"有数据！",dayOrderList);
        } else {
            return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
    }
}

