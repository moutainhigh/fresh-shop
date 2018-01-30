package com.hafu365.fresh.goods.controller.goods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.*;
import com.hafu365.fresh.core.entity.constant.PriceConstant;
import com.hafu365.fresh.core.entity.goods.*;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.*;
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
 * 商品
 * Created by HuangWeizhen on 2017/8/2.
 */
@Slf4j
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private GoodsClassService gcService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ModifyAttrGoodsService modifyAttrGoodsService;

    @Autowired
    private ModifyPriceGoodsService modifyPriceGoodsService;

    @Autowired
    private GoodsStockService stockService;

    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private RoleService roleService;

    /**
     * 发布商品
     *
     * @param goodsTitle    商品标题
     * @param goodsSubTitle 商品副标题[可空]
     * @param marketPrice   市场价
     * @param costPrice     成本价
     * @param storeId       商品所属店铺id[可空]
     * @param classId       商品所属分类id
     * @param brandId       商品所属品牌id[可空]
     * @param imgs          商品图片集合
     * @param goodsBody     商品详情
     * @param keywords      商品关键字[可空]
     * @param repositoryNum 商品库存数量
     * @param sku           商品库存单元
     * @param commission    商品提取佣金
     * @param goodsShow     商品显示状态[可空]["true","false"]
     * @param soldInTime    商品上架起始时间
     * @param soldOutTime   商品上架结束时间
     * @return
     */
    @RequestMapping("/save")
    public ReturnMessages addGoods(@RequestParam(name = "goodsTitle", required = true) String goodsTitle,
                                   @RequestParam(name = "goodsSubTitle", required = false) String goodsSubTitle,
                                   @RequestParam(name = "marketPrice", required = true) double marketPrice,
                                   @RequestParam(name = "costPrice", required = true) double costPrice,
                                   @RequestParam(name = "storeId", required = false) String storeId,
                                   @RequestParam(name = "classId", required = true) String classId,
                                   @RequestParam(name = "brandId", required = false) String brandId,
                                   @RequestParam(name = "imgs", required = true) String imgs,
                                   @RequestParam(name = "goodsBody", required = true) String goodsBody,
                                   @RequestParam(name = "keywords", required = false) String keywords,
                                   @RequestParam(name = "repositoryNum", required = true) long repositoryNum,
                                   @RequestParam(name = "sku", required = true) String sku,
                                   @RequestParam(name = "commission", required = true) double commission,
                                   @RequestParam(name = "goodsShow", required = false) String goodsShow,
                                   @RequestParam(name = "soldInTime", required = true) long soldInTime,
                                   @RequestParam(name = "soldOutTime", required = true) long soldOutTime,
                                   HttpServletRequest request
    ) {

        ReturnMessages rm = null;
        Goods goods = new Goods();
        GoodsStock goodsStock = new GoodsStock();

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member member = memberService.findMemberByUsername(userName);
        if(member == null){ //用户不存在
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
        if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员可以发布默认店铺商品，也可以代发布商品
            //商品所属店铺
            if (StringUtils.isNotEmpty(storeId)) {
                Store store = storeService.findByStoreId(storeId);
                if (store != null) {
                    if (store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) {//店铺处于审核通过状态才能添加商品
                        goods.setStore(store);
                    } else {
                        return new ReturnMessages(RequestState.ERROR, "店铺未通过审核！", null);
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "店铺不存在！", null);

                }
            } else {    //不选店铺默认添加默认店铺的商品
                Store defaultStore = storeService.findStoreByTheDefaultTrue();
                if(defaultStore != null ){
                    goods.setStore(defaultStore);
                }
            }
        } else if (roleCodeStr.contains("ROLE:SELLER")) {  //是商家,发布自己店铺的商品
            if (userStore == null) {
                return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
            } else {
                if (userStore.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) {//店铺处于审核通过状态才能添加商品
                    goods.setStore(userStore);  //默认添加自己的商品
                } else {
                    return new ReturnMessages(RequestState.ERROR, "您的店铺未通过审核！", null);
                }
            }
        } else {
            return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
        }

        //商品标题
        if (StringUtils.isNotEmpty(goodsTitle)) {
            goods.setGoodsTitle(goodsTitle);
        } else {
            return new ReturnMessages(RequestState.ERROR, "商品名称不能为空！", null);
        }

        //商品副标题
        if (StringUtils.isNotEmpty(goodsSubTitle)) {
            goods.setGoodsSubTitle(goodsSubTitle);
        }

        //商品价格
        Map<String, Double> priceMap = new HashMap<String, Double>();
        if (marketPrice < 0 || costPrice < 0) {
            return new ReturnMessages(RequestState.ERROR, "价格不能为负数！", null);
        } else {
            priceMap.put(PriceConstant.GOODS_MARKET_PRICE.toString(), marketPrice);
            priceMap.put(PriceConstant.GOODS_COST_PRICE.toString(), costPrice);
            goods.setPrice(priceMap);
        }


        //商品所属分类
        if (StringUtils.isNotEmpty(classId)) {
            GoodsClass gc = gcService.findById(classId);
            if (gc != null) {
                if (gc.isGcShow()) {
                    goods.setGoodsClass(gc);
                } else {
                    return new ReturnMessages(RequestState.ERROR, "分类不显示！", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "分类不存在！", null);
            }
        } else {
            return new ReturnMessages(RequestState.ERROR, "商品所属分类不能为空！", null);
        }

        Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
        //商品所属品牌
        if (StringUtils.isNotEmpty(brandId)) {
            Brand brand = brandService.findById(brandId);
            if (brand != null) {
                if (brand.getState().equals(StateConstant.BRAND_STATE_CHECK_ON.toString())) {//品牌审核通过才能添加商品
                    goods.setBrand(brand);
                } else {
                    return new ReturnMessages(RequestState.ERROR, "品牌未通过审核！", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "品牌不存在！", null);
            }
        } else {
            if (defaultBrand != null) {
                goods.setBrand(defaultBrand);
            }

        }

        //商品图片
        if (StringUtils.isNotEmpty(imgs)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {
            }.getType();
            try {
                List<Image> imgList = gson.fromJson(imgs, type);
                goods.setGoodsPic(imgList);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "商品图片请求参数有误！", null);
            }

        }

        //商品描述
        if (StringUtils.isNotEmpty(goodsBody)) {
            goods.setGoodsBody(new StringBuffer(goodsBody));
        } else {
            return new ReturnMessages(RequestState.ERROR, "商品详情不能为空！", null);
        }

        //商品关键字
        if (StringUtils.isNotEmpty(keywords)) {
            goods.setKeywords(keywords);
        }

        //库存数量
        if (repositoryNum < 0) {
            return new ReturnMessages(RequestState.ERROR, "商品库存不能为负数！", null);
        } else {
            goodsStock.setStockNum(repositoryNum);
        }

        //库存单位
        if (StringUtils.isNotEmpty(sku)) {
            goodsStock.setSku(sku);
        } else {
            return new ReturnMessages(RequestState.ERROR, "商品库存单位不能为空！", null);
        }

        //佣金
        if (commission < 0) {
            return new ReturnMessages(RequestState.ERROR, "佣金不能为负数！", null);
        } else {
            goods.setCommission(commission);
        }

        //创建时间
        goods.setCreateTime(System.currentTimeMillis());

        //是否显示
        if (StringUtils.isNotEmpty(goodsShow)) {
            goods.setGoodsShow(Boolean.valueOf(goodsShow));
        } else {
            goods.setGoodsShow(true);
        }

        //是否删除
        goods.setDel(Boolean.FALSE);

        //上架时间
        goods.setSoldInTime(soldInTime);

        //下架时间
        goods.setSoldOutTime(soldOutTime);

        //商品状态
        goods.setState(StateConstant.GOODS_STATE_ON_CHECKING.toString());

        Goods goodsRes = goodsService.save(goods, goodsStock);
        if (goodsRes != null) {
            rm = new ReturnMessages(RequestState.SUCCESS, "添加成功!", goodsRes);
        } else {
            rm = new ReturnMessages(RequestState.ERROR, "添加失败!", null);

        }
        return rm;
    }


    /**
     * 编辑商品
     *
     * @param goodsId       商品id
     * @param goodsTitle    商品标题[可空]
     * @param goodsSubTitle 商品副标题[可空]
     * @param marketPrice   商品市场价[可空]
     * @param costPrice     商品成本价[可空]
     * @param classId       商品所属分类[可空]
     * @param brandId       商品所属品牌[可空]
     * @param imgs          商品图片集合[可空]
     * @param goodsBody     商品详情[可空]
     * @param keywords      关键字[可空]
     * @param repositoryNum 商品库存数量[可空]
     * @param sku           商品库存单元[可空]
     * @param commission    商品提取佣金[可空]
     * @param goodsShow     商品显示状态[可空]["true","false"]
     * @param soldInTime    商品上架起始时间[可空]
     * @param soldOutTime   商品上架结束时间[可空]
     * @param reason        编辑原因[可空]
     * @return
     */
    @RequestMapping("/edit")
    public ReturnMessages editGoods(@RequestParam(name = "goodsId", required = true) String goodsId,
                                    @RequestParam(name = "goodsTitle", required = false) String goodsTitle,
                                    @RequestParam(name = "goodsSubTitle", required = false) String goodsSubTitle,
                                    @RequestParam(name = "marketPrice", required = false) Double marketPrice,
                                    @RequestParam(name = "costPrice", required = false) Double costPrice,
                                    @RequestParam(name = "classId", required = false) String classId,
                                    @RequestParam(name = "brandId", required = false) String brandId,
                                    @RequestParam(name = "imgs", required = false) String imgs,
                                    @RequestParam(name = "goodsBody", required = false) String goodsBody,
                                    @RequestParam(name = "keywords", required = false) String keywords,
                                    @RequestParam(name = "repositoryNum", required = false) Long repositoryNum,
                                    @RequestParam(name = "sku", required = false) String sku,
                                    @RequestParam(name = "commission", required = false) Double commission,
                                    @RequestParam(name = "goodsShow", required = false) String goodsShow,
                                    @RequestParam(name = "soldInTime", required = false) Long soldInTime,
                                    @RequestParam(name = "soldOutTime", required = false) Long soldOutTime,
                                    @RequestParam(name = "reason", required = false) String reason,
                                    HttpServletRequest request
    ) {

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member member = memberService.findMemberByUsername(userName);
        if(member == null){ //用户不存在
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

        Goods goodsSearch = null;
        if (StringUtils.isNotEmpty(goodsId)) {
            goodsSearch = goodsService.findByGoodsId(goodsId);
            if (goodsSearch == null) {
                return new ReturnMessages(RequestState.ERROR, "商品不存在！", null);
            }
        } else {
            return new ReturnMessages(RequestState.ERROR, "商品参数有误！", null);
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
        Store userStore = storeService.findByMember(member);//获取用户店铺
        Store goodsStore = goodsSearch.getStore();
        if (roleCodeStr.contains("ROLE:ADMIN")) { //有管理员权限,可以编辑店铺的商品

        } else if (roleCodeStr.contains("ROLE:SELLER")) {  //是商家,只能编辑自己店铺的商品
            if (userStore == null) {
                return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
            } else {
                String brandStoreId = "";
                String userStoreId = userStore.getStoreId();
                if (goodsStore != null) {
                    brandStoreId = goodsStore.getStoreId();
                    if (!brandStoreId.equals(userStoreId)) {
                        return new ReturnMessages(RequestState.ERROR, "商家只能编辑自己店铺的商品！", null);
                    } else if (!(userStore.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString()))) {
                        return new ReturnMessages(RequestState.ERROR, "您的店铺未通过审核！", null);
                    } else {

                    }
                }
            }

        } else {
            return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
        }

        ReturnMessages rm = new ReturnMessages();
        Goods goods = new Goods();  //  不需审核的属性编辑
        Goods goods_attr = new Goods(); //需审核的属性编辑商品
        Goods goods_price = new Goods();    //价格编辑商品
        Goods goods_update = new Goods();   //审核不通过的商品编辑
        GoodsStock stock = new GoodsStock();
        int attrNum = 0;
        int noCheckNum = 0;
        int stockNum = 0;
        int priceNum = 0;

        //商品id
        BeanUtils.copyProperties(goodsSearch, goods);
        BeanUtils.copyProperties(goodsSearch, goods_attr);
        BeanUtils.copyProperties(goodsSearch, goods_price);
        GoodsStock stockSearch = goodsSearch.getGoodsStock();
        if (stockSearch != null) {
            BeanUtils.copyProperties(stockSearch, stock);
        }


        //商品标题
        if (StringUtils.isNotEmpty(goodsTitle) && !(goods_attr.getGoodsTitle().equals(goodsTitle))) {
            goods_attr.setGoodsTitle(goodsTitle);
            attrNum = attrNum + 1;
        }

        //商品副标题
        if(StringUtils.isNotEmpty(goodsSubTitle)){
            if(StringUtils.isNotEmpty(goods_attr.getGoodsSubTitle())){  //原来的商品副标题不为空
                if(!goodsSubTitle.equals(goods_attr.getGoodsSubTitle())){   //新的副标题与原来的不相等
                    goods_attr.setGoodsSubTitle(goodsSubTitle);
                    attrNum = attrNum + 1;
                }else{  //相等
                    //do nothing
                }

            }else{  //原来的商品副标题为空
                goods_attr.setGoodsSubTitle(goodsSubTitle);
                attrNum = attrNum + 1;
            }

        }

        //商品所属分类
        if (StringUtils.isNotEmpty(classId) && !(goods_attr.getGoodsClass().getClassId().equals(classId))) {
            GoodsClass gc = gcService.findById(classId);
            if (gc != null) {
                if (gc.isGcShow()) {
                    goods_attr.setGoodsClass(gc);
                    attrNum = attrNum + 1;
                } else {
                    return new ReturnMessages(RequestState.ERROR, "分类不显示！", null);
                }
            } else {
                return new ReturnMessages(RequestState.ERROR, "分类不存在！", null);
            }
        }

        //商品所属品牌
        if (StringUtils.isNotEmpty(brandId) && !(goods_attr.getBrand().getBrandId().equals(brandId))) {
            Brand brand = brandService.findById(brandId);
            if (brand != null) {
                if (brand.getState().equals(StateConstant.BRAND_STATE_CHECK_ON.toString())) {//品牌审核通过才能添加商品
                    goods_attr.setBrand(brand);
                    attrNum = attrNum + 1;
                } else {
                    return new ReturnMessages(RequestState.ERROR, "品牌未通过审核！", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "品牌不存在！", null);
            }
        }

        //商品图片
        if (StringUtils.isNotEmpty(imgs)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {
            }.getType();
            List<Image> goodsPic = goods_attr.getGoodsPic();
            if(goodsPic != null){   //原来图片不为空
                String pics = gson.toJson(goodsPic);
                if(!imgs.equals(pics)){ //图片不同，则修改
                    try {
                        List<Image> imgList = gson.fromJson(imgs, type);
                        goods_attr.setGoodsPic(imgList);
                        goods_attr.setPics(imgs);
                        attrNum = attrNum + 1;
                    } catch (Exception e) {
                        return new ReturnMessages(RequestState.ERROR, "商品图片请求参数有误！", null);
                    }

                }
            }else{  //原来图片为空
                try {
                    List<Image> imgList = gson.fromJson(imgs, type);
                    goods_attr.setGoodsPic(imgList);
                    goods_attr.setPics(imgs);
                    attrNum = attrNum + 1;
                } catch (Exception e) {
                    return new ReturnMessages(RequestState.ERROR, "商品图片请求参数有误！", null);
                }

            }

        }

        //商品详情
        if (StringUtils.isNotEmpty(goodsBody) && !(goods_attr.getGoodsBody().equals(goodsBody))) {
            goods_attr.setGoodsBody(new StringBuffer(goodsBody));
            attrNum = attrNum + 1;
        }

        //商品关键字
        if (StringUtils.isNotEmpty(keywords)) {
            if(StringUtils.isNotEmpty(goods_attr.getKeywords())){   //原来关键字不为空
                if(!(goods_attr.getKeywords().equals(keywords))){   //新关键字与原来的不等
                    goods_attr.setKeywords(keywords);
                    attrNum = attrNum + 1;
                }else{
                    //do nothing
                }

            }else{  //原来关键字为空
                goods_attr.setKeywords(keywords);
                attrNum = attrNum + 1;

            }
        }

        //佣金
        if (commission != null) {
            if (commission < 0) {
                return new ReturnMessages(RequestState.ERROR, "佣金不能为负数！", null);
            } else {
                if (goods_attr.getCommission() != commission) {
                    goods_attr.setCommission(commission);
                    attrNum = attrNum + 1;
                }

            }
        }

        //是否显示
        if (StringUtils.isNotEmpty(goodsShow) && goods_attr.isGoodsShow() != Boolean.valueOf(goodsShow)) {
            goods_attr.setGoodsShow(Boolean.valueOf(goodsShow));
            attrNum = attrNum + 1;
        }

        BeanUtils.copyProperties(goods_attr, goods_update);

        //价格修改不需要审核，次日生效
        Map<String, Double> priceMap = new HashMap<String, Double>();
        priceMap = goods.getPrice();
        double mPrice = 0d;
        double cPrice = 0d;
        //商品市场价格
        if (marketPrice != null) {
            if (marketPrice < 0) {
                return new ReturnMessages(RequestState.ERROR, "市场价不能为负数！", null);
            } else {
                if (priceMap.containsKey(PriceConstant.GOODS_MARKET_PRICE.toString())) {
                    mPrice = priceMap.get(PriceConstant.GOODS_MARKET_PRICE.toString());
                    if (mPrice != marketPrice) {
                        priceNum = priceNum + 1;
                    }
                } else {  // 市场价为空
                    priceNum = priceNum + 1;
                }
            }
            priceMap.put(PriceConstant.GOODS_MARKET_PRICE.toString(), marketPrice);
        }

        //商品成本价格
        if (costPrice != null) {
            if (costPrice < 0) {
                return new ReturnMessages(RequestState.ERROR, "成本价不能为负数！", null);
            } else {

                if (priceMap.containsKey(PriceConstant.GOODS_COST_PRICE.toString())) {
                    cPrice = priceMap.get(PriceConstant.GOODS_COST_PRICE.toString());
                    if (cPrice != costPrice) {
                        priceNum = priceNum + 1;
                    }
                } else {  //成本价位空
                    priceNum = priceNum + 1;

                }
            }
            priceMap.put(PriceConstant.GOODS_COST_PRICE.toString(), costPrice);
        }

        goods_price.setPrice(priceMap);
        goods_update.setPrice(priceMap);

        //上下架时间不需要审核，立即生效
        //上架时间
        if (soldInTime != null) {
            if (goods.getSoldInTime() != soldInTime) {
                goods.setSoldInTime(soldInTime);
                goods_update.setSoldInTime(soldInTime);
                noCheckNum = noCheckNum + 1;
            }
        }
        //下架时间
        if (soldOutTime != null) {
            if (goods.getSoldOutTime() != soldOutTime) {
                goods.setSoldOutTime(soldOutTime);
                goods_update.setSoldOutTime(soldOutTime);
                noCheckNum = noCheckNum + 1;
            }
        }

        //库存修改不需要审核，立即生效
        //库存数量
        if (repositoryNum != null) {
            if (repositoryNum < 0) {
                return new ReturnMessages(RequestState.ERROR, "商品库存不能为负数！", null);
            } else {
                if (stock.getStockNum() != repositoryNum) {
                    stock.setStockNum(repositoryNum);
                    stockNum = stockNum + 1;
                }
            }
        }

        //库存单位
        if (StringUtils.isNotEmpty(sku) && !(stock.getSku().equals(sku))) {
            stock.setSku(sku);
            stockNum = stockNum + 1;
        }

        //如果商品是审核未通过或待审核，则更改商品状态为待审核，不做保存及时更新商品
        String goodsState = goods_update.getState();
        if(StringUtils.isNotEmpty(goodsState) && (goodsState.equals(StateConstant.GOODS_STATE_CHECK_OFF.toString()) || goodsState.equals(StateConstant.GOODS_STATE_ON_CHECKING.toString()))){
            goods_update.setState(StateConstant.GOODS_STATE_ON_CHECKING.toString());
            if(stockNum > 0){   //库存有修改
                Goods updateGoods = goodsService.update(goods_update, stock);
                if(updateGoods != null){
                    return new ReturnMessages(RequestState.SUCCESS, "编辑成功！", updateGoods);
                }else{
                    return new ReturnMessages(RequestState.ERROR, "编辑失败！", null);
                }

            }else{  //库存无修改
                Goods updateGoods = goodsService.updateGoods(goods_update);
                if(updateGoods != null){
                    return new ReturnMessages(RequestState.SUCCESS, "编辑成功！", updateGoods);
                }else{
                    return new ReturnMessages(RequestState.ERROR, "编辑失败！", null);
                }
            }

        }

        Map<String, Object> objRes = new HashMap<String, Object>();
        //不需审核的属性(上下架时间，库存)修改
        String noCheckAttrMsg = "";
        Goods noCheckAttrGoods = new Goods();
        if (noCheckNum > 0 || stockNum > 0) {
            noCheckAttrGoods = goodsService.update(goods, stock);
            if (noCheckAttrGoods != null) {
                noCheckAttrMsg = "编辑成功。";
            } else {
                noCheckAttrMsg = "编辑失败。";
            }
        } else {
            noCheckAttrMsg = null;
        }
        objRes.put("noCheckAttrGoods", noCheckAttrGoods);


        //需审核的属性若有修改，先查询属性商品表中是否存在未生效的（即未删除的），若存在未生效的属性编辑，则无法修改；若不存在，则可以编辑
        String checkAttrMsg = "";
        ModifyAttrGoods modifyAttrGoods = null;
        if (attrNum > 0) {//属性有修改
            modifyAttrGoods = new ModifyAttrGoods();
            modifyAttrGoods.setGoods(goods_attr);
            modifyAttrGoods.setGoodsId(goodsId);
            modifyAttrGoods.setMember(member);
            modifyAttrGoods.setCreateTime(System.currentTimeMillis());
            modifyAttrGoods.setState(StateConstant.MODIFY_GOODS_STATE_ON_CHECKING.toString());
            if (StringUtils.isNotEmpty(reason)) {
                modifyAttrGoods.setReason(reason);
            }
            ModifyAttrGoods modifyAttrGoodsSearch = modifyAttrGoodsService.findByGoodsId(goodsId);
            if (modifyAttrGoodsSearch != null) {//存在表明上次的编辑还未审核
                checkAttrMsg = "上次的属性编辑尚未审核,暂时不能编辑!";
            } else {
                //保存编辑属性
                ModifyAttrGoods modifyAttrGoodsRes = modifyAttrGoodsService.save(modifyAttrGoods);
                if (modifyAttrGoodsRes != null) {
                    checkAttrMsg = "编辑成功，该操作将在审核后执行。";
                } else {
                    checkAttrMsg = "编辑失败。";
                }
            }
        } else {
            checkAttrMsg = null;
        }
        objRes.put("modifyAttrGoods", modifyAttrGoods);

        //价格若有编辑，查询上次的价格修改是否生效，若无未生效的价格修改则可以编辑价格，价格编辑定时生效，
        String priceMsg = "";
        ModifyPriceGoods modifyPriceGoods = null;
        if (priceNum > 0) {   //价格有编辑
            modifyPriceGoods = new ModifyPriceGoods();
            modifyPriceGoods.setGoods(goods_price);
            modifyPriceGoods.setGoodsId(goodsId);
            modifyPriceGoods.setMember(member);
            modifyPriceGoods.setCreateTime(System.currentTimeMillis());
            modifyPriceGoods.setState(StateConstant.MODIFY_GOODS_STATE_ON_CHECKING.toString());
            if (StringUtils.isNotEmpty(reason)) {
                modifyPriceGoods.setReason(reason);
            }
            ModifyPriceGoods modifyPriceGoodsSearch = modifyPriceGoodsService.findByGoodsId(goodsId);
            if (modifyPriceGoodsSearch != null) {   //存在表明上次的价格编辑还未生效
                priceMsg = "上次价格编辑尚未生效,该操作请在生效后执行。";
            } else {
                //保存价格编辑
                ModifyPriceGoods modifyPriceGoodsRes = modifyPriceGoodsService.save(modifyPriceGoods);
                if (modifyPriceGoodsRes != null) {
                    priceMsg = "编辑成功，结果将在次日生效。";
                } else {
                    priceMsg = "编辑失败。";
                }
            }

        } else {    //价格无编辑项
            priceMsg = null;
        }
        objRes.put("modifyPriceGoods", modifyAttrGoods);

//        rm.setMessages("无需审核的属性编辑：" + noCheckAttrMsg + ";需审核的属性编辑：" + checkAttrMsg + ";价格编辑：" + priceMsg);
        if(StringUtils.isNotEmpty(priceMsg)){
            rm.setMessages(priceMsg);
        }else if(StringUtils.isNotEmpty(checkAttrMsg)){
            rm.setMessages(checkAttrMsg);
        }else if (StringUtils.isNotEmpty(noCheckAttrMsg)){
            rm.setMessages(noCheckAttrMsg);
        }else{
            rm.setMessages("当次操作无效。");
        }
        rm.setContent(objRes);
        rm.setState(RequestState.SUCCESS);
        return rm;

    }

    /**
     * 审核修改的商品
     * (审核过后，属性商品和价格商品的删除状态变为已删除)
     * @param modifyGoodsId 修改商品id
     * @param result        审核结果["success",任意值表示失败]
     * @return
     */
    @RequestMapping("/checkEdit")
    public ReturnMessages checkEditGoods(@RequestParam(name = "modifyGoodsId", required = true, defaultValue = "") String modifyGoodsId,
                                         @RequestParam(name = "result", required = true, defaultValue = "") String result,//审核结果,默认审核失败
                                         HttpServletRequest request
    ) {
        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限审核！", null);
        }
        Member member = memberService.findMemberByUsername(userName);
        if(member == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限审核！", null);

        }
        List<Role> roleList = roleService.findRoleByMember(member);
        String roleCodeStr = "";
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }

        }
        if (!roleCodeStr.contains("ROLE:ADMIN")) { //不是管理员没有权限审核
            return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
        }

        if (StringUtils.isNotEmpty(modifyGoodsId) && StringUtils.isNotEmpty(result)) {
            boolean res = goodsService.checkEditGoods(Long.valueOf(modifyGoodsId), result);
            if (res) {
                return new ReturnMessages(RequestState.SUCCESS, "审核修改成功！", null);
            } else {
                return new ReturnMessages(RequestState.ERROR, "审核失败！", null);
            }

        } else {
            return new ReturnMessages(RequestState.ERROR, "参数有误！", null);
        }

    }

    /**
     * 审核发布的商品
     * @param goodsId 待审核商品id
     * @param result  审核结果["success","failure"]
     * @return
     */
    @RequestMapping("/checkAdd")
    public ReturnMessages checkAddGoods(@RequestParam(name = "goodsId", required = true, defaultValue = "") String goodsId,
                                        @RequestParam(name = "result", required = true, defaultValue = "") String result,//审核结果,默认审核失败
                                        HttpServletRequest request
    ) {

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限审核！", null);
        }
        Member member = memberService.findMemberByUsername(userName);
        if(member == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限审核！", null);

        }
        List<Role> roleList = roleService.findRoleByMember(member);
        String roleCodeStr = "";
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }

        }
        if (!roleCodeStr.contains("ROLE:ADMIN")) { //不是管理员没有权限审核
            return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
        }
        if (StringUtils.isNotEmpty(goodsId) && StringUtils.isNotEmpty(result)) {
            Goods goods = goodsService.findByGoodsId(goodsId);
            if (goods != null) {
                if (result.equals("success")) {
                    goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                } else if (result.equals("failure")) {
                    goods.setState(StateConstant.GOODS_STATE_CHECK_OFF.toString());
                } else {
                    return new ReturnMessages(RequestState.ERROR, "结果参数有误！", null);
                }
                goods.setUpdateTime(System.currentTimeMillis());
                Goods goodsRes = goodsService.updateGoods(goods);
                if (goodsRes != null) {
                    return new ReturnMessages(RequestState.SUCCESS, "审核成功！", null);

                } else {
                    return new ReturnMessages(RequestState.ERROR, "审核失败！", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "商品不存在!", null);
            }

        } else {
            return new ReturnMessages(RequestState.ERROR, "参数有误！", null);
        }

    }

    /**
     * 商品详情（根据id搜索商品）
     *
     * @param goodsId 商品id
     * @return
     */
    @RequestMapping("/findById")
    public ReturnMessages findGoodsById(@RequestParam(name = "goodsId", required = true) String goodsId) {
        ReturnMessages rm = null;
        Goods goods = goodsService.findByGoodsId(goodsId);
        long time = System.currentTimeMillis();
        if (goods != null) {
            Store store = goods.getStore();
            GoodsClass gc = goods.getGoodsClass();
            Brand brand = goods.getBrand();
            GoodsStock gs = goods.getGoodsStock();
            // (分类不为null && 分类未删除 && 分类显示) && (库存不为null && 库存大于0)
            //分类的显示状态与商品的显示无关修改
            /*if (gc != null && !(gc.isDel()) && (gc.isGcShow())
                    && gs != null && gs.getStockNum() > 0) {
            } else {
                goods = null;
            }*/
        }
        if (goods != null) {

            rm = new ReturnMessages(RequestState.SUCCESS, "查询成功！", goods);
        } else {
            rm = new ReturnMessages(RequestState.ERROR, "商品不存在!", null);
        }

        return rm;
    }

    /**
     * 逻辑删除商品
     *
     * @param goodsId 商品id
     * @return
     */
    @RequestMapping("/deleteByGoodsId")
    public ReturnMessages deleteByGoodsId(@RequestParam(name = "goodsId", required = true) String goodsId,
                                          HttpServletRequest request
    ) {
        Goods goodsSearch = null;
        if (StringUtils.isNotEmpty(goodsId)) {
            goodsSearch = goodsService.findByGoodsId(goodsId);
        } else {
            return new ReturnMessages(RequestState.ERROR, "参数有误！", null);
        }
        if (goodsSearch == null) {
            return new ReturnMessages(RequestState.ERROR, "商品不存在!", null);

        } else {
            //获取当前用户
            String userName = SecurityUtils.getUsername(request);
            if(!StringUtils.isNotEmpty(userName)){   //用户未登录
                return new ReturnMessages(RequestState.ERROR, "未登录，没有权限删除！", null);
            }
            Member member = memberService.findMemberByUsername(userName);
            if(member == null){ //用户不存在
                return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限删除！", null);

            }
            List<Role> roleList = roleService.findRoleByMember(member);
            String roleCodeStr = "";
            if (roleList != null && roleList.size() > 0) {
                for (Role role : roleList) {
                    String roleCode = role.getRoleCode();
                    roleCodeStr = roleCodeStr + roleCode + ",";
                }
            }
            Store userStore = storeService.findByMember(member);
            Store goodsStore = goodsSearch.getStore();
            if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员,只能删除默认店铺的商品
                Store defaultStore = storeService.findStoreByTheDefaultTrue();
                if (goodsStore != null && defaultStore != null && goodsStore.getStoreName().equals(defaultStore.getStoreName())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "没有权限删除商家的商品！", null);
                }

            } else if (roleCodeStr.contains("ROLE:SELLER")) {  //是商家,只能删除自己店铺的商品
                if (userStore == null) {
                    return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                } else {
                    String goodsStoreId = "";
                    String userStoreId = userStore.getStoreId();
                    if (goodsStore != null) {
                        goodsStoreId = goodsStore.getStoreId();
                        if (!goodsStoreId.equals(userStoreId)) {
                            return new ReturnMessages(RequestState.ERROR, "商家只能删除自己店铺的商品！", null);
                        } else {

                        }
                    }
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
            }

            if (goodsService.deleteByGoodsId(goodsId)) {
                return new ReturnMessages(RequestState.SUCCESS, "删除成功!", null);
            }
            return new ReturnMessages(RequestState.ERROR, "删除失败!", null);
        }

    }

    /**
     * 物理删除商品
     *
     * @param goodsId 商品id
     * @return
     */
    @RequestMapping("/physicalDelete")
    public ReturnMessages physicalDelete(@RequestParam(name = "goodsId", required = true) String goodsId,
                                         HttpServletRequest request
    ) {

        Goods goodsSearch = null;
        if (StringUtils.isNotEmpty(goodsId)) {
            goodsSearch = goodsService.findByGoodsId(goodsId);
        } else {
            return new ReturnMessages(RequestState.ERROR, "参数有误！", null);
        }
        if (goodsSearch == null) {
            return new ReturnMessages(RequestState.ERROR, "商品不存在!", null);

        } else {
            //获取当前用户
            String userName = SecurityUtils.getUsername(request);
            if(!StringUtils.isNotEmpty(userName)){   //用户未登录
                return new ReturnMessages(RequestState.ERROR, "未登录，没有权限删除！", null);
            }
            Member member = memberService.findMemberByUsername(userName);
            if(member == null){ //用户不存在
                return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限删除！", null);

            }
            List<Role> roleList = roleService.findRoleByMember(member);
            String roleCodeStr = "";
            if (roleList != null && roleList.size() > 0) {
                for (Role role : roleList) {
                    String roleCode = role.getRoleCode();
                    roleCodeStr = roleCodeStr + roleCode + ",";
                }
            }
            Store userStore = storeService.findByMember(member);
            Store goodsStore = goodsSearch.getStore();
            if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员,只能删除默认店铺的商品
                Store defaultStore = storeService.findStoreByTheDefaultTrue();
                if (goodsStore != null && defaultStore != null && goodsStore.getStoreName().equals(defaultStore.getStoreName())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "没有权限删除商家的商品！", null);
                }

            } else if (roleCodeStr.contains("ROLE:SELLER")) {  //是商家,只能删除自己店铺的商品
                if (userStore == null) {
                    return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                } else {
                    String goodsStoreId = "";
                    String userStoreId = userStore.getStoreId();
                    if (goodsStore != null) {
                        goodsStoreId = goodsStore.getStoreId();
                        if (!goodsStoreId.equals(userStoreId)) {
                            return new ReturnMessages(RequestState.ERROR, "商家只能删除自己店铺的商品！", null);
                        } else {

                        }
                    }
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
            }

            if (goodsService.deleteGoods(goodsId)) {
                return new ReturnMessages(RequestState.SUCCESS, "删除成功!", null);
            }
            return new ReturnMessages(RequestState.ERROR, "删除失败!", null);
        }
    }

    //***前端商品，商家也可以查看
    /**
     * 条件分页查询商品
     *
     * @param goodsId       商品id[可空]
     * @param goodsTitle    商品标题[可空]
     * @param goodsSubTitle 商品副标题[可空]
     * @param stockId       商品库存id[可空]
     * @param storeId       商品所属店铺id[可空]
     * @param classId       商品所属分类id[可空]
     * @param brandId       商品所属品牌id[可空]
     * @param keywords      关键字[可空]
     * @param commission    商品提取佣金[可空]
     * @param goodsShow     商品显示状态[可空][0表示不显示，1表示显示，2包括前两者][默认2]
     * @param state         商品审核状态[可空]["GOODS_STATE_ON_CHECKING","GOODS_STATE_CHECK_ON","GOODS_STATE_CHECK_OFF"]
     * @param startTime     查询起始时间[可空][搜索商品创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索商品创建时间所在区间]
     * @param del           删除状态[可空]["true","false"]
     * @param inSoldTime    是否在上下架时间范围[可空]["true","false"]
     * @param repositoryNum 库存数量[可空]
     * @param numCondition  库存数量条件[可空]["gt","gtAndEq","eq","lt","ltAndEq",其他任意值][默认值"doNothing"，不能单独使用，需与库存数量同时使用]
     * @param reqFrom       查询请求来源[可空]["front"表示前端商品查询：商品查询默认未删除，显示，审核通过，在上下架区间范围内,库存大于0]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findGoods")
    public ReturnMessages findFrontGoodsByConditon(
            @RequestParam(name = "goodsId", required = false) String goodsId,
            @RequestParam(name = "goodsTitle", required = false) String goodsTitle,
            @RequestParam(name = "goodsSubTitle", required = false) String goodsSubTitle,
            @RequestParam(name = "stockId", required = false) String stockId,
            @RequestParam(name = "storeId", required = false) String storeId,
            @RequestParam(name = "classId", required = false) String classId,
            @RequestParam(name = "brandId", required = false) String brandId,
            @RequestParam(name = "keywords", required = false) String keywords,
            @RequestParam(name = "commission", required = false) String commission,
            @RequestParam(name = "goodsShow", required = false) String goodsShow,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            @RequestParam(name = "del", required = false) String del,
            @RequestParam(name = "inSoldTime", required = false) String inSoldTime,
            @RequestParam(name = "repositoryNum", required = false) String repositoryNum,
            @RequestParam(name = "numCondition", required = false) String numCondition,
            @RequestParam(name = "reqFrom", required = false) String reqFrom,
            @RequestParam(name = "pageNum", required = false) String pageNum,
            @RequestParam(name = "pageSize", required = false) String pageSize,
            @RequestParam(name = "pageSort", required = false) String pageSort,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "forMe", required = false, defaultValue = "false") boolean forMe,
            HttpServletRequest request
    ) {
        ReturnMessages rm = new ReturnMessages();
        Goods goods = new Goods();


        if (StringUtils.isNotEmpty(goodsId)) {
            goods.setGoodsId(goodsId);
        }

        if (StringUtils.isNotEmpty(goodsTitle)) {
            goods.setGoodsTitle(goodsTitle);
        }

        if (StringUtils.isNotEmpty(goodsSubTitle)) {
            goods.setGoodsSubTitle(goodsSubTitle);
        }

        if (StringUtils.isNotEmpty(stockId)) {
            GoodsStock stock = stockService.findByStockId(stockId);
            if (stock != null) {
                goods.setGoodsStock(stock);
            } else {
                return new ReturnMessages(RequestState.ERROR, "库存不存在!", null);
            }
        }

        if (StringUtils.isNotEmpty(keywords)) {
            goods.setKeywords(keywords);
        }

        if (StringUtils.isNotEmpty(commission)) {
            try {
                goods.setCommission(Double.valueOf(commission));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "佣金参数格式有误!", null);
            }
        }

        //商品显示状态0不显示；1显示；2全部
        String goodsShowStr = "2";
        if (StringUtils.isNotEmpty(goodsShow)) {
            if (goodsShow.equals("0") || goodsShow.equals("1") || goodsShow.equals("2")) {
                goodsShowStr = goodsShow.toString();
            } else {
                return new ReturnMessages(RequestState.ERROR, "显示参数格式有误!", null);
            }

        }

        if (StringUtils.isNotEmpty(state)) {
            if ((state.equals(StateConstant.GOODS_STATE_CHECK_ON.toString()) || state.equals(StateConstant.GOODS_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.GOODS_STATE_CHECK_OFF.toString()))) {
                goods.setState(state);
            } else {
                return new ReturnMessages(RequestState.ERROR, "审核状态参数有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(del)) {
            if (del.equals("true") || del.equals("false")) {
                goods.setDel(Boolean.valueOf(del));

            } else {
                return new ReturnMessages(RequestState.ERROR, "删除状态参数有误!", null);
            }
        }

        //设置是否在上下架区间范围
        String inSoldTimeStr = "false";
        if (StringUtils.isNotEmpty(inSoldTime)) {
            if (inSoldTime.equals("true") || inSoldTime.equals("false")) {
                inSoldTimeStr = inSoldTime;
            } else {
                return new ReturnMessages(RequestState.ERROR, "inSoldTime参数有误!", null);
            }
        }

        //设置库存数量
        long stockNum = -1l;
        if (StringUtils.isNotEmpty(repositoryNum)) {

            try {
                stockNum = Long.valueOf(repositoryNum);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "库存数量参数格式有误！", null);
            }
        }
        //设置库存数据条件
        if (StringUtils.isNotEmpty(numCondition) && (numCondition.equals("gt") || numCondition.equals("gtAndEq") || numCondition.equals("eq") || numCondition.equals("lt") || numCondition.equals("ltAndEq"))) {

        } else {
            numCondition = "doNothing";
        }

        //前端请求，商品查询默认未删除，显示，审核通过，在上下架区间范围内
        if (StringUtils.isNotEmpty(reqFrom)) {
            if (reqFrom.equals("front")) {
                goods.setDel(Boolean.FALSE);
                goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                inSoldTimeStr = "true"; //在上架区间范围内
                //库存大于0
                stockNum = 0l;  //库存数量
                numCondition = "gt";    //库存数量条件
            }

        }

        Map<String, Object> mapParam = new HashMap<String, Object>();

        //设置创建时间条件查询
        long sTime = 0l;
        if (StringUtils.isNotEmpty(startTime)) {
            try {
                sTime = Long.valueOf(startTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "起始时间参数格式有误!", null);
            }
        }
        mapParam.put("startTime", sTime);
        long eTime = 0l;
        if (StringUtils.isNotEmpty(endTime)) {
            try {
                eTime = Long.valueOf(endTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "结束时间参数格式有误!", null);
            }
        }
        mapParam.put("endTime", eTime);

        //设置商品所属店铺
        Store store = null;
        if (StringUtils.isNotEmpty(storeId)) {
            store = storeService.findByStoreId(storeId);
            if (store != null) {
                if (store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "店铺不在正常运营状态!", null);

                }
            } else {
                return new ReturnMessages(RequestState.ERROR, "店铺不存在!", null);
            }
        }
        mapParam.put("store", store);

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //用户已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在,获取角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员
                    //do nothing
                } else if (roleCodeStr.contains("ROLE:SELLER")) {   //是商家,也可以看到前端的商品
                    /*Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //重新设置店铺
                        mapParam.put("store", userStore);
                    }*/
                    goods.setDel(Boolean.FALSE);
                    goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                    goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                    inSoldTimeStr = "true"; //在上架区间范围内
                    stockNum = 0l;  //库存数量
                    numCondition = "gt";    //库存数量条件

                } else {  //买家<相当于前端请求，商品查询默认未删除，显示，审核通过，在上下架区间范围内，库存大于0>
                    goods.setDel(Boolean.FALSE);
                    goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                    goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                    inSoldTimeStr = "true"; //在上架区间范围内
                    stockNum = 0l;  //库存数量
                    numCondition = "gt";    //库存数量条件
                }

            }else{  //用户不存在等同于没登录，可以查看前端商品
                goods.setDel(Boolean.FALSE);
                goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                inSoldTimeStr = "true"; //在上架区间范围内
                stockNum = 0l;  //库存数量
                numCondition = "gt";    //库存数量条件
            }

        }else{  //用户没登录，能看前端商品
            goods.setDel(Boolean.FALSE);
            goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
            goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
            inSoldTimeStr = "true"; //在上架区间范围内
            stockNum = 0l;  //库存数量
            numCondition = "gt";    //库存数量条件
        }

        mapParam.put("goodsShowStr", goodsShowStr);
        mapParam.put("inSoldTime", inSoldTimeStr);
        mapParam.put("stockNum", stockNum);
        mapParam.put("numCondition", numCondition);
        //设置商品
        mapParam.put("goods", goods);
        //设置商品分类
        List<GoodsClass> gcList = new ArrayList<GoodsClass>();
        if (StringUtils.isNotEmpty(classId)) {
            GoodsClass gc_parent = gcService.findById(classId);
            if (gc_parent != null) {
                if (gc_parent.isGcShow()) {
                    gcList.add(gc_parent);
                    List<GoodsClass> gc_child = gcService.findGcChildren(gc_parent);
                    if (gc_child != null && gc_child.size() > 0) {
                        for (GoodsClass gc : gc_child) {
                            if (gc != null && gc.isGcShow()) { //添加显示的子类
                                gcList.add(gc);
                            }
                        }
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "分类不显示!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "分类不存在!", null);
            }
        }
        mapParam.put("gcList", gcList);

        //设置品牌
        Brand brand = null;
        if (StringUtils.isNotEmpty(brandId)) {
            brand = brandService.findById(brandId);
            if (brand != null) {
                if (brand.getState().equals(StateConstant.BRAND_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "品牌不在正常使用状态!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "品牌不存在!", null);
            }
        }
        mapParam.put("brand", brand);

        //设置查询分页
        UtilPage page = new UtilPage(0, 5, "createTime", Sort.Direction.DESC);
        if (StringUtils.isNotEmpty(pageNum)) {
            try {
                page.setPageNum(Integer.valueOf(pageNum));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页页数格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSize)) {
            try {
                page.setPageSize(Integer.valueOf(pageSize));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页大小格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSort)) {
            page.setPageSort(pageSort);
        }

        if (StringUtils.isNotEmpty(sortDirection)) {
            if (sortDirection.equals("asc")) {
                page.setDirection(Sort.Direction.ASC);
            } else if (sortDirection.equals("desc")) {
                page.setDirection(Sort.Direction.DESC);
            } else {
                return new ReturnMessages(RequestState.ERROR, "分页排序参数有误!", null);
            }

        }

        mapParam.put("page", page);

        Page<Goods> goodsPage = goodsService.findByCondition(mapParam);
        if (goodsPage != null && goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            rm.setMessages("查询成功!");
        } else {
            rm.setMessages("暂无数据!");
        }
        rm.setContent(goodsPage);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }

    //***前端商品，商家也可以查看
    /**
     * 条件分页查询简单商品
     *
     * @param goodsId       商品id[可空]
     * @param goodsTitle    商品标题[可空]
     * @param goodsSubTitle 商品副标题[可空]
     * @param stockId       商品库存id[可空]
     * @param storeId       商品所属店铺id[可空]
     * @param classId       商品所属分类id[可空]
     * @param brandId       商品所属品牌id[可空]
     * @param keywords      关键字[可空]
     * @param commission    商品提取佣金[可空]
     * @param goodsShow     商品显示状态[可空][0表示不显示，1表示显示，2包括前两者][默认2]
     * @param state         商品审核状态[可空]["GOODS_STATE_ON_CHECKING","GOODS_STATE_CHECK_ON","GOODS_STATE_CHECK_OFF"]
     * @param startTime     查询起始时间[可空][搜索商品创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索商品创建时间所在区间]
     * @param del           删除状态[可空]["true","false"]
     * @param inSoldTime    是否在上下架时间范围[可空]["true","false"]
     * @param repositoryNum 库存数量[可空]
     * @param numCondition  库存数量条件[可空]["gt","gtAndEq","eq","lt","ltAndEq",其他任意值][默认值"doNothing"，不能单独使用，需与库存数量同时使用]
     * @param reqFrom       查询请求来源[可空]["front"表示前端商品查询：商品查询默认未删除，显示，审核通过，在上下架区间范围内,库存大于0]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findSimpleGoods")
    public ReturnMessages findFrontSimpleGoods(@RequestParam(name = "goodsId", required = false) String goodsId,
                                          @RequestParam(name = "goodsTitle", required = false) String goodsTitle,
                                          @RequestParam(name = "goodsSubTitle", required = false) String goodsSubTitle,
                                          @RequestParam(name = "stockId", required = false) String stockId,
                                          @RequestParam(name = "storeId", required = false) String storeId,
                                          @RequestParam(name = "classId", required = false) String classId,
                                          @RequestParam(name = "brandId", required = false) String brandId,
                                          @RequestParam(name = "keywords", required = false) String keywords,
                                          @RequestParam(name = "commission", required = false) String commission,
                                          @RequestParam(name = "goodsShow", required = false) String goodsShow,
                                          @RequestParam(name = "state", required = false) String state,
                                          @RequestParam(name = "startTime", required = false) String startTime,
                                          @RequestParam(name = "endTime", required = false) String endTime,
                                          @RequestParam(name = "del", required = false) String del,
                                          @RequestParam(name = "inSoldTime", required = false) String inSoldTime,
                                          @RequestParam(name = "repositoryNum", required = false) String repositoryNum,
                                          @RequestParam(name = "numCondition", required = false) String numCondition,
                                          @RequestParam(name = "reqFrom", required = false) String reqFrom,
                                          @RequestParam(name = "pageNum", required = false) String pageNum,
                                          @RequestParam(name = "pageSize", required = false) String pageSize,
                                          @RequestParam(name = "pageSort", required = false) String pageSort,
                                          @RequestParam(name = "sortDirection", required = false) String sortDirection,
                                          HttpServletRequest request
    ) {

        ReturnMessages rm = new ReturnMessages();
        Goods goods = new Goods();
        if (StringUtils.isNotEmpty(goodsId)) {
            goods.setGoodsId(goodsId);
        }

        if (StringUtils.isNotEmpty(goodsTitle)) {
            goods.setGoodsTitle(goodsTitle);
        }

        if (StringUtils.isNotEmpty(goodsSubTitle)) {
            goods.setGoodsSubTitle(goodsSubTitle);
        }

        if (StringUtils.isNotEmpty(stockId)) {
            GoodsStock stock = stockService.findByStockId(stockId);
            if (stock != null) {
                goods.setGoodsStock(stock);
            } else {
                return new ReturnMessages(RequestState.ERROR, "库存不存在!", null);
            }
        }

        if (StringUtils.isNotEmpty(keywords)) {
            goods.setKeywords(keywords);
        }

        if (StringUtils.isNotEmpty(commission)) {
            try {
                goods.setCommission(Double.valueOf(commission));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "佣金参数格式有误!", null);
            }
        }

        //商品显示状态0不显示；1显示；2全部
        String goodsShowStr = "2";
        if (StringUtils.isNotEmpty(goodsShow)) {
            if (goodsShow.equals("0") || goodsShow.equals("1") || goodsShow.equals("2")) {
                goodsShowStr = goodsShow.toString();
            } else {
                return new ReturnMessages(RequestState.ERROR, "显示参数格式有误!", null);
            }

        }

        if (StringUtils.isNotEmpty(state)) {
            if ((state.equals(StateConstant.GOODS_STATE_CHECK_ON.toString()) || state.equals(StateConstant.GOODS_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.GOODS_STATE_CHECK_OFF.toString()))) {
                goods.setState(state);
            } else {
                return new ReturnMessages(RequestState.ERROR, "审核状态参数有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(del)) {
            if (del.equals("true") || del.equals("false")) {
                goods.setDel(Boolean.valueOf(del));

            } else {
                return new ReturnMessages(RequestState.ERROR, "删除状态参数有误!", null);
            }
        }

        //设置是否在上下架区间范围
        String inSoldTimeStr = "false";
        if (StringUtils.isNotEmpty(inSoldTime)) {
            if (inSoldTime.equals("true") || inSoldTime.equals("false")) {
                inSoldTimeStr = inSoldTime;
            } else {
                return new ReturnMessages(RequestState.ERROR, "inSoldTime参数有误!", null);
            }
        }

        //设置库存数量
        long stockNum = -1l;
        if (StringUtils.isNotEmpty(repositoryNum)) {

            try {
                stockNum = Long.valueOf(repositoryNum);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "库存数量参数格式有误！", null);
            }
        }
        //设置库存数据条件
        if (StringUtils.isNotEmpty(numCondition) && (numCondition.equals("gt") || numCondition.equals("gtAndEq") || numCondition.equals("eq") || numCondition.equals("lt") || numCondition.equals("ltAndEq"))) {

        } else {
            numCondition = "doNothing";
        }

        //前端请求，商品查询默认未删除，显示，审核通过，在上下架区间范围内
        if (StringUtils.isNotEmpty(reqFrom)) {
            if (reqFrom.equals("front")) {
                goods.setDel(Boolean.FALSE);
                goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                inSoldTimeStr = "true"; //在上架区间范围内
                stockNum = 0l;  //库存数量
                numCondition = "gt";    //库存数量条件
            }

        }

        Map<String, Object> mapParam = new HashMap<String, Object>();

        //设置创建时间条件查询
        long sTime = 0l;
        if (StringUtils.isNotEmpty(startTime)) {
            try {
                sTime = Long.valueOf(startTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "起始时间参数格式有误!", null);
            }
        }
        mapParam.put("startTime", sTime);
        long eTime = 0l;
        if (StringUtils.isNotEmpty(endTime)) {
            try {
                eTime = Long.valueOf(endTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "结束时间参数格式有误!", null);
            }
        }
        mapParam.put("endTime", eTime);

        //设置商品所属店铺
        Store store = null;
        if (StringUtils.isNotEmpty(storeId)) {
            store = storeService.findByStoreId(storeId);
            if (store != null) {
                if (store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "店铺不在正常运营状态!", null);

                }
            } else {
                return new ReturnMessages(RequestState.ERROR, "店铺不存在!", null);
            }
        }
        mapParam.put("store", store);

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //用户已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在,获取角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员
                    //do nothing
                } else if (roleCodeStr.contains("ROLE:SELLER")) {   //是商家,也可以看到前端的商品
                    /*Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //重新设置店铺
                        mapParam.put("store", userStore);
                    }*/
                    goods.setDel(Boolean.FALSE);
                    goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                    goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                    inSoldTimeStr = "true"; //在上架区间范围内
                    stockNum = 0l;  //库存数量
                    numCondition = "gt";    //库存数量条件

                } else {  //买家<相当于前端请求，商品查询默认未删除，显示，审核通过，在上下架区间范围内，库存大于0>
                    goods.setDel(Boolean.FALSE);
                    goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                    goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                    inSoldTimeStr = "true"; //在上架区间范围内
                    stockNum = 0l;  //库存数量
                    numCondition = "gt";    //库存数量条件
                }

            }else{  //用户不存在等同于没登录，可以查看前端商品
                goods.setDel(Boolean.FALSE);
                goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                inSoldTimeStr = "true"; //在上架区间范围内
                stockNum = 0l;  //库存数量
                numCondition = "gt";    //库存数量条件
            }

        }else{  //用户没登录，能看前端商品
            goods.setDel(Boolean.FALSE);
            goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
            goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
            inSoldTimeStr = "true"; //在上架区间范围内
            stockNum = 0l;  //库存数量
            numCondition = "gt";    //库存数量条件
        }
        mapParam.put("goodsShowStr", goodsShowStr);
        mapParam.put("inSoldTime", inSoldTimeStr);
        mapParam.put("stockNum", stockNum);
        mapParam.put("numCondition", numCondition);
        mapParam.put("goods", goods);

        //设置商品分类
        List<GoodsClass> gcList = new ArrayList<GoodsClass>();
        if (StringUtils.isNotEmpty(classId)) {
            GoodsClass gc_parent = gcService.findById(classId);
            if (gc_parent != null) {
                if (gc_parent.isGcShow()) {
                    gcList.add(gc_parent);
                    List<GoodsClass> gc_child = gcService.findGcChildren(gc_parent);
                    if (gc_child != null && gc_child.size() > 0) {
                        for (GoodsClass gc : gc_child) {
                            if (gc != null && gc.isGcShow()) { //添加显示的子类
                                gcList.add(gc);
                            }
                        }
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "分类不显示!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "分类不存在!", null);
            }
        }
        mapParam.put("gcList", gcList);

        //设置品牌
        Brand brand = null;
        if (StringUtils.isNotEmpty(brandId)) {
            brand = brandService.findById(brandId);
            if (brand != null) {
                if (brand.getState().equals(StateConstant.BRAND_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "品牌不在正常使用状态!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "品牌不存在!", null);
            }
        }
        mapParam.put("brand", brand);

        //设置查询分页
        UtilPage page = new UtilPage(0, 5, "createTime", Sort.Direction.DESC);
        if (StringUtils.isNotEmpty(pageNum)) {
            try {
                page.setPageNum(Integer.valueOf(pageNum));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页页数格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSize)) {
            try {
                page.setPageSize(Integer.valueOf(pageSize));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页大小格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSort)) {
            page.setPageSort(pageSort);
        }

        if (StringUtils.isNotEmpty(sortDirection)) {
            if (sortDirection.equals("asc")) {
                page.setDirection(Sort.Direction.ASC);
            } else if (sortDirection.equals("desc")) {
                page.setDirection(Sort.Direction.DESC);
            } else {
                return new ReturnMessages(RequestState.ERROR, "分页排序参数有误!", null);
            }

        }

        mapParam.put("page", page);

        Page<Goods> goodsList = goodsService.findSimpleGoodsByCondition(mapParam);
        if (goodsList.getContent() != null && goodsList.getContent().size() > 0) {
            rm.setMessages("查询成功!");
        } else {
            rm.setMessages("暂无数据!");
        }

        rm.setContent(goodsList);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }

    //***添加后台查询方法(前后端方法分离)（商家登录前端可以查看前端商品，商家登录后台只能查看自己的商品，后台不允许用户进入）
    //***商品后台查询，根据店铺id查询改为根据店铺名称查询
    /**
     * 条件分页查询商品
     *
     * @param goodsId       商品id[可空]
     * @param goodsTitle    商品标题[可空]
     * @param goodsSubTitle 商品副标题[可空]
     * @param stockId       商品库存id[可空]
     * @param storeName     商品所属店铺名称[可空]
     * @param classId       商品所属分类id[可空]
     * @param brandId       商品所属品牌id[可空]
     * @param keywords      关键字[可空]
     * @param commission    商品提取佣金[可空]
     * @param goodsShow     商品显示状态[可空][0表示不显示，1表示显示，2包括前两者][默认2]
     * @param state         商品审核状态[可空]["GOODS_STATE_ON_CHECKING","GOODS_STATE_CHECK_ON","GOODS_STATE_CHECK_OFF"]
     * @param startTime     查询起始时间[可空][搜索商品创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索商品创建时间所在区间]
     * @param del           删除状态[可空]["true","false"]
     * @param inSoldTime    是否在上下架时间范围[可空]["true","false"]
     * @param repositoryNum 库存数量[可空]
     * @param numCondition  库存数量条件[可空]["gt","gtAndEq","eq","lt","ltAndEq",其他任意值][默认值"doNothing"，不能单独使用，需与库存数量同时使用]
     * @param reqFrom       查询请求来源[可空]["front"表示前端商品查询：商品查询默认未删除，显示，审核通过，在上下架区间范围内,库存大于0]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findBackGoods")
    public ReturnMessages findGoodsByConditon(
            @RequestParam(name = "goodsId", required = false) String goodsId,
            @RequestParam(name = "goodsTitle", required = false) String goodsTitle,
            @RequestParam(name = "goodsSubTitle", required = false) String goodsSubTitle,
            @RequestParam(name = "stockId", required = false) String stockId,
            @RequestParam(name = "storeName",required = false)String storeName,
            @RequestParam(name = "classId", required = false) String classId,
            @RequestParam(name = "brandId", required = false) String brandId,
            @RequestParam(name = "keywords", required = false) String keywords,
            @RequestParam(name = "commission", required = false) String commission,
            @RequestParam(name = "goodsShow", required = false) String goodsShow,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            @RequestParam(name = "del", required = false) String del,
            @RequestParam(name = "inSoldTime", required = false) String inSoldTime,
            @RequestParam(name = "repositoryNum", required = false) String repositoryNum,
            @RequestParam(name = "numCondition", required = false) String numCondition,
            @RequestParam(name = "reqFrom", required = false) String reqFrom,
            @RequestParam(name = "pageNum", required = false) String pageNum,
            @RequestParam(name = "pageSize", required = false) String pageSize,
            @RequestParam(name = "pageSort", required = false) String pageSort,
            @RequestParam(name = "sortDirection", required = false) String sortDirection,
            @RequestParam(name = "forMe", required = false, defaultValue = "false") boolean forMe,
            HttpServletRequest request
    ) {
        ReturnMessages rm = new ReturnMessages();
        Goods goods = new Goods();


        if (StringUtils.isNotEmpty(goodsId)) {
            goods.setGoodsId(goodsId);
        }

        if (StringUtils.isNotEmpty(goodsTitle)) {
            goods.setGoodsTitle(goodsTitle);
        }

        if (StringUtils.isNotEmpty(goodsSubTitle)) {
            goods.setGoodsSubTitle(goodsSubTitle);
        }

        if (StringUtils.isNotEmpty(stockId)) {
            GoodsStock stock = stockService.findByStockId(stockId);
            if (stock != null) {
                goods.setGoodsStock(stock);
            } else {
                return new ReturnMessages(RequestState.ERROR, "库存不存在!", null);
            }
        }

        if (StringUtils.isNotEmpty(keywords)) {
            goods.setKeywords(keywords);
        }

        if (StringUtils.isNotEmpty(commission)) {
            try {
                goods.setCommission(Double.valueOf(commission));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "佣金参数格式有误!", null);
            }
        }

        //商品显示状态0不显示；1显示；2全部
        String goodsShowStr = "2";
        if (StringUtils.isNotEmpty(goodsShow)) {
            if (goodsShow.equals("0") || goodsShow.equals("1") || goodsShow.equals("2")) {
                goodsShowStr = goodsShow.toString();
            } else {
                return new ReturnMessages(RequestState.ERROR, "显示参数格式有误!", null);
            }

        }

        if (StringUtils.isNotEmpty(state)) {
            if ((state.equals(StateConstant.GOODS_STATE_CHECK_ON.toString()) || state.equals(StateConstant.GOODS_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.GOODS_STATE_CHECK_OFF.toString()))) {
                goods.setState(state);
            } else {
                return new ReturnMessages(RequestState.ERROR, "审核状态参数有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(del)) {
            if (del.equals("true") || del.equals("false")) {
                goods.setDel(Boolean.valueOf(del));

            } else {
                return new ReturnMessages(RequestState.ERROR, "删除状态参数有误!", null);
            }
        }

        //设置是否在上下架区间范围
        String inSoldTimeStr = "false";
        if (StringUtils.isNotEmpty(inSoldTime)) {
            if (inSoldTime.equals("true") || inSoldTime.equals("false")) {
                inSoldTimeStr = inSoldTime;
            } else {
                return new ReturnMessages(RequestState.ERROR, "inSoldTime参数有误!", null);
            }
        }

        //设置库存数量
        long stockNum = -1l;
        if (StringUtils.isNotEmpty(repositoryNum)) {

            try {
                stockNum = Long.valueOf(repositoryNum);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "库存数量参数格式有误！", null);
            }
        }
        //设置库存数据条件
        if (StringUtils.isNotEmpty(numCondition) && (numCondition.equals("gt") || numCondition.equals("gtAndEq") || numCondition.equals("eq") || numCondition.equals("lt") || numCondition.equals("ltAndEq"))) {

        } else {
            numCondition = "doNothing";
        }

        //前端请求，商品查询默认未删除，显示，审核通过，在上下架区间范围内
        if (StringUtils.isNotEmpty(reqFrom)) {
            if (reqFrom.equals("front")) {
                goods.setDel(Boolean.FALSE);
                goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                inSoldTimeStr = "true"; //在上架区间范围内
                //库存大于0
                stockNum = 0l;  //库存数量
                numCondition = "gt";    //库存数量条件
            }

        }

        Map<String, Object> mapParam = new HashMap<String, Object>();

        //设置创建时间条件查询
        long sTime = 0l;
        if (StringUtils.isNotEmpty(startTime)) {
            try {
                sTime = Long.valueOf(startTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "起始时间参数格式有误!", null);
            }
        }
        mapParam.put("startTime", sTime);
        long eTime = 0l;
        if (StringUtils.isNotEmpty(endTime)) {
            try {
                eTime = Long.valueOf(endTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "结束时间参数格式有误!", null);
            }
        }
        mapParam.put("endTime", eTime);

        //设置商品所属店铺
        Store store = null;
        if (StringUtils.isNotEmpty(storeName)) {
            store = storeService.findByStoreName(storeName);
            if (store != null) {
                if (store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "店铺不在正常运营状态!", null);

                }
            } else {
                return new ReturnMessages(RequestState.ERROR, "店铺不存在!", null);
            }
        }
        mapParam.put("store", store);

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //用户已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在,获取角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员
                    //do nothing
                } else if (roleCodeStr.contains("ROLE:SELLER")) {   //是商家
                    Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //重新设置店铺
                        mapParam.put("store", userStore);
                    }

                } else {  //买家不允许进入后台
                    return new ReturnMessages(RequestState.ERROR, "买家没有权限！", null);
                }

            }else{  //用户不存在
                return new ReturnMessages(RequestState.ERROR, "获取用户信息有误！", null);
            }

        }else{  //用户没登录
            return new ReturnMessages(RequestState.ERROR, "您还未登录！", null);
        }

        mapParam.put("goodsShowStr", goodsShowStr);
        mapParam.put("inSoldTime", inSoldTimeStr);
        mapParam.put("stockNum", stockNum);
        mapParam.put("numCondition", numCondition);
        //设置商品
        mapParam.put("goods", goods);
        //设置商品分类
        List<GoodsClass> gcList = new ArrayList<GoodsClass>();
        if (StringUtils.isNotEmpty(classId)) {
            GoodsClass gc_parent = gcService.findById(classId);
            if (gc_parent != null) {
                if (gc_parent.isGcShow()) {
                    gcList.add(gc_parent);
                    List<GoodsClass> gc_child = gcService.findGcChildren(gc_parent);
                    if (gc_child != null && gc_child.size() > 0) {
                        for (GoodsClass gc : gc_child) {
                            if (gc != null && gc.isGcShow()) { //添加显示的子类
                                gcList.add(gc);
                            }
                        }
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "分类不显示!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "分类不存在!", null);
            }
        }
        mapParam.put("gcList", gcList);

        //设置品牌
        Brand brand = null;
        if (StringUtils.isNotEmpty(brandId)) {
            brand = brandService.findById(brandId);
            if (brand != null) {
                if (brand.getState().equals(StateConstant.BRAND_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "品牌不在正常使用状态!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "品牌不存在!", null);
            }
        }
        mapParam.put("brand", brand);

        //设置查询分页
        UtilPage page = new UtilPage(0, 5, "createTime", Sort.Direction.DESC);
        if (StringUtils.isNotEmpty(pageNum)) {
            try {
                page.setPageNum(Integer.valueOf(pageNum));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页页数格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSize)) {
            try {
                page.setPageSize(Integer.valueOf(pageSize));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页大小格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSort)) {
            page.setPageSort(pageSort);
        }

        if (StringUtils.isNotEmpty(sortDirection)) {
            if (sortDirection.equals("asc")) {
                page.setDirection(Sort.Direction.ASC);
            } else if (sortDirection.equals("desc")) {
                page.setDirection(Sort.Direction.DESC);
            } else {
                return new ReturnMessages(RequestState.ERROR, "分页排序参数有误!", null);
            }

        }

        mapParam.put("page", page);

        Page<Goods> goodsPage = goodsService.findByCondition(mapParam);
        if (goodsPage != null && goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            rm.setMessages("查询成功!");
        } else {
            rm.setMessages("暂无数据!");
        }
        rm.setContent(goodsPage);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }

    /**
     * 条件分页查询简单商品
     *
     * @param goodsId       商品id[可空]
     * @param goodsTitle    商品标题[可空]
     * @param goodsSubTitle 商品副标题[可空]
     * @param stockId       商品库存id[可空]
     * @param storeId       商品所属店铺id[可空]
     * @param classId       商品所属分类id[可空]
     * @param brandId       商品所属品牌id[可空]
     * @param keywords      关键字[可空]
     * @param commission    商品提取佣金[可空]
     * @param goodsShow     商品显示状态[可空][0表示不显示，1表示显示，2包括前两者][默认2]
     * @param state         商品审核状态[可空]["GOODS_STATE_ON_CHECKING","GOODS_STATE_CHECK_ON","GOODS_STATE_CHECK_OFF"]
     * @param startTime     查询起始时间[可空][搜索商品创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索商品创建时间所在区间]
     * @param del           删除状态[可空]["true","false"]
     * @param inSoldTime    是否在上下架时间范围[可空]["true","false"]
     * @param repositoryNum 库存数量[可空]
     * @param numCondition  库存数量条件[可空]["gt","gtAndEq","eq","lt","ltAndEq",其他任意值][默认值"doNothing"，不能单独使用，需与库存数量同时使用]
     * @param reqFrom       查询请求来源[可空]["front"表示前端商品查询：商品查询默认未删除，显示，审核通过，在上下架区间范围内,库存大于0]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findBackSimpleGoods")
    public ReturnMessages findSimpleGoods(@RequestParam(name = "goodsId", required = false) String goodsId,
                                          @RequestParam(name = "goodsTitle", required = false) String goodsTitle,
                                          @RequestParam(name = "goodsSubTitle", required = false) String goodsSubTitle,
                                          @RequestParam(name = "stockId", required = false) String stockId,
                                          @RequestParam(name = "storeId", required = false) String storeId,
                                          @RequestParam(name = "classId", required = false) String classId,
                                          @RequestParam(name = "brandId", required = false) String brandId,
                                          @RequestParam(name = "keywords", required = false) String keywords,
                                          @RequestParam(name = "commission", required = false) String commission,
                                          @RequestParam(name = "goodsShow", required = false) String goodsShow,
                                          @RequestParam(name = "state", required = false) String state,
                                          @RequestParam(name = "startTime", required = false) String startTime,
                                          @RequestParam(name = "endTime", required = false) String endTime,
                                          @RequestParam(name = "del", required = false) String del,
                                          @RequestParam(name = "inSoldTime", required = false) String inSoldTime,
                                          @RequestParam(name = "repositoryNum", required = false) String repositoryNum,
                                          @RequestParam(name = "numCondition", required = false) String numCondition,
                                          @RequestParam(name = "reqFrom", required = false) String reqFrom,
                                          @RequestParam(name = "pageNum", required = false) String pageNum,
                                          @RequestParam(name = "pageSize", required = false) String pageSize,
                                          @RequestParam(name = "pageSort", required = false) String pageSort,
                                          @RequestParam(name = "sortDirection", required = false) String sortDirection,
                                          HttpServletRequest request
    ) {

        ReturnMessages rm = new ReturnMessages();
        Goods goods = new Goods();
        if (StringUtils.isNotEmpty(goodsId)) {
            goods.setGoodsId(goodsId);
        }

        if (StringUtils.isNotEmpty(goodsTitle)) {
            goods.setGoodsTitle(goodsTitle);
        }

        if (StringUtils.isNotEmpty(goodsSubTitle)) {
            goods.setGoodsSubTitle(goodsSubTitle);
        }

        if (StringUtils.isNotEmpty(stockId)) {
            GoodsStock stock = stockService.findByStockId(stockId);
            if (stock != null) {
                goods.setGoodsStock(stock);
            } else {
                return new ReturnMessages(RequestState.ERROR, "库存不存在!", null);
            }
        }

        if (StringUtils.isNotEmpty(keywords)) {
            goods.setKeywords(keywords);
        }

        if (StringUtils.isNotEmpty(commission)) {
            try {
                goods.setCommission(Double.valueOf(commission));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "佣金参数格式有误!", null);
            }
        }

        //商品显示状态0不显示；1显示；2全部
        String goodsShowStr = "2";
        if (StringUtils.isNotEmpty(goodsShow)) {
            if (goodsShow.equals("0") || goodsShow.equals("1") || goodsShow.equals("2")) {
                goodsShowStr = goodsShow.toString();
            } else {
                return new ReturnMessages(RequestState.ERROR, "显示参数格式有误!", null);
            }

        }

        if (StringUtils.isNotEmpty(state)) {
            if ((state.equals(StateConstant.GOODS_STATE_CHECK_ON.toString()) || state.equals(StateConstant.GOODS_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.GOODS_STATE_CHECK_OFF.toString()))) {
                goods.setState(state);
            } else {
                return new ReturnMessages(RequestState.ERROR, "审核状态参数有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(del)) {
            if (del.equals("true") || del.equals("false")) {
                goods.setDel(Boolean.valueOf(del));

            } else {
                return new ReturnMessages(RequestState.ERROR, "删除状态参数有误!", null);
            }
        }

        //设置是否在上下架区间范围
        String inSoldTimeStr = "false";
        if (StringUtils.isNotEmpty(inSoldTime)) {
            if (inSoldTime.equals("true") || inSoldTime.equals("false")) {
                inSoldTimeStr = inSoldTime;
            } else {
                return new ReturnMessages(RequestState.ERROR, "inSoldTime参数有误!", null);
            }
        }

        //设置库存数量
        long stockNum = -1l;
        if (StringUtils.isNotEmpty(repositoryNum)) {

            try {
                stockNum = Long.valueOf(repositoryNum);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "库存数量参数格式有误！", null);
            }
        }
        //设置库存数据条件
        if (StringUtils.isNotEmpty(numCondition) && (numCondition.equals("gt") || numCondition.equals("gtAndEq") || numCondition.equals("eq") || numCondition.equals("lt") || numCondition.equals("ltAndEq"))) {

        } else {
            numCondition = "doNothing";
        }

        //前端请求，商品查询默认未删除，显示，审核通过，在上下架区间范围内
        if (StringUtils.isNotEmpty(reqFrom)) {
            if (reqFrom.equals("front")) {
                goods.setDel(Boolean.FALSE);
                goodsShowStr = "1"; //商品显示状态0不显示；1显示；2全部
                goods.setState(StateConstant.GOODS_STATE_CHECK_ON.toString());
                inSoldTimeStr = "true"; //在上架区间范围内
                stockNum = 0l;  //库存数量
                numCondition = "gt";    //库存数量条件
            }

        }

        Map<String, Object> mapParam = new HashMap<String, Object>();

        //设置创建时间条件查询
        long sTime = 0l;
        if (StringUtils.isNotEmpty(startTime)) {
            try {
                sTime = Long.valueOf(startTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "起始时间参数格式有误!", null);
            }
        }
        mapParam.put("startTime", sTime);
        long eTime = 0l;
        if (StringUtils.isNotEmpty(endTime)) {
            try {
                eTime = Long.valueOf(endTime);
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "结束时间参数格式有误!", null);
            }
        }
        mapParam.put("endTime", eTime);

        //设置商品所属店铺
        Store store = null;
        if (StringUtils.isNotEmpty(storeId)) {
            store = storeService.findByStoreId(storeId);
            if (store != null) {
                if (store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "店铺不在正常运营状态!", null);

                }
            } else {
                return new ReturnMessages(RequestState.ERROR, "店铺不存在!", null);
            }
        }
        mapParam.put("store", store);

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //用户已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在,获取角色
                List<Role> roleList = roleService.findRoleByMember(member);
                String roleCodeStr = "";
                if (roleList != null && roleList.size() > 0) {
                    for (Role role : roleList) {
                        String roleCode = role.getRoleCode();
                        roleCodeStr = roleCodeStr + roleCode + ",";
                    }
                }
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员
                    //do nothing
                } else if (roleCodeStr.contains("ROLE:SELLER")) {   //是商家
                    Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //重新设置店铺
                        mapParam.put("store", userStore);
                    }

                } else {  //买家不允许进入后台
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }

            }else{  //用户不存在
                return new ReturnMessages(RequestState.ERROR, "获取用户信息有误！", null);
            }

        }else{  //用户没登录
            return new ReturnMessages(RequestState.ERROR, "您还未登录！", null);
        }
        mapParam.put("goodsShowStr", goodsShowStr);
        mapParam.put("inSoldTime", inSoldTimeStr);
        mapParam.put("stockNum", stockNum);
        mapParam.put("numCondition", numCondition);
        mapParam.put("goods", goods);

        //设置商品分类
        List<GoodsClass> gcList = new ArrayList<GoodsClass>();
        if (StringUtils.isNotEmpty(classId)) {
            GoodsClass gc_parent = gcService.findById(classId);
            if (gc_parent != null) {
                if (gc_parent.isGcShow()) {
                    gcList.add(gc_parent);
                    List<GoodsClass> gc_child = gcService.findGcChildren(gc_parent);
                    if (gc_child != null && gc_child.size() > 0) {
                        for (GoodsClass gc : gc_child) {
                            if (gc != null && gc.isGcShow()) { //添加显示的子类
                                gcList.add(gc);
                            }
                        }
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "分类不显示!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "分类不存在!", null);
            }
        }
        mapParam.put("gcList", gcList);

        //设置品牌
        Brand brand = null;
        if (StringUtils.isNotEmpty(brandId)) {
            brand = brandService.findById(brandId);
            if (brand != null) {
                if (brand.getState().equals(StateConstant.BRAND_STATE_CHECK_ON.toString())) {

                } else {
                    return new ReturnMessages(RequestState.ERROR, "品牌不在正常使用状态!", null);
                }

            } else {
                return new ReturnMessages(RequestState.ERROR, "品牌不存在!", null);
            }
        }
        mapParam.put("brand", brand);

        //设置查询分页
        UtilPage page = new UtilPage(0, 5, "createTime", Sort.Direction.DESC);
        if (StringUtils.isNotEmpty(pageNum)) {
            try {
                page.setPageNum(Integer.valueOf(pageNum));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页页数格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSize)) {
            try {
                page.setPageSize(Integer.valueOf(pageSize));
            } catch (Exception e) {
                return new ReturnMessages(RequestState.ERROR, "分页大小格式有误!", null);
            }
        }

        if (StringUtils.isNotEmpty(pageSort)) {
            page.setPageSort(pageSort);
        }

        if (StringUtils.isNotEmpty(sortDirection)) {
            if (sortDirection.equals("asc")) {
                page.setDirection(Sort.Direction.ASC);
            } else if (sortDirection.equals("desc")) {
                page.setDirection(Sort.Direction.DESC);
            } else {
                return new ReturnMessages(RequestState.ERROR, "分页排序参数有误!", null);
            }

        }

        mapParam.put("page", page);

        Page<Goods> goodsList = goodsService.findSimpleGoodsByCondition(mapParam);
        if (goodsList.getContent() != null && goodsList.getContent().size() > 0) {
            rm.setMessages("查询成功!");
        } else {
            rm.setMessages("暂无数据!");
        }

        rm.setContent(goodsList);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }
}
