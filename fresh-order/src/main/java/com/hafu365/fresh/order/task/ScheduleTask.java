package com.hafu365.fresh.order.task;

import com.hafu365.fresh.core.entity.common.CommonDateUtils;
import com.hafu365.fresh.core.entity.constant.OrderStateConstant;
import com.hafu365.fresh.core.entity.constant.PriceConstant;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.OrderDaddress;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.entity.store.StoreVo;
import com.hafu365.fresh.core.utils.QRCodeUtil;
import com.hafu365.fresh.core.utils.TemplateFileUtil;
import com.hafu365.fresh.order.config.QRCodeConfig;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.goods.GoodsUtils;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.DayOrderService;
import com.hafu365.fresh.service.order.OrderDaddressService;
import com.hafu365.fresh.service.order.OrderService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务类   订单自动生成
 * Created by zhaihuilin on 2017/9/24  13:29.
 */
@Configuration
@Component   //必须加
@EnableScheduling
@Log4j
@Transactional
public class ScheduleTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    @Autowired
    MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private DayOrderService dayOrderService;

    @Autowired
    private OrderDaddressService orderDaddressService;
    @Autowired
    private QRCodeConfig qrCodeConfig;
    @Autowired
    private MemberInfoService memberInfoService;

    @PersistenceContext
    @Qualifier(value = "entityManagerFactory")
    private EntityManager em;

    /**
     * 订单自动生成
     */
    public void AutoCreateOrder() {
        long time = CommonDateUtils.getDayUnix(CommonDateUtils.getDay()+1);//获取当天的时间
        log.info("获取当天的时间是:"+time);
        List<DayOrder> dayOrders = dayOrderService.findDayOrderByDeliverTime(time);
        for (DayOrder dayOrder : dayOrders) {
            String username = dayOrder.getMakeOrder().getUsername();
            MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
            Member member = memberService.findMemberByUsername(username);
            if (verifyMemberInfo(memberInfo)) {
                OrderDaddress orderDaddress = orderDaddressService.findDefaultOrderDaddressByUsername(username);//获取用户指定的默认地址
                if (orderDaddress != null) {
                    if (dayOrder.getDayOrderState() == OrderStateConstant.DAY_ORDER_STATE_YES)
                        continue;
                    Map<StoreVo, List<GoodsVo>> map = new HashMap<StoreVo, List<GoodsVo>>();
                    for (GoodsVo goodsVo : dayOrder.getGoodsVoList()) {
                        StoreVo store = goodsVo.getStore();
                        List<GoodsVo> goodsVos = map.get(store);
                        if (goodsVos == null || goodsVos.size() == 0) {
                            goodsVos = new ArrayList<GoodsVo>();
                        }
                        goodsVos.add(goodsVo);
                        map.put(store, goodsVos);
                    }
                    for (StoreVo key : map.keySet()) {
                        Orders orders = new Orders();
                        double OrdersTotalPrice = 0.0;//声明一个订单总价 = 所有商品总价之和
                        double GoodsTotalPrice = 0.0;//声明商品的总价 = 商品购买 数量  *  商品的单价
                        double goodsPrice = 0.0;//声明 一个 单价
                        List<GoodsVo> goodsVos = map.get(key);//根据键值获取相对应的商品集合信息
                        if (goodsVos != null && goodsVos.size() > 0) {
                            Store store = null;
                            for (GoodsVo goodsVo : goodsVos) {
                                Goods goods=goodsService.findByGoodsId(goodsVo.getGoodsId());
                                if (!GoodsUtils.theFailGoods(goods))//商品失效就跳过
                                    continue;
//                                if(!goodsService.isExist(goodsVo.getGoodsId()))
//                                    continue;
                                StoreVo storeVo = goodsVo.getStore();//获取所属店铺
                                store = storeService.findByStoreId(storeVo.getStoreId());//获取店铺【将Storevo 转换为store】
                                goodsPrice = goodsVo.getGoods().getPrice().get(PriceConstant.GOODS_MARKET_PRICE.toString());//获取商品的单价
                                GoodsTotalPrice = goodsPrice * goodsVo.getNumber();
                                OrdersTotalPrice = OrdersTotalPrice + GoodsTotalPrice;
                            }
//                            orderDaddress.setMember(member);
                          //  orders = new Orders(OrdersTotalPrice, OrderStateConstant.ORDER_STATE_UNFILLE, new Member(member), new OrderDaddress(orderDaddress), new Store(store), goodsVos, null);
                            orders = new Orders(OrdersTotalPrice, OrderStateConstant.ORDER_STATE_UNFILLE,username, new OrderDaddress(orderDaddress), new Store(store), goodsVos, null);

                            orders = orderService.saveOrders(orders);
                            if (orders != null) {
                                /********************************二维码生成***********************************************************/
                                String orderId = orders.getOrdersId();//获取订单编号
                                String textt = qrCodeConfig.QRCODEText_URL + "?orderId=" + orderId;//生成二维码的内容
                                String imgName = "hafu.jpg";
                                File file = null;//获取文件
                                try {
                                    file = TemplateFileUtil.getQrecodeLogo(imgName);
                                    String imgPath = file.getPath();//在获取文件所在路劲
                                    String destPath = qrCodeConfig.QRCODEIMG_PATH;//二维码生成地址路劲
                                    System.out.println("生成成功");
                                    QRCodeUtil.encode(textt, imgPath, destPath, true);
                                    String fileName = orderId + ".jpg";
                                    String qrcodeUrl = qrCodeConfig.QRCODEBASE_URL + "//" + fileName;//获取到二维码的链接链接地址
                                    orders.setQRCodeImg(qrcodeUrl);
                                    orders.setOrdersId(orderId);
                                    orderService.updateOrders(orders);//在进行一次编辑
                                    dayOrderService.deleteDayOrdersByDayOrderId(dayOrder.getDayOrderId());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                }
            }
        }
         //遍历第二天要生成的订单的天订单 对失效的商品做标记
         deleteOrderContentDelGoods(time);
    }


    /**
     * 将失效商品进行标记
     */
    public void deleteOrderContentDelGoods(long time) {
        /**
         * 1.获取所有删除的商品ID
         * 2.遍历所有次日的订单
         * 3.String indexof 商品订单号
         * 4.goodsvo 转换为对象删除商品信息
         * 5.保存天订单
         */
        time = CommonDateUtils.getDayUnix(CommonDateUtils.getDay()+2);//获取后天的时间
        List<String> StrId = goodsService.findDelGoods(time);
        //刷选满足生成第二天的订单的天订单
        List<DayOrder> dayOrderList= dayOrderService.findDayOrderByDeliverTime(time);
        if (dayOrderList !=null && dayOrderList.size()>0){
             for (DayOrder dayOrder:dayOrderList){
                 List<GoodsVo> goodsVoList = dayOrder.getGoodsVoList();
                 if (goodsVoList != null && goodsVoList.size() > 0) {
                     for (int i = 0; i < goodsVoList.size(); i++) {
                          String goodsId=goodsVoList.get(i).getGoodsId();
                          Goods goods=goodsService.findByGoodsId(goodsId);
                          if (!GoodsUtils.theFailGoods(goods)){//失效商品
                              goodsVoList = UpdateGoodsVoList(goodsVoList.get(i).getGoodsId(), goodsVoList);
                          }
                     }
                 }
                 dayOrder.setGoodsVoStr(null);
                 dayOrder.setGoodsVoList(goodsVoList);
                 dayOrderService.update(dayOrder);
             }
        }
    }

    /**
     * 找到要修改的商品位置
     *
     * @param goodsId     商品编号
     * @param goodsVoList 商品列表
     * @return goodsVoList
     */
    public List<GoodsVo> UpdateGoodsVoList(String goodsId, List<GoodsVo> goodsVoList) {
        int pos = findGoodsVo(goodsVoList, goodsId);
        if (pos >= 0) {
            GoodsVo goodsVo = goodsVoList.get(pos);
            goodsVo.setIslose(Boolean.TRUE);
            goodsVoList.set(pos, goodsVo);
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
     * 判断用户状态
     * @param memberInfo
     * @return
     */
    private boolean verifyMemberInfo(MemberInfo memberInfo) {
        if (memberInfo == null) {
            log.info("未完善资料。");
            return false;
        } else {
            String state = memberInfo.getState();
            if (state == null) {
                log.info(memberInfo.getUsername() + " : 状态未获取。");
                return false;
            }
            if (state.equals(StateConstant.USER_STATE_CHECK_ING.toString())) {
                log.info(memberInfo.getUsername() + " : 未通过审核。");
                return false;
            }
            if (state.equals(StateConstant.USER_STATE_CHECK_OFF.toString())) {
                log.info(memberInfo.getUsername() + " : 审核失败。");
                return false;
            }
            if (state.equals(StateConstant.USER_STATE_LOCK_ING.toString())) {
                log.info(memberInfo.getUsername() + " : 账号已被锁定。");
                return false;
            }
        }
        return true;
    }
}
