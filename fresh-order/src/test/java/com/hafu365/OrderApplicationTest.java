//package com.hafu365;
//
//import com.hafu365.fresh.core.entity.cart.Cart;
//import com.hafu365.fresh.core.entity.common.ReturnMessages;
//import com.hafu365.fresh.core.entity.constant.PriceConstant;
//import com.hafu365.fresh.core.entity.constant.RequestState;
//import com.hafu365.fresh.core.entity.goods.Goods;
//import com.hafu365.fresh.core.entity.goods.GoodsVo;
//import com.hafu365.fresh.core.entity.member.Member;
//import com.hafu365.fresh.core.entity.order.DayOrder;
//import com.hafu365.fresh.core.entity.order.MakeOrder;
//import com.hafu365.fresh.core.entity.store.Store;
//import com.hafu365.fresh.order.OrderApplication;
//import com.hafu365.fresh.repository.cart.CartRepository;
//import com.hafu365.fresh.service.cart.CartService;
//import com.hafu365.fresh.service.goods.GoodsService;
//import com.hafu365.fresh.service.member.MemberService;
//import com.hafu365.fresh.service.order.DayOrderService;
//import com.hafu365.fresh.service.order.MakeOrderService;
//import com.hafu365.fresh.service.store.StoreService;
//import junit.framework.TestCase;
//import lombok.extern.log4j.Log4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * 单元测试
// * Created by zhaihuilin on 2017/7/20  18:15.
// */
//@RunWith(SpringJUnit4ClassRunner.class) //Junit的注解，通过这个注解让SpringJUnit4ClassRunner这个类提供spring测试上下文
//@SpringBootTest(classes = OrderApplication.class)
//@WebAppConfiguration
//@EntityScan(value = "com.hafu365.fresh.core.entity.**")
//@ComponentScan({"com.hafu365.fresh.service.**","com.hafu365.fresh.order.controller.**"})
//@SpringBootApplication
//@EnableJpaRepositories("com.hafu365.fresh.repository.**")
//@Profile("application-dev.yml")
//@Log4j
//public class OrderApplicationTest extends TestCase {
//
//   public static void main( String[] args ) {
//        SpringApplication.run(OrderApplicationTest.class,args);
//    }
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private MemberService memberService;
//
//    @Autowired
//    private StoreService storeService;
//
//    @Autowired
//    private GoodsService goodsService;
//
//    @Autowired
//    private DayOrderService dayOrderService;
//
//    @Autowired
//    private MakeOrderService makeOrderService;
//
//
//    /**
//     * 测试 保存购物信息
//     * @throws Exception
//     */
//   @Test
//    public void testsaveCart() throws  Exception{
//       Cart cart=new Cart();
//       String memberId="M20170726114837946";
//       Member member=memberService.findMemberByMemberId(memberId);
//       cart.setMember(member);
//       int count=30;
//       GoodsVo goodsVo=new GoodsVo();
//       goodsVo.setNumber(count);
//       String goodsId="G20170726140427915";
//       Goods goods= goodsService.findByGoodsId(goodsId);
//       Map<String,Double> price =goods.getPrice();//获取商品的单价
//       //遍历map 集合 获取 到 map 中的 价格值
//       double goodsPrice=0.0;
//       //声明商品的总价 = 商品购买 数量  *  商品的单价
//       double GoodsTotalPrice=0.0;
//       //声明购物车的总价 = 所有商品总价之和
//       double CartTotalPrice=0.0;
//       goodsPrice=price.get(PriceConstant.GOODS_MARKET_PRICE.toString());//获取市场价
//       GoodsTotalPrice=goodsPrice *count; //计算商品的总价
//       System.out.println("获取的商品总价是:"+GoodsTotalPrice);
//       goodsVo.setGoods(goods);
//       goodsVo.setGoodsId(goodsId);
//       goodsVo.setGoodsprice(price);
//       goodsVo.setPrice(GoodsTotalPrice);  //设置 商品的总价
//       cart.setPrice(GoodsTotalPrice);
//       List<GoodsVo> goodsVoList;
//       goodsVoList = new ArrayList<GoodsVo>();
//       goodsVoList.add(goodsVo);
//       cart.setGoodsVoList(goodsVoList);
//       cart.setCreateTime(new Date().getTime());
//       cartRepository.save(cart);
//       System.out.println("保存的购物车信息:"+cart);
//    }
//
//    /**
//     * 测试 某用户 所创建的 购物车 信息
//     * @throws Exception
//     */
//    @Test
//    public  void  testfindcart() throws  Exception{
//       int page=0;
//       int pageSize=20;
//       Pageable pageable=new PageRequest(page,pageSize);
//       String memberId="M20170726133852163"; //查询此编号所创建的购物车
//       Member member=memberService.findMemberByMemberId(memberId);
//       Page<Cart> carts= cartService.findCartByMember(member,pageable);
//       System.out.println("获取的列表长度是:"+carts.getContent().size());
//       if (carts.getContent().size()>0){
//           for (Cart cart: carts){
//               System.out.println("展示购物车信息:"+cart);
//           }
//           System.out.println("获取到数据:"+carts.toString());
//       }
//    }
//
//    /**
//     *  测试 编辑 购物车 信息
//     * @throws Exception
//     */
//    @Test
//    public void testupdateCart() throws  Exception{
//          //声明商品的总价 = 商品购买 数量  *  商品的单价
//          double GoodsTotalPrice = 0.0;
//          //声明购物车的总价 = 所有商品总价之和
//          double CartTotalPrice = 0.0;
//          //声明 一个 单价
//          double goodsPrice = 0.0;
//          String cartId="C20170731135409773";//购物车编号
//          String goodsId="G20170727111926237";// 商品编号
//          int count=120; //购买的商品数量
//          //根据 购物车编号查询 购物车信息
//          Cart cart= cartService.findCartByCartId(cartId);
//          System.out.println("编辑前:------->获取的购物车信息:"+cart.toString());
//          // 根据 商品编号 进行 查询 商品信息
//          Goods goods= goodsService.findByGoodsId(goodsId);
//          Map<String,Double> price=goods.getPrice();
//          goodsPrice=price.get(PriceConstant.GOODS_MARKET_PRICE.toString());//获取市场价
//        System.out.println("获取的商品信息:"+goods.toString());
//          if (cart !=null){
//              List<GoodsVo> goodsVoList=cart.getGoodsVoList();
//              if (goodsVoList.size()>0){
//                  for (GoodsVo goodsVo: goodsVoList){
//                      goodsVo.setNumber(count);//设置购买的数量
//                      GoodsTotalPrice=count * goodsPrice; //计算商品的总价
//                      goodsVo.setPrice(GoodsTotalPrice);
//                      //声明一个集合 用来接受 商品映射信息
//                      List<GoodsVo> goodsVoList1=new ArrayList<GoodsVo>();
//                      goodsVoList1.add(goodsVo);
//                      cart.setGoodsVoList(goodsVoList1);
//                      cart.setGoodsVoStr(null);
//                  }
//              }
//              cart.setCartId(cartId);
//              cart.setPrice(GoodsTotalPrice);//设置购物车的总价
//              cart.setUpdateTime(new Date().getTime());//设置 购物车的编辑时间
//              cartService.updateCart(cart);
//              System.out.println("编辑后的购物车信息:"+cart);
//          }
//
//    }
//
//    /**
//     * 测试 根据 购物车 编号进行删除
//     * @throws Exception
//     */
//    @Test
//    public  void  testdeleteCartByCartId() throws  Exception {
//        String cartId = "C20170727111525933";  //设定 要删除的 购物车 编号
//        Cart cart = cartService.findCartByCartId(cartId);
//        if (cart != null) {
//            System.out.println("获取的购物车信息:" + cart.toString());
//            boolean flag = cartService.deleteCartByCartId(cartId);
//            System.out.println("获取的结果 flag :" + flag);
//            if (flag == true) {
//                System.out.println(" 删除成功:" + cart.toString());
//            } else {
//                System.out.println(" 哎呀 ！删除失败咯");
//            }
//        }
//
///*********************************** 下面代码是实现批量删除*************************************************************/
////        String  cartIdStr="C20170726140635559,C20170726140635558";  //设定 要删除的 购物车 编号
////        if (cartIdStr !=null){
////            String[] cartIds=  cartIdStr.split(",");
////            for (String cartId:cartIds){
////                Cart cart= cartService.findCartByCartid(cartId);
////                if (cart !=null){
////                    System.out.println("获取的购物车信息:"+cart.toString());
////                    boolean flag=cartService.deleteCartByCartid(cartId);
////                    System.out.println("获取的结果 flag :"+flag);
////                    if (flag ==true){
////                        System.out.println(" 删除成功:"+cart.toString());
////                    }else {
////                        System.out.println(" 哎呀 ！删除失败咯" );
////                    }
////                }
////            }
////        }
////    }
//
//    }
//    /**
//     * 测试 天订单的新增
//     * @throws Exception
//     */
//   @Test
//   public  void testsaveDayOrder() throws Exception{
//       String[] counts={"10"};
//       String[] goodsIdStr={"G20170814111332011"};
//       long[] deliverTimes={new Date().getTime(),new Date().getTime()+86400056,new Date().getTime()+172800088};
//       //声明商品的总价 = 商品购买 数量  *  商品的单价
//       double GoodsTotalPrice = 0.0;
//       //声明 一个 单价
//       double goodsPrice = 0.0;
//       if (deliverTimes.length>0 || goodsIdStr !=null || counts.length>0 ){
//           for (long deliverTime:deliverTimes){
//               List<GoodsVo> goodsVoList = new ArrayList<GoodsVo>();//声明一个集合
//               DayOrder dayOrder = new DayOrder();//实例化
//               dayOrder.setDeliverTime(deliverTime);
//               dayOrder.setDayOrderState(20);
//                   for (int i=0;i<goodsIdStr.length;i++){
//                       GoodsVo goodsVo=new GoodsVo();//实例化
//                       log.info("长度:"+goodsIdStr.length+"编号:"+goodsIdStr[i]);
//                       //根据商品编号获取商品信息
//                       Goods goods= goodsService.findByGoodsId(goodsIdStr[i]);
//                       Store store= goods.getStore();//获取商品所属店铺
//                       Map<String,Double> price=goods.getPrice();
//                       goodsPrice=price.get(PriceConstant.GOODS_MARKET_PRICE.toString());//获取市场价
//                       GoodsTotalPrice= goodsPrice * Integer.parseInt(counts[i]) ;//计算获取商品的总价
//                       goodsVo.setPrice(GoodsTotalPrice);
//                       goodsVo.setNumber(Integer.parseInt(counts[i]));
//                       goodsVo.setGoodsId(goodsIdStr[i]);
//                       goodsVo.setGoods(goods);
//                       goodsVo.setGoodsprice(price);
//                       goodsVo.setStore(store);
//                       goodsVoList.add(goodsVo);//加载
//                       log.info("添加的时间:"+deliverTime+"商品的编号:"+goodsIdStr[i]+"购买数量:"+Integer.parseInt(counts[i])+"遍历的 i的值");
//                   }
//                       dayOrder.setGoodsVoList(goodsVoList);
//                   try {
//                       dayOrder=dayOrderService.saveDayOrders(dayOrder);
//                       log.info("添加成功");
//                       log.info("新增的天订单信息:"+dayOrder.toString());
//                   }catch (Exception e){
//                       log.info("添加失败"+e.getMessage());
//                   }
//               }
//           }
//       }
//
//    /**
//     * 测试 天订单的编辑
//     * @throws Exception
//     */
//   @Test
//   public void  testupdateDayOrder() throws Exception{
//       String[] dayOrderIds={"DO20170804131017086"};
//       String[] counts={"0","100","300"};
//       String[] goodsIdStr={"G20170803113602750","G20170803113649321","G20170803113629473"};
////     long[]   deliverTimes={new Date().getTime(),new Date().getTime()+86400056,new Date().getTime()+172800088};
//       //声明商品的总价 = 商品购买 数量  *  商品的单价
//       double GoodsTotalPrice = 0.0;
//       //声明 一个 单价
//       double goodsPrice = 0.0;
//       //声明一个集合 用来接收 GoodVo 的信息
//       if (dayOrderIds.length>0 && dayOrderIds !=null){
//           for (String dayOrderId :dayOrderIds){
//               List<GoodsVo> goodsVoList1 = new ArrayList<GoodsVo>();//声明一个集合
//               DayOrder dayOrder = dayOrderService.findDayOrderByDayOrderId(dayOrderId);
//               List<GoodsVo> goodsVoList=dayOrder.getGoodsVoList();//获取该天订单下的商品列表信息
//               if (goodsVoList !=null && goodsVoList.size()>0){
//                   for(int i=0;i<goodsVoList.size();i++){
//                       if (goodsIdStr!=null && goodsIdStr.length>0){
//                           for (int j=0; j<goodsIdStr.length;j++){
//                               GoodsVo goodsVo=goodsVoList.get(i);
//                               String getgoodsId=goodsVo.getGoodsId();//获取商品编号    与 传过来的商品编号进行比较
//                               String goodsId=goodsIdStr[j];  //遍历穿过来的商品编号
//                               if (getgoodsId.equals(goodsId)){ //商品编号一致
//                                   Goods goods=goodsService.findByGoodsId(goodsId);
//                                   Map<String,Double> Price =goods.getPrice();//获取商品单价
//                                   goodsPrice=Price.get(PriceConstant.GOODS_MARKET_PRICE.toString());//获取市场价
//                                   goodsVo.setGoodsId(goodsId);
//                                   goodsVo.setGoods(goods);
//                                   if (counts.length>0){
//                                       int count=Integer.parseInt(counts[j]);
//                                       if (count<=0){   //当传过来的数量小于或等于0  表示删除了改商品  操作字符串
//                                           goodsId=goodsIdStr[j]="";
//                                           Goods goods1 = goodsService.findByGoodsId(goodsId);
//                                           goodsVo.setGoods(goods1);
//                                           goodsVo.setNumber(0);
//                                           goodsVo.setPrice(0.0);
//                                           goodsVo.setGoodsId(goodsId);
//                                           goodsVoList1.add(null);  //重新加载     清空这一块的数据
//                                       }else {
//                                           goodsVo.setNumber(Integer.parseInt(counts[j])); //设置购买数量
//                                           GoodsTotalPrice = goodsPrice * Integer.parseInt(counts[j]); //计算商品的总价
//                                           goodsVo.setPrice(GoodsTotalPrice);
//                                           goodsVoList1.add(goodsVo);  //重新加载
//                                       }
//                                   }
//                                   dayOrder.setGoodsVoList(goodsVoList1);
//                                   dayOrder.setGoodsVoStr(null);
//                               }else {
//                                   //没有的话 就相当于新增
//                               }
//                           }
//                       }
//
//                   }
//               }
//               dayOrder.setDayOrderId(dayOrderId);
//               try {
//                   dayOrder=dayOrderService.updateDayOrders(dayOrder);
//                   log.info(RequestState.SUCCESS);
//                   log.info("编辑成功");
//                   log.info("编辑的信息:"+dayOrder.toString());
//               }catch (Exception e){
//                   log.info(RequestState.ERROR);
//                   log.info("编辑失败");
//               }
//           }
//       }
//   }
//
//    /**
//     * 测试 根据 天订单编号进行查询
//     * @throws Exception
//     */
//   @Test
//   public void testfindDayOrderBydayOrderId() throws Exception{
//       String dayOrderId="DO20170804113738151";
//       DayOrder dayOrder= dayOrderService.findDayOrderByDayOrderId(dayOrderId);
//       if (dayOrder !=null){
//           log.info("获取到数据"+dayOrder.toString());
//       }else {
//           log.info("没有获取到数据");
//       }
//   }
//
//
//    /**
//     * 测试 根据 天订单编号进行删除
//     * @throws Exception
//     */
//   @Test
//   public void  testdeleteDayOrderBydayOrderId() throws  Exception{
//       String dayOrderIdStr="DO20170804110732517,DO20170804110732568";
//       try {
//           if (dayOrderIdStr !=null && dayOrderIdStr.length()>0){
//               String[] dayOrderIds=dayOrderIdStr.split(",");
//               for (String dayOrderId:dayOrderIds){
//                   DayOrder dayOrder=dayOrderService.findDayOrderByDayOrderId(dayOrderId);
//                   if (dayOrder !=null){
//                       boolean flag=dayOrderService.deleteDayOrdersByDayOrderId(dayOrderId);
//                       if (flag==true){
//                           log.info("删除成功");
//                           log.info("删除的dayOrder"+dayOrder.toString());
//                       }else {
//                           log.info("删除失败");
//                       }
//                   }
//               }
//           }
//       }catch (Exception e){
//           log.info("删除失败");
//       }
//   }
//
//    /**
//     * 测试 天订单的条件查询
//     * @throws Exception
//     */
//   @Test
//   public  void testfindDayOrderAll() throws Exception{
//       int dayOrderState=20;
//       Long startDate=new Date().getTime()-3600000*3;
//       Long endDate = new Date().getTime()-1800000;
//       int page=0;
//       int pageSize=20;
//       Pageable pageable = new PageRequest(page,pageSize);
//       Date startTime = null;
//       Date endTime = null;
//       if(startDate != null && startDate > 0){
//           startTime = new Date(startDate);
//       }
//       if(endDate != null && endDate > 0){
//           endTime = new Date(endDate);
//       }
////       Page<DayOrder> dayOrderPage = dayOrderService.findAll(dayOrderState,startTime,endTime,pageable);
////       if (dayOrderPage.getContent().size()>0  && dayOrderPage !=null){
////           log.info("获取的数据dayOrderPage"+dayOrderPage);
////       }else {
////           log.info("没有获取到数据");
////       }
//   }
//
//    /**
//     * 测试 预约订单新增
//     * @throws Exception
//     */
// @Test
// public  void  testsaveMakeOrder() throws  Exception{
//     String memberId="M20170807133343531";
//     String[] dayOrderIds={"DO20170804131034701","DO20170804131033235"};
//     //实例化预约订单实体类
//     MakeOrder makeOrder=new MakeOrder();
//     ReturnMessages returnMessages=new ReturnMessages();
//     if (memberId !=null && memberId.length()>0){
//         //根据用户编号查询用户信息
//         Member member= memberService.findMemberByMemberId(memberId);
//         makeOrder.setMember(member);
//     }
//     if (dayOrderIds !=null && dayOrderIds.length>0){
//         //声明一个集合 用来接收 天订单
//         List<DayOrder> dayOrderList=new ArrayList<DayOrder>();
//         for (String dayOrderId:dayOrderIds){
//             DayOrder dayOrder=dayOrderService.findDayOrderByDayOrderId(dayOrderId);
//             dayOrderList.add(dayOrder);//加载
//             makeOrder.setDayOrderList(dayOrderList);
//         }
//     }
//     makeOrder.setCreateTime(new Date().getTime());//创建时间
//     try {
//         makeOrder=  makeOrderService.saveMakeOrder(makeOrder);
//         log.info("新增的预约订单信息:"+makeOrder.toString());
//     }catch (Exception e){
//         log.info("新增失败:"+e.getMessage());
//     }
// }
//
//    /**
//     * 测试根据预约订单标号查询
//     * @throws Exception
//     */
// @Test
// public void  testfindMakeOrderBymakeOrderId() throws  Exception{
//     String makeOderId="MO20170807135100972";
//    MakeOrder makeOrder= makeOrderService.findMakeOrderByMakeOrderId(makeOderId);
//    if (makeOrder !=null){
//        log.info("获取到数据:"+makeOrder);
//    }else {
//        log.info("没有获取到数据");
//    }
// }
//
//    /**
//     * 测试预约订单删除
//     * @throws Exception
//     */
//    @Test
// public void testdeleteMakeOrderByMakeOrderId() throws  Exception{
//    String[] makeOrderIds={"MO20170807135100972"};
//        if (makeOrderIds.length>0){
//            for (String makeOrderId:makeOrderIds){
//                if (makeOrderId !=null){
//                    MakeOrder makeOrder=makeOrderService.findMakeOrderByMakeOrderId(makeOrderId);
//                    if (makeOrder !=null){
//                        boolean flag=makeOrderService.deleteMakeOrderByMakeOrderId(makeOrderId);
//                        if (flag==true){
//                            log.info("删除成功"+makeOrder.toString());
//                        }else {
//                            log.info("删除失败...");
//                        }
//                    }else {
//                        log.info("没有此编号...");
//                    }
//                }
//            }
//        }
// }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
