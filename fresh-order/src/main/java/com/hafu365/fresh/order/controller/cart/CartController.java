package com.hafu365.fresh.order.controller.cart;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.cart.Cart;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.PriceConstant;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.Constants;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.cart.CartService;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.goods.GoodsUtils;
import com.hafu365.fresh.service.order.CheckUserService;
import com.hafu365.fresh.service.store.StoreService;
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
import java.util.*;

/**
 * Created by zhaihuilin on 2017/7/24  17:17.
 */
@RestController
@Log4j
@RequestMapping("/cart")
public class CartController implements Constants {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private CheckUserService checkUserService;



    /**
     * 获取用户自己的购物列表信息
     * 获取购物车所有列表信息   顺便剔除不满足商品使用的条件
     * @param page     第几页
     * @param pageSize 每页显示的条数
     * @return
     */
    @RequestMapping(value = "/findcart")
    public ReturnMessages findcart(
            @RequestParam(name = "page", required = false, defaultValue = "0") String page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "20") String pageSize,
            HttpServletRequest request
    ) {
        ReturnMessages returnMessages=null;
        Pageable pageable = new PageRequest(Integer.parseInt(page),Integer.parseInt(pageSize));
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
        Page<Cart> cartPage = cartService.findCartByMember(username,pageable);
        if (cartPage !=null && cartPage.getContent().size()>0){
            for (Cart cart:cartPage){
                List<GoodsVo> goodsVoList=cart.getGoodsVoList();
                if (goodsVoList !=null && goodsVoList.size()>0) {
                    for (GoodsVo goodsVo : goodsVoList) {
                        String getGoodsId = goodsVo.getGoodsId();
                        Goods goods = goodsService.findByGoodsId(getGoodsId);
                        if (goods == null) {//商品失效
                            goodsVo.setIslose(Boolean.TRUE);//商品失效
                            List<GoodsVo> NewgoodsVoList = new ArrayList<GoodsVo>();//声明一个
                            NewgoodsVoList.add(goodsVo);
                            cart.setGoodsVoList(NewgoodsVoList);
                            cartService.updateCart(cart);
                          //cartService.deleteCartByCartId(cart.getCartId()); //删除商品
                        } else {
                            boolean flag= GoodsUtils.theFailGoods(goods);//判断商品是否是失效
                            if (flag==true){
                                goodsVo.setGoods(goods);
                                List<GoodsVo> NewgoodsVoList = new ArrayList<GoodsVo>();//声明一个
                                NewgoodsVoList.add(goodsVo);
                                cart.setGoodsVoList(NewgoodsVoList);
                                cartService.updateCart(cart);
                            }else{//商品失效
                                goodsVo.setIslose(Boolean.TRUE);//商品失效
                                goodsVo.setGoods(goods);
                                List<GoodsVo> NewgoodsVoList = new ArrayList<GoodsVo>();//声明一个
                                NewgoodsVoList.add(goodsVo);
                                cart.setGoodsVoList(NewgoodsVoList);
                                cartService.updateCart(cart);
                              //cartService.deleteCartByCartId(cart.getCartId());//删除商品
                            }
                        }
                    }
                }
            }
        }
        try {
            Page<Cart> carts = cartService.findCartByMember(username,pageable);//再次查询一次
            if (cartPage !=null && cartPage.getContent().size()>0){
                return  new ReturnMessages(RequestState.SUCCESS,"有数据!",carts);
            }else{
                return  new ReturnMessages(RequestState.ERROR,"暂无数据!",null);
            }
        }catch (Exception e){
                return  new ReturnMessages(RequestState.ERROR,"加载数据异常....",null);
        }
    }

    /*
     * 获取购物车所有列表信息   不分页
     * @return
     */
    @RequestMapping(value = "/findcartByMember")
    public ReturnMessages findcartByMember(
            HttpServletRequest request
    ) {
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
        List<Cart> cartList = cartService.findCartListByMember(username);
        try {
            if (cartList !=null && cartList.size() > 0 ) {
                return  new ReturnMessages(RequestState.SUCCESS,"有数据！",cartList);
            } else {
                return  new ReturnMessages(RequestState.ERROR,"您还没有创建购物车！",null);
            }
        } catch (Exception e) {
                return  new ReturnMessages(RequestState.ERROR,"加载数据异常...！",null);
        }
    }

    @RequestMapping(value = "/saveCart")
    public  ReturnMessages save(
            @RequestParam(name = "goodsId")  String goodsId,
            @RequestParam(name = "count") int count,
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
        Goods goods=goodsService.findGoodsByGoodsId(goodsId);//2.根据商品编号获取商品信息
        Map<String, Double> Price=new HashMap<String, Double>();
        if (goods == null){
             return  new ReturnMessages(RequestState.ERROR,"该商品不存在！",null);
        }
        Store store1= storeService.findByMember(member);//获取当前登录用户自己的店铺信息
        boolean flag=GoodsUtils.CheckwhetherMeGoods(store1,goods);
        if (flag ==false){
            return  new ReturnMessages(RequestState.ERROR,"商家不能够买自己旗下的商品!",null);
        }
        double GoodsTotalPrice=0.0;//声明商品的总价 = 商品购买 数量  *  商品的单价
        double goodsPrice=0.0; //声明 一个 单价
        Integer TotalNumber=0;//声明一个购买总数量
        Price= goods.getPrice(); //获取到 mAP 集合
        if (Price ==null){
            return  new ReturnMessages(RequestState.ERROR,"该商品价格不存在！",null);
        }
        goodsPrice = Price.get(PriceConstant.GOODS_MARKET_PRICE.toString());
        //获取该用户所有的购物车信息列表
        List<Cart> cartList=cartService.findCartListByMember(username);
        if (cartList != null && cartList.size()>0){  //有购物车
             for (int i=0;i<cartList.size();i++){
                   Cart cart =cartList.get(i);
                   List<GoodsVo> goodsVos=cart.getGoodsVoList();//获取订单商品集合信息
                   if (goodsVos !=null && goodsVos.size()>0){
                        for (GoodsVo goodsVo :goodsVos){
                             String GetgoodsId = goodsVo.getGoodsId();//获取商品编号进行对比
                             if (GetgoodsId.equals(goodsId)){
                                 goods=goodsService.findByGoodsId(GetgoodsId);//2.根据商品编号获取商品信息
                                 /****获取的商品编号与传过来的商品编号一一致*****************/
                                 Integer GetNumber = goodsVo.getNumber();//获取原来的该商品的购买数量
                                 TotalNumber = GetNumber + count; //计算获取总的购买数量
                                 GoodsTotalPrice = TotalNumber * goodsPrice;// 计算获取总的商品总价
                                 /*************************/
                                 goodsVo.setGoods(goods);
                                 goodsVo.setNumber(TotalNumber);      //设置 订单商品总购买的数量
                                 goodsVo.setPrice(GoodsTotalPrice);  //设置 订单商品商品总价
                                 List<GoodsVo> goodsVoList1 = new ArrayList<GoodsVo>();
                                 goodsVoList1.add(goodsVo);
                                 /*************************/
                                 cart.setCartId(cart.getCartId());
                                 cart.setUsername(username);
                                 cart.setCreateTime(new Date().getTime());
                                 cart.setPrice(GoodsTotalPrice);
                                 cart.setGoodsVoStr(null);
                                 cart.setGoodsVoList(goodsVoList1);
                                 try {
                                     cart=cartService.updateCart(cart);
                                     return  new ReturnMessages(RequestState.SUCCESS,"添加成功!",cart);
                                 }catch (Exception e){
                                     return  new ReturnMessages(RequestState.ERROR,"添加失败!",null);
                                 }
                             }
                        }
                   }
             }
        }
/******************************没有购物车*************************************************/
        GoodsVo goodsVo=GoodsUtils.toGoodsVo(goods,count);
        GoodsTotalPrice= count * goodsPrice; //计算总的商品付款金额
        goodsVo.setPrice(GoodsTotalPrice);
        List<GoodsVo> goodsVoList=new ArrayList<GoodsVo>();//声明一个集合
        goodsVoList.add(goodsVo);
        /********************************************************/
        Cart cart=new Cart(GoodsTotalPrice,username,new Date().getTime(),0,goodsVoList);//实例化购物车
        /********************************************************/
        try {
            cart=cartService.saveCart(cart);//保存购物车信息
            if (cart !=null){
                return new ReturnMessages(RequestState.SUCCESS,"添加成功!",cart);
            }else{
                return new ReturnMessages(RequestState.ERROR,"添加失败!",null);
            }
        }catch (Exception e){
            return new ReturnMessages(RequestState.ERROR,"添加失败!",null);
        }
    }

    /**
     * 编辑 购物车
     *
     * @param cartId    购物车编号
     * @param goodsId   商品编号
     * @param count    购买数量
     * @return
     */
    @RequestMapping(value = "/updateCart")
    public ReturnMessages updateCart(
            @RequestParam(name = "cartId")   String cartId,
            @RequestParam(name = "goodsId")  String goodsId,
            @RequestParam(name = "count")    int count,
            HttpServletRequest request
    ) {
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
        double GoodsTotalPrice = 0.0;//声明商品的总价 = 商品购买 数量  *  商品的单价
        double goodsPrice = 0.0; //声明 一个 单价
        try {
            //根据 购物车 编号查询到 购物车信息
            Cart cart = cartService.findCartByCartId(cartId);
            if (cart != null) {
                    List<GoodsVo> goodsVoList = cart.getGoodsVoList();
                    Goods goods = goodsService.findByGoodsId(goodsId);
                    if (goods !=null){
                        /*************对商品信息进行数据处理,过滤掉不需要的数据***************************/
                        Gson gson1 = new GsonBuilder().setExclusionStrategies(retrunes()).create();
                        String goodsStr=gson1.toJson(goods);
                        Type type1=new TypeToken<Goods>() {}.getType();
                        goods=gson1.fromJson(goodsStr,type1);
                        Map<String, Double> Price = goods.getPrice();//获取到 mAP 集合
                        goodsPrice=Price.get(PriceConstant.GOODS_MARKET_PRICE.toString());//获取市场价
                    }
                    if (goodsVoList !=null &&goodsVoList.size() > 0 ) {
                        for (GoodsVo goodsVo : goodsVoList) {
                            goodsVo.setGoods(goods);
                            goodsVo.setNumber(count); //设置购买数量
                            GoodsTotalPrice = goodsPrice * count; //计算商品的总价
                            goodsVo.setPrice(GoodsTotalPrice);
                            List<GoodsVo> goodsVoList1 = new ArrayList<GoodsVo>(); //声明一个集合 用来接收 GoodVo 的信息
                            goodsVoList1.add(goodsVo);
                            cart.setGoodsVoStr(null);
                            cart.setGoodsVoList(goodsVoList1);
                        }
                    }
                    cart.setPrice(GoodsTotalPrice);
                    cart.setUpdateTime(new Date().getTime());//设置 购物车的编辑时间
                    cart.setCartId(cart.getCartId());
                    cart=cartService.updateCart(cart);
                    if (cart !=null){
                        return new ReturnMessages(RequestState.SUCCESS,"编辑成功！",cart);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
                    }
            } else{
                        return new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
            }
        } catch (Exception e){
                        return new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
        }
    }

    /**
     * 根据  购物车 编号进行删除  （可批量删除)
     * @param cartIdStr 购物车编号
     * @return  信息反馈
     */
    @RequestMapping(value = "/deleteCartByCartId")
    public ReturnMessages deleteCartByCartId(
            @RequestParam(name = "cartIds") String cartIdStr,
            HttpServletRequest request
    ) {
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
        if (cartIdStr !=null && cartIdStr.length()>0) {
                try {
                    String[] cartIds = cartIdStr.split(",");
                    for (String cartId : cartIds) {
                        boolean flag = cartService.deleteCartByCartId(cartId);
                        if (flag == true) {
                            returnMessages= new ReturnMessages(RequestState.SUCCESS,"删除成功！",flag);
                        } else {
                            returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",null);
                        }
                     }
                } catch (Exception e) {
                    returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",null);
                }
        }else{
                    returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",null);
        }
        return  returnMessages;
    }
    /**
     * 根据  商品编号进行删除  （可批量删除)
     * @param goodsStr 商品编号
     * @return  信息反馈
     */
    @RequestMapping(value = "/deleteCartByGoodsId")
    public ReturnMessages deleteCartByGoodsId(
            @RequestParam(name = "goodsIds") String goodsStr,
            HttpServletRequest request
    ) {
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
        if (goodsStr !=null && goodsStr.length()>0){
              try {
                  String[] goodsIds=goodsStr.split(",");
                  List<Cart> cartList=cartService.findCartListByMember(username);//获取该用户所有的购物车信息
                  if(cartList!=null && cartList.size()>0){
                        for (Cart cart :cartList){
                            List<GoodsVo> goodsVoList=cart.getGoodsVoList();
                            if (goodsVoList !=null && goodsVoList.size()>0){
                                 for (GoodsVo goodsVo:goodsVoList){
                                     String  GETgoodsId=goodsVo.getGoodsId();
                                     for (String goodsId:goodsIds){
                                          if (goodsId.equals(GETgoodsId)){   //判断传过来的商品编号和列表的商品编号进行对比
                                              String   cartId=cart.getCartId();
                                              boolean flag = cartService.deleteCartByCartId(cartId);
                                              if (flag == true) {
                                                  returnMessages= new ReturnMessages(RequestState.SUCCESS,"删除成功！",flag);
                                              } else {
                                                  returnMessages= new ReturnMessages(RequestState.ERROR,"删除失败！",flag);
                                              }
                                          }
                                     }
                                 }
                            }
                        }
                  }
              }catch (Exception e){
                   returnMessages=new ReturnMessages(RequestState.ERROR,"删除失败",null);
              }
        }else{
                   returnMessages=new ReturnMessages(RequestState.ERROR,"没有此编号,删除失败",null);
        }
        return returnMessages;
    }

    /**
     * 根据 购物编号 进行查询
     * @param cartId
     * @return
     */
    @RequestMapping(value = "/findCartBycartId")
    public  ReturnMessages findCartBycartId(
            @RequestParam(name = "cartId") String cartId,
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
        if (cartId !=null && cartId.length()>0) {
            try {
                Cart cart = cartService.findCartByCartId(cartId);
                if (cart != null) {
                  return  new ReturnMessages(RequestState.SUCCESS,"有数据！",cart);
                } else {
                  return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
                }
            } catch (Exception e) {
                  return  new ReturnMessages(RequestState.ERROR,"暂无数据！",null);
            }
        }else{
            return  new ReturnMessages(RequestState.ERROR,"没有此编号！",null);
        }
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
}