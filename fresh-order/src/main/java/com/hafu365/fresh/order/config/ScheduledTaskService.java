//package com.hafu365.fresh.order.config;
//
//import com.google.gson.ExclusionStrategy;
//import com.google.gson.FieldAttributes;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//import com.hafu365.fresh.core.entity.common.ReturnMessages;
//import com.hafu365.fresh.core.entity.constant.OrderStateConstant;
//import com.hafu365.fresh.core.entity.constant.RequestState;
//import com.hafu365.fresh.core.entity.goods.GoodsVo;
//import com.hafu365.fresh.core.entity.member.Member;
//import com.hafu365.fresh.core.entity.order.DayOrder;
//import com.hafu365.fresh.core.entity.order.MakeOrder;
//import com.hafu365.fresh.core.entity.order.OrderDaddress;
//import com.hafu365.fresh.core.entity.order.Orders;
//import com.hafu365.fresh.core.entity.store.Store;
//import com.hafu365.fresh.core.utils.QRCodeUtil;
//import com.hafu365.fresh.core.utils.TemplateFileUtil;
//import com.hafu365.fresh.service.member.MemberService;
//import com.hafu365.fresh.service.order.DayOrderService;
//import com.hafu365.fresh.service.order.MakeOrderService;
//import com.hafu365.fresh.service.order.OrderDaddressService;
//import com.hafu365.fresh.service.order.OrderService;
//import lombok.extern.log4j.Log4j;
//import org.apache.commons.collections.map.HashedMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.File;
//import java.lang.reflect.Type;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// * 订单生成器
// * Created by zhaihuilin on 2017/8/8  16:07.
// */
//@Service
//@Transactional
//@Log4j
//public class ScheduledTaskService {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private DayOrderService dayOrderService;
//
//    @Autowired
//    private OrderDaddressService orderDaddressService;
//    @Autowired
//    private QRCodeConfig qrCodeConfig;
//    @Autowired
//    private MakeOrderService makeOrderService;
//
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
//
//    @Scheduled(fixedRate =50000)
//    public void reportCurrentTime(){
//        log.info("每隔五秒执行一次:"+dateFormat.format(new Date()));
//        log.info("现在的时间毫秒数:"+new Date().getTime());
//        log.info("明天这个的时间毫秒数:"+(new Date().getTime()+86400000));
//        log.info("后天这个的时间毫秒数:"+(new Date().getTime()+(86400000*2)));
//        log.info("大后天这个的时间毫秒数:"+(new Date().getTime()+(86400000*3)));
//        log.info("后后天这个的时间毫秒数:"+(new Date().getTime()+(86400000*4)));
//
//    }
//    /**
//     * 订单自动生成    暂时写死
//     * 每天晚上11点执行
//     * @return
//     */
//    //@Scheduled(cron="0 41 10 * * ?")
////    public void OrderCreateTimer(){
////
////        log.info("开始执行任务:"+new Date());
////
//////        String orderDaddressId="1";
////        String memberId="M20170814170704005";
////        Orders orders= new Orders();
////        ReturnMessages returnMessages=new ReturnMessages();
////        //声明一个订单总价 = 所有商品总价之和
////        double OrdersTotalPrice = 0.0;
////        //根据用户编号查询用户信息
////        if (memberId !=null && memberId.length()>0){
////            Member member= memberService.findMemberByMemberId(memberId);
////            List<OrderDaddress> orderDaddressList=orderDaddressService.findOrderDaddressByMember(member);
////            if (orderDaddressList.size()>0){
////                    for (OrderDaddress orderDaddress:orderDaddressList){
////                            if (orderDaddress.isIsdefault()==true){
////                                orders.setOrderDaddress(orderDaddress);
////                            }
////                    }
////            }else if (orderDaddressList.size()>0 && orderDaddressList.size()==1){
////                            //有地址并且只要一条数据的时候
////                            orders.setOrderDaddress(orderDaddressList.get(0));
////            }
////            /*************设置订单 所属用户******************/
////            orders.setMember(member);
////        }
////        /*************设置订单 订单状态********20：代表待发货**********/
////        orders.setOrderState(OrderStateConstant.ORDER_STATE_UNFILLE);
////        /**
////         * 获取预定的时间   用来与【系统约定的时间： 每前一天晚上11:00生成订单】 进行比较
////         *
////         * 1天(d)=86400000毫秒(ms)
////         * 1时(h)= 3600000毫秒(ms)
////         * 1天(d)=24时(h)
////         * 23时(h)=82800000毫秒(ms)
////         */
////        long sysTime=new Date().getTime();//前一天晚上23点的当前时间
////        log.info("现在的时间是:"+new Date());
////        log.info("现在的时间毫秒是:"+new Date().getTime());
////        long StartdeliverTime=new Date().getTime() +3600000*24;//预定时间的开始时间
////        long enddeliverTime=new Date().getTime()+24*3600000*2;//预定时间的结束时间
////        //根据天订单的预定时间为条件查询
////        List<DayOrder>  dayOrderList= dayOrderService.findDayOrderBydeliverTime(StartdeliverTime,enddeliverTime);
////        log.info("----------------长度------------------:"+dayOrderList.size());
////        if (dayOrderList.size()>0){
////            for ( DayOrder dayOrder:dayOrderList){
////                List<GoodsVo> goodsVoList=dayOrder.getGoodsVoList();
////                String dayOrderId=dayOrder.getDayOrderId();
////                /*************设置订单 商品列表信息******************/
////                orders.setGoodsList(goodsVoList);
////                for (GoodsVo goodsVo:goodsVoList){
////                    Store store=goodsVo.getGoods().getStore();//获取改商品所属的店铺
////                    ExclusionStrategy es = new ExclusionStrategy() {
////                        @Override
////                        public boolean shouldSkipField(FieldAttributes fa) {
////
////                            return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList") || fa.getName().equals("member");
////                        }
////
////                        @Override
////                        public boolean shouldSkipClass(Class<?> aClass) {
////                            return false;
////                        }
////                    };
////                    Gson gson = new GsonBuilder().setExclusionStrategies(es).create();
////                    String storeStr=gson.toJson(store);
////                    Type type = new TypeToken<Store>() {}.getType();
////                    store = gson.fromJson(storeStr,type);
////                    orders.setStore(store);
////                    OrdersTotalPrice=OrdersTotalPrice+goodsVo.getPrice(); //订单总价计算
////                }
////                dayOrder.setDayOrderState(OrderStateConstant.DAY_ORDER_STATE_YES);
////                dayOrder.setDayOrderId(dayOrderId);
////                dayOrderService.updateDayOrders(dayOrder);
////                /*************设置订单 总价******************/
////                orders.setPrice(OrdersTotalPrice);
////            }
////            /*************设置订单 订单的生成时间*****************/
////            try {
////                orders.setCreateTime(new Date().getTime());
////                orders=orderService.saveOrders(orders);
////                /********************************二维码生成***********************************************************/
////                String  orderId=orders.getOrdersId();//获取订单编号
////                String textt = "http://192.168.0.17:8080/fresh-order/order/CheckQRCode?orderId="+orderId;
////                String imgName="hafu.jpg";
////                File file= TemplateFileUtil.getQrecodeLogo(imgName);//获取文件
////                String  imgPath=file.getPath();//在获取文件所在路劲
////                String destPath=qrCodeConfig.QRCODEIMG_PATH;//二维码生成地址路劲
////                System.out.println("生成成功");
////                QRCodeUtil.encode(textt,imgPath,destPath,true);
////                Map<String,Object> returnMap = new HashedMap();
////                String fileName=orderId+".jpg";
////                String qrcodeUrl=qrCodeConfig.QRCODEBASE_URL+"//"+fileName;//获取到二维码的链接链接地址
////                orders.setQRCodeImg(qrcodeUrl);
////                orders.setOrdersId(orderId);
////                orderService.updateOrders(orders);//在进行一次编辑
////                if (qrcodeUrl !=null){
////                    returnMap.put("downUrl",qrcodeUrl);
////                    returnMessages.setContent(returnMap);
////                    returnMessages.setState(RequestState.SUCCESS);
////                    returnMessages.setMessages("生成二维码成功");
////                }else{
////                    returnMessages.setState(RequestState.ERROR);
////                    returnMessages.setMessages("生成二维码失败");
////                }
////            }catch (Exception e){
////                returnMessages.setMessages("保存失败");
////                log.info("保存失败"+e.getMessage());
////                returnMessages.setState(RequestState.ERROR);
////            }
////        }else {
////            returnMessages.setMessages("没有要生成订单条件,保存失败");
////            log.info("没有要生成订单条件,保存失败");
////            returnMessages.setState(RequestState.ERROR);
////        }
////    }
//
//
//    /**
//     * 订单自动生成
//     */
////    @Scheduled(cron="0 25 14 * * ?")
//    @Scheduled(fixedRate =50000)
//    public   void  AutoCreateOrder(){
//        log.info("开始执行任务"+new Date());
//        ReturnMessages returnMessages=new ReturnMessages();
//        //声明一个订单总价 = 所有商品总价之和
//        double OrdersTotalPrice = 0.0;
//        long sysTime=new Date().getTime();//前一天晚上23点的当前时间
//        long StartdeliverTime=new Date().getTime() +3600000*24;//预定时间的开始时间
//        long enddeliverTime=new Date().getTime()+24*3600000*2;//预定时间的结束时间
//        //获取所有的预订单列表信息
//        List<MakeOrder> makeOrderList=makeOrderService.getMakeOrderList();
//        Orders orders=new Orders();//实例化订单实体对象
//         if (makeOrderList.size()>0){
//               //遍历
//               for (MakeOrder makeOrder:makeOrderList){
//                    List<DayOrder> dayOrderList=makeOrder.getDayOrderList();//获取天订单列表信息
///********************************************************************************************************/
//                   //遍历所有的天订单 找出在符合生成订单条件
//                  if (dayOrderList.size()>0){
//                         for (DayOrder dayOrder:dayOrderList){
//                              long getDeliverTime=dayOrder.getDeliverTime();//获取配送时间
//                              //刷选
//                              if((getDeliverTime>=StartdeliverTime)&&(getDeliverTime<=enddeliverTime)){
//                                  List<GoodsVo> goodsVoList=dayOrder.getGoodsVoList(); //获取商品集合信息
//                                 /*****************设置订单商品信息************************************/
//                                  orders.setGoodsList(goodsVoList);
//                                  String dayOrderId=dayOrder.getDayOrderId();//获取天订单编号
//                                  for (GoodsVo goodsVo:goodsVoList){
//                                      Store store=goodsVo.getGoods().getStore();//获取改商品所属的店铺
//                                      ExclusionStrategy es = new ExclusionStrategy() {
//                                          @Override
//                                          public boolean shouldSkipField(FieldAttributes fa) {
//                                              return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList") || fa.getName().equals("member");
//                                          }
//                                          @Override
//                                          public boolean shouldSkipClass(Class<?> aClass) {
//                                              return false;
//                                          }
//                                      };
//                                      Gson gson = new GsonBuilder().setExclusionStrategies(es).create();
//                                      String storeStr=gson.toJson(store);
//                                      Type type = new TypeToken<Store>() {}.getType();
//                                      store = gson.fromJson(storeStr,type);
//                                      /*************设置订单 所属店铺**********************/
//                                      orders.setStore(store);
//                                      OrdersTotalPrice=OrdersTotalPrice+goodsVo.getPrice(); //订单总价计算
//                                  }
//                                  /******************************设置天订单信息**更改其状态************************************/
//                                  dayOrder.setDayOrderState(OrderStateConstant.DAY_ORDER_STATE_YES);
//                                  dayOrder.setDayOrderId(dayOrderId);
//                                  dayOrderService.updateDayOrders(dayOrder);
//                                  /********************************************************************/
//                                  Member member=makeOrder.getMember();//获取所属用户
//                                  List<OrderDaddress> orderDaddressList=orderDaddressService.findOrderDaddressByMember(member);//根据用户获取该用户的配送地址信息
//                                  if (orderDaddressList.size()>0){
//                                      for (OrderDaddress orderDaddress:orderDaddressList){
//                                          if (orderDaddress.isIsdefault()==true){
//                                              /*************设置订单 配送地址信息**********************/
//                                              orders.setOrderDaddress(orderDaddress);
//                                          }
//                                      }
//                                  }else if (orderDaddressList.size()>0 && orderDaddressList.size()==1){
//                                      /*************设置订单 配送地址信息**********************/
//                                      orders.setOrderDaddress(orderDaddressList.get(0)); //有地址并且只要一条数据的时候
//                                  }
//                                  /*************设置订单 用户**********************************/
//                                  orders.setMember(member);
//                                  /*************设置订单 订单状态********20：代表待发货**********/
//                                  orders.setOrderState(OrderStateConstant.ORDER_STATE_UNFILLE);
//                                  /*************设置订单 总价**********************************/
//                                  orders.setPrice(OrdersTotalPrice);
//                                  /*************设置订单生成时间******************************/
//                                  orders.setCreateTime(new Date().getTime());
//                                  try {
//                                      /**************保存订单信息*********************/
//                                      orders=orderService.saveOrders(orders);
//                                      /********************************二维码生成***********************************************************/
//                                      String  orderId=orders.getOrdersId();//获取订单编号
//                                      String textt = "http://192.168.0.17:8080/fresh-order/order/CheckQRCode?orderId="+orderId;
//                                      String imgName="hafu.jpg";
//                                      File file= TemplateFileUtil.getQrecodeLogo(imgName);//获取文件
//                                      String  imgPath=file.getPath();//在获取文件所在路劲
//                                      String destPath=qrCodeConfig.QRCODEIMG_PATH;//二维码生成地址路劲
//                                      System.out.println("生成成功");
//                                      QRCodeUtil.encode(textt,imgPath,destPath,true);
//                                      Map<String,Object> returnMap = new HashedMap();
//                                      String fileName=orderId+".jpg";
//                                      String qrcodeUrl=qrCodeConfig.QRCODEBASE_URL+"//"+fileName;//获取到二维码的链接链接地址
//                                      orders.setQRCodeImg(qrcodeUrl);
//                                      orders.setOrdersId(orderId);
//                                      orderService.updateOrders(orders);//在进行一次编辑
//                                      if (qrcodeUrl !=null){
//                                          returnMap.put("downUrl",qrcodeUrl);
//                                          returnMessages.setContent(returnMap);
//                                          returnMessages.setState(RequestState.SUCCESS);
//                                          returnMessages.setMessages("生成二维码成功");
//                                      }else{
//                                          returnMessages.setState(RequestState.ERROR);
//                                          returnMessages.setMessages("生成二维码失败");
//                                      }
//                                  }catch (Exception e){
//                                       e.printStackTrace();
//                                  }
//
//                              }
//                         }
//                  }
//               }
//         }
//
//
//     }
//
//}
