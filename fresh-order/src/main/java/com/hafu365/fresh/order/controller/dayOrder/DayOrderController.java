package com.hafu365.fresh.order.controller.dayOrder;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.OrderStateConstant;
import com.hafu365.fresh.core.entity.constant.PriceConstant;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.goods.GoodsUtils;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.CheckUserService;
import com.hafu365.fresh.service.order.DayOrderService;
import com.hafu365.fresh.service.order.MakeOrderService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 天订单实现层
 * Created by zhaihuilin on 2017/7/26  16:48.
 */
@Log4j
@RestController
@RequestMapping("/dayOrder")
public class DayOrderController {


    @Autowired
    private MemberService memberService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private DayOrderService dayOrderService;

    @Autowired
    private MakeOrderService makeOrderService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CheckUserService checkUserService;

    /**
     * 新增天订单   预订单
     *
     * @param goodsIdStr   商品编号    数组
     * @param counts       购买数量     数组
     * @param deliverTimes 预定时间   数组
     * @return
     */
    @RequestMapping(value = "/savaDayOrder")
    public ReturnMessages savaDayOrder(
            @RequestParam(name = "goodsId", defaultValue = "", required = false) String[] goodsIdStr,
            @RequestParam(name = "count", defaultValue = "1", required = false) String[] counts,
            @RequestParam(name = "deliverTime", defaultValue = "", required = false) long[] deliverTimes,
            HttpServletRequest request
    ) {
        ReturnMessages returnMessages =null;
        try {
            double GoodsTotalPrice = 0.0;//声明商品的总价 = 商品购买 数量  *  商品的单价
            double goodsPrice = 0.0; //声明 一个 单价
            MakeOrder makeOrder = null;
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
            Store Mystore= storeService.findByMember(member);//获取当前登录用户自己的店铺信息
            if (goodsIdStr == null && counts == null && counts.length < 0 && goodsIdStr.length < 0) {
                return new ReturnMessages(RequestState.ERROR, "传入的商品参数有误", null);
            } else {
                if (counts.length != goodsIdStr.length) {
                    return new ReturnMessages(RequestState.ERROR, "传入的商品数量不一致", null);
                }
            }
            if (goodsIdStr.length <= 0 && !(goodsIdStr.length == counts.length)) {
                returnMessages = new ReturnMessages(RequestState.ERROR, "参数错误。", null);
            }
            makeOrder = makeOrderService.findMakeOrderByMember(username);
            if (makeOrder != null) {
                if (deliverTimes.length > 0) {
                    List<DayOrder> dayOrderList = dayOrderService.findDayOrderByMakeOrder(makeOrder);
                    for (long time : deliverTimes) {
                        int index = existTime(time, dayOrderList);
                        log.info("------------天数:" + time);
                        if (index != -1) {//日期已存在
                            DayOrder dayOrder = dayOrderList.get(index);
                            int dayOrderState = dayOrder.getDayOrderState();
                            if (dayOrderState == OrderStateConstant.DAY_ORDER_STATE_YES) {
                                     log.info("-------该天已生成订单无法在进行添加--------");
                            } else {
                                List<GoodsVo> goodsVoList = dayOrder.getGoodsVoList();
                                if (goodsVoList != null && goodsVoList.size() > 0) {
                                    for (int i = 0; i < goodsIdStr.length; i++) {
                                        Goods goods= goodsService.findByGoodsId(goodsIdStr[i]);
                                        boolean goodsflag=GoodsUtils.theFailGoods(goods); //判断商品是否失效
                                        if (goodsflag == false) {
                                            return new ReturnMessages(RequestState.ERROR, "商品已失效！", null);
                                        }
                                        boolean flag= GoodsUtils.CheckwhetherMeGoods(Mystore,goods);
                                        if (flag ==false){
                                            return  new ReturnMessages(RequestState.ERROR,"商家不能够买自己旗下的商品!",null);
                                        }
                                        goodsVoList = addGoodsVoList(goodsIdStr[i], Integer.valueOf(counts[i]), goodsVoList);
                                    }
                                    dayOrder.setGoodsVoList(goodsVoList);
                                    dayOrderService.updateDayOrders(dayOrder);
                                }
                            }
                        } else { //日期未存在
                            List<GoodsVo> goodsVoList = new ArrayList<GoodsVo>();
                            for (int i = 0; i < goodsIdStr.length; i++) {
                                Goods goods = goodsService.findByGoodsId(goodsIdStr[i]);
                                boolean goodsflag=GoodsUtils.theFailGoods(goods);//判断商品是否失效
                                if (goodsflag == false) {
                                    return new ReturnMessages(RequestState.ERROR, "商品已失效！", null);
                                }
                                if (goods != null) {
                                    goodsPrice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
                                    GoodsTotalPrice = goodsPrice * Integer.valueOf(counts[i]);
                                }
                                boolean flag= GoodsUtils.CheckwhetherMeGoods(Mystore,goods);//判断当前加入的商品是否为属于当前登录用户旗下的店铺
                                if (flag ==false){
                                    return  new ReturnMessages(RequestState.ERROR,"商家不能够买自己旗下的商品!",null);
                                }
                                GoodsVo goodsVo=GoodsUtils.toGoodsVo(goods,Integer.valueOf(counts[i]));
                                goodsVo.setPrice(GoodsTotalPrice);
                                goodsVoList.add(goodsVo);
                            }
                            DayOrder dayOrder = new DayOrder(OrderStateConstant.DAY_ORDER_STATE_NO,time,goodsVoList,makeOrder);
                            dayOrderService.saveDayOrders(dayOrder);
                        }
                    }
                    returnMessages = new ReturnMessages(RequestState.SUCCESS, "添加成功。", makeOrder);
                }
            } else {
                //没有预订单模板的时候
                makeOrder = new MakeOrder(username);
                makeOrder = makeOrderService.saveMakeOrder(makeOrder);
                if (makeOrder !=null){
                    for (long time : deliverTimes) {
                        List<GoodsVo> goodsVoList = new ArrayList<GoodsVo>();
                        for (int i = 0; i < goodsIdStr.length; i++) {
                            Goods goods = goodsService.findByGoodsId(goodsIdStr[i]);
                            boolean goodsflag=GoodsUtils.theFailGoods(goods);
                            if (goodsflag == false) {
                                return new ReturnMessages(RequestState.ERROR, "商品已失效！", null);
                            }
                            if (goods != null) {
                                goodsPrice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
                                GoodsTotalPrice = goodsPrice * Integer.valueOf(counts[i]);
                            }
                            boolean flag= GoodsUtils.CheckwhetherMeGoods(Mystore,goods);//判断当前加入的商品是否为属于当前登录用户旗下的店铺
                            if (flag ==false){
                                return  new ReturnMessages(RequestState.ERROR,"商家不能够买自己旗下的商品!",null);
                            }
                            GoodsVo goodsVo=GoodsUtils.toGoodsVo(goods,Integer.valueOf(counts[i]));
                            goodsVo.setPrice(GoodsTotalPrice);
                            goodsVoList.add(goodsVo);
                        }
                        if (goodsVoList != null && goodsVoList.size() > 0) {
                            DayOrder dayOrder = new DayOrder(OrderStateConstant.DAY_ORDER_STATE_NO,time,goodsVoList,makeOrder);
                            dayOrderService.saveDayOrders(dayOrder);
                            returnMessages = new ReturnMessages(RequestState.SUCCESS, "预约订单成功！", makeOrder);
                        } else {
                            returnMessages = new ReturnMessages(RequestState.ERROR, "预约订单失败!", null);
                        }
                    }
                }else{
                    returnMessages = new ReturnMessages(RequestState.ERROR, "预约订单失败。", null);
                }
            }

        } catch (Exception e) {
            log.info("错误的原因:" + e.getMessage());
            return new ReturnMessages(RequestState.ERROR, "预约订单失败！", null);
        }
        return returnMessages;
    }

    /**
     * 验证时间是否存在
     *
     * @param time
     * @param dayOrderList
     * @return
     */
    public int existTime(long time, List<DayOrder> dayOrderList) {
        if (dayOrderList == null || dayOrderList.size() <= 0) {
            return -1;
        }
        int row = 0;
        for (DayOrder dayOrder : dayOrderList) {
            if (dayOrder.getDeliverTime() == time) {
                return row;
            }
            row++;
        }
        return -1;
    }


    /**
     * 修改订单内商品信息
     *
     * @param goodsId     商品id
     * @param count       商品数量 【负数代表减少商品】
     * @param goodsVoList 【商品列表】
     * @return
     */
    public List<GoodsVo> addGoodsVoList(String goodsId, int count, List<GoodsVo> goodsVoList) {
        int pos = findGoodsVo(goodsVoList, goodsId);
        double goodsorice = 0.0;
        double GoodsTotalPrice = 0.0;
        if (pos >= 0) {
            GoodsVo goodsVo = goodsVoList.get(pos);
            Goods goods = goodsService.findByGoodsId(goodsId);
            if (goods != null) {
                goodsorice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
            }
            int sum = goodsVo.getNumber() + count;
            GoodsTotalPrice = goodsorice * sum;
            if (sum <= 0) {
                goodsVoList.remove(pos);
            } else {
                goodsVo.setGoods(goods);
                goodsVo.setNumber(sum);
                goodsVo.setPrice(GoodsTotalPrice);
                goodsVoList.set(pos, goodsVo);
            }
        } else {
            Goods goods = goodsService.findByGoodsId(goodsId);
            if (goods != null) {
                goodsorice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
                GoodsTotalPrice = goodsorice * count;
                GoodsVo goodsVo=GoodsUtils.toGoodsVo(goods,count);
                goodsVo.setPrice(GoodsTotalPrice);
                goodsVoList.add(goodsVo);
            }
        }
        return goodsVoList;
    }


    /**
     * 查询商品 在GoodsVoList中的位置
     *
     * @param goodsVoList 商品集合
     * @param goodsId     商品编号
     * @return 数值
     */
    public int findGoodsVo(List<GoodsVo> goodsVoList, String goodsId) {
        if (goodsVoList == null || goodsVoList.size() <= 0) {
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
     * 根据天订单编号查询天订单信息
     *
     * @param dayOrderId 天订单编号
     * @return 信息反馈
     */
    @RequestMapping(value = "/findDayOrderBydayOrderId")
    public ReturnMessages findDayOrderBydayOrderId(
            @RequestParam(name = "dayOrderId", defaultValue = "", required = true) String dayOrderId,
            HttpServletRequest request
    ) {
        ReturnMessages returnMessages = null;
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
        if (dayOrderId != null) {
            DayOrder dayOrder = dayOrderService.findDayOrderByDayOrderId(dayOrderId);
            List<GoodsVo> goodsVoList = dayOrder.getGoodsVoList();
            if (goodsVoList != null && goodsVoList.size() > 0) {
                List<Integer> nList = new ArrayList<Integer>();
                for (int i = 0; i < goodsVoList.size(); i++) {
                    Goods goods = goodsService.findByGoodsId(goodsVoList.get(i).getGoodsId());
                    if (goods != null) {
                        boolean flag=GoodsUtils.theFailGoods(goods);
                        if (flag==false){
                            nList.add(i);
                        }
                    } else {
                        goodsVoList.remove(i);//删除商品
                    }
                }
                Collections.reverse(nList);//对集合进行重新排序
                for (int i : nList) {
                    goodsVoList.remove(i);
                }
            }
            dayOrder.setGoodsVoList(goodsVoList);
            dayOrder=dayOrderService.update(dayOrder);
            if (dayOrder != null) {
                return  new ReturnMessages(RequestState.SUCCESS,"有数据！",dayOrder);
            } else {
                return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
            }
        }else{
                return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
    }

    /**
     * 编辑天订单信息
     * @param dayOrderId   天订单编号
     * @param goodsIdStr    商品编号  【可以为多个】
     * @param counts        商品数量  【可以为多个】
     * @param request
     * @return
     */
    @RequestMapping(value = "/updatDayOrder")
    public  ReturnMessages updatDayOrder(
            @RequestParam(name = "dayOrderId") String dayOrderId,
            @RequestParam(name = "goodsId", defaultValue = "", required = false) String[] goodsIdStr,
            @RequestParam(name = "count", defaultValue = "1", required = false) String[] counts,
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
        if (goodsIdStr ==null && counts==null && counts.length<0 && goodsIdStr.length<0){
            return  new ReturnMessages(RequestState.ERROR,"传入的商品参数有误",null);
        }else{
            if(counts.length!=goodsIdStr.length){
            return  new ReturnMessages(RequestState.ERROR,"传入的商品数量不一致",null);
            }
        }
        if (!StringUtils.isNotEmpty(dayOrderId)){
            return  new ReturnMessages(RequestState.ERROR,"该天订单编号不存在！",null);
        }
        DayOrder dayOrder= dayOrderService.findDayOrderByDayOrderId(dayOrderId);
        if (dayOrder ==null){
            return  new ReturnMessages(RequestState.ERROR,"该天订单不存在！",null);
        }
        int dayOrderState=dayOrder.getDayOrderState();
        if (dayOrderState==OrderStateConstant.DAY_ORDER_STATE_YES){
            return  new ReturnMessages(RequestState.ERROR,"该天订单已生成订单,无法进行编辑操作！",null);
        }
        List<GoodsVo>  goodsVoList= dayOrder.getGoodsVoList();
        if (goodsVoList == null){
            return  new ReturnMessages(RequestState.ERROR,"该天订单没有商品信息,无法进行编辑操作！",null);
        }
        for (int i = 0; i < goodsIdStr.length; i++) {
                goodsVoList = UpdateGoodsVoList(goodsIdStr[i], Integer.valueOf(counts[i]), goodsVoList);
         }
         dayOrder.setGoodsVoList(goodsVoList);
        try {
            dayOrder=dayOrderService.updateDayOrders(dayOrder);
            /***判断天订单中的商品集合信息**/
            List<GoodsVo> goodsVos=dayOrder.getGoodsVoList();
            if (goodsVos !=null && goodsVos.size()<=0){
               MakeOrder makeOrder=makeOrderService.findMakeOrderByMember(username);
               if (makeOrder !=null){
                   List<DayOrder> dayOrderList=dayOrderService.findDayOrderByMakeOrder(makeOrder);
                   List<Integer> indexs = new ArrayList<Integer>();
                   List<String>delDayOrderId = new ArrayList<String>();
                   for (int i=0;i<dayOrderList.size();i++){
                       String  key= dayOrderList.get(i).getDayOrderId();
                       if (key.equals(dayOrder.getDayOrderId())){
                           indexs.add(i);
                       }
                   }
                   Collections.reverse(indexs);
                   for (int index:indexs){
                       delDayOrderId.add(dayOrderList.get(index).getDayOrderId());
                       dayOrderList.remove(index);
                   }
                   for (String dayOrderIds:delDayOrderId){
                       dayOrderService.deleteDayOrdersByDayOrderId(dayOrderIds);
                   }
                   makeOrder=makeOrderService.findMakeOrderByMember(username);
                   if (dayOrderList !=null && dayOrderList.size()<=0){
                       makeOrderService.deleteMakeOrderByMakeOrderId(makeOrder.getMakeOrderId());
                   }
                   return   new ReturnMessages(RequestState.SUCCESS,"编辑成功!",null);
               }
            }
            returnMessages=new ReturnMessages(RequestState.SUCCESS,"编辑成功!",dayOrder);
        }catch (Exception e){
            returnMessages=new ReturnMessages(RequestState.ERROR,"编辑失败!",null);
        }
        return  returnMessages;
    }


    /**
     * 找到要修改的商品位置
     * @param goodsId   商品编号
     * @param count      数量
     * @param goodsVoList  商品列表
     * @return  goodsVoList
     */
    public List<GoodsVo> UpdateGoodsVoList(String goodsId, int count, List<GoodsVo> goodsVoList){
        int pos = findGoodsVo(goodsVoList, goodsId);
        double goodsorice = 0.0;
        double GoodsTotalPrice = 0.0;
        if (pos >= 0) {
            GoodsVo goodsVo = goodsVoList.get(pos);
            Goods goods = goodsService.findByGoodsId(goodsId);
            if (goods !=null){
                goodsorice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
            }
            GoodsTotalPrice = goodsorice * count;
            if (count <= 0) {
                goodsVoList.remove(pos);
            } else {
                goodsVo.setGoods(goods);
                goodsVo.setNumber(count);
                goodsVo.setPrice(GoodsTotalPrice);
                goodsVoList.set(pos, goodsVo);
            }
        }
        return goodsVoList;
    }


    /**
     * 找到要删除的商品位置
     * @param goodsId   商品编号
     * @param count      数量
     * @param goodsVoList  商品列表
     * @return  goodsVoList
     */
    public List<GoodsVo> delteGoodsVoList(String goodsId, int count, List<GoodsVo> goodsVoList){
        int pos = findGoodsVo(goodsVoList, goodsId);
        double goodsorice = 0.0;
        double GoodsTotalPrice = 0.0;
        if (pos >= 0) {
            GoodsVo goodsVo = goodsVoList.get(pos);
            Goods goods = goodsService.findByGoodsId(goodsId);
            if (goods !=null){
                goodsorice = goods.getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());
            }
            GoodsTotalPrice = goodsorice * count;
            if (count <= 0) {
                goodsVoList.remove(pos);
            } else {
                goodsVo.setGoods(goods);
                goodsVo.setNumber(count);
                goodsVo.setPrice(GoodsTotalPrice);
                goodsVoList.set(pos, goodsVo);
            }
        }
        return goodsVoList;
    }

    /***
     * 根据天订单编号进行删除
     * @param dayOrderId  天订单编号
     * @param  goodsId   商品编号
     * @return
     */
    @RequestMapping(value = "/deleteDayOrdersByDayOrderId")
    public ReturnMessages deleteDayOrdersByDayOrderId(
            @RequestParam(name = "dayOrderId", defaultValue = "", required = true) String dayOrderId,
            @RequestParam(name = "goodsId",defaultValue = "",required = true) String goodsId,
            @RequestParam(name = "count",defaultValue = "0",required = true) String count,
            HttpServletRequest request
    ) {
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
        if (!StringUtils.isNotEmpty(dayOrderId)){
            return new ReturnMessages(RequestState.ERROR,"传入的天订单编号为空,无法进行操作",null);
        }
        if (!StringUtils.isNotEmpty(goodsId)){
            return new ReturnMessages(RequestState.ERROR,"传入的商品编号为空,无法进行操作",null);
        }
        DayOrder dayOrder= dayOrderService.findDayOrderByDayOrderId(dayOrderId);
        if (dayOrder ==null){
            return new ReturnMessages(RequestState.ERROR,"该天订单不存在,删除失败",null);
        }
        List<GoodsVo> goodsVoList=dayOrder.getGoodsVoList();
        if (goodsVoList !=null && goodsVoList.size()<=0){
            return new ReturnMessages(RequestState.ERROR,"该天订单不存在商品信息,删除失败",null);
        }
        try {
            goodsVoList=delteGoodsVoList(goodsId,Integer.parseInt(count),goodsVoList);
            dayOrder.setGoodsVoList(goodsVoList);
            dayOrder=dayOrderService.updateDayOrders(dayOrder);
            List<GoodsVo> goodsVos=dayOrder.getGoodsVoList();
            if (goodsVos !=null && goodsVos.size()<=0){//说明该天订单中没有商品信息
                MakeOrder makeOrder=makeOrderService.findMakeOrderByMember(username);
                if (makeOrder !=null){
                    List<DayOrder>  dayOrderList=dayOrderService.findDayOrderByMakeOrder(makeOrder);
                    if (dayOrderList !=null && dayOrderList.size()<=0){
                        return new ReturnMessages(RequestState.ERROR,"没有相关信息",null);
                    }
                    List<Integer> indexs = new ArrayList<Integer>();
                    List<String>delDayOrderId = new ArrayList<String>();
                    for (int i=0;i<dayOrderList.size();i++){
                        String  key= dayOrderList.get(i).getDayOrderId();
                        if (key.equals(dayOrder.getDayOrderId())){
                             indexs.add(i);
                        }
                    }
                    Collections.reverse(indexs);
                    for (int index:indexs){
                        delDayOrderId.add(dayOrderList.get(index).getDayOrderId());
                        dayOrderList.remove(index);
                    }
                    for (String dayOrderIds:delDayOrderId){
                        dayOrderService.deleteDayOrdersByDayOrderId(dayOrderIds);
                    }
                    makeOrder=makeOrderService.findMakeOrderByMember(username);
                    if (dayOrderList !=null && dayOrderList.size()<=0){
                        makeOrderService.deleteMakeOrderByMakeOrderId(makeOrder.getMakeOrderId());
                    }
                  return   new ReturnMessages(RequestState.SUCCESS,"编辑成功!",null);
                }
            }
            returnMessages=new ReturnMessages(RequestState.SUCCESS,"编辑成功!",dayOrder);
            return  returnMessages;
        } catch (Exception e) {
            returnMessages=new ReturnMessages(RequestState.ERROR,"编辑失败!",null);
        }
        return returnMessages;
    }
    /**
     * 根据预约时间查询天订单信息列表信息 不分页
     * @param startTime
     * @param endTime
     * @return
     */
    @RequestMapping(value = "/findDayOrdersByTime")
    public ReturnMessages findDayOrdersByTime(
            @RequestParam(name = "startTime", required = false, defaultValue = "") String startTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "") String endTime
    ){
        ReturnMessages returnMessages = null;
        Long startDate = null;
        Long endDate = null;
        try {
            if (StringUtils.isNotEmpty(startTime)) {
                startDate = Long.valueOf(startTime);
            }
            if (StringUtils.isNotEmpty(endTime)) {
                endDate = Long.valueOf(endTime);
            }
            List<DayOrder>  dayOrderList=dayOrderService.findDayOrderBydeliverTime(startDate,endDate);
            if (dayOrderList !=null && dayOrderList.size()>0){
                return new ReturnMessages(RequestState.SUCCESS,"有数据！",dayOrderList);
            }else {
                return new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
            }
        } catch (Exception e) {
            log.info("时间参数有误:" + e.getMessage());
                return new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
        }
    }


}
