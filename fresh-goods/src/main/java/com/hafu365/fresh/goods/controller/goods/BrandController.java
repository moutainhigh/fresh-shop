package com.hafu365.fresh.goods.controller.goods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsClass;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.BrandService;
import com.hafu365.fresh.service.goods.GoodsClassService;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.role.RoleService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.log4j.Log4j;
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
 * 品牌(无店铺的品牌为公用品牌，关联分类后，发布商品时根据商品分类所有店铺的商品都可使用；默认品牌为初始化数据，发布商品不选品牌时为默认品牌；管理员可以发布编辑公用品牌和默认店铺的品牌；)
 * Created by HuangWeizhen on 2017/8/14.
 */
@RestController
@RequestMapping("/brand")
@Log4j
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private GoodsClassService gcService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MemberInfoService memberInfoService;

    @Autowired
    private RoleService roleService;

    /**
     * 添加品牌
     * @param brandTitle    品牌标题
     * @param pics          品牌图片集合
     * @param classIds      品牌关联的分类id拼接的字符串[可空]
     * @param storeId       店铺id[可空]
     * @return
     */
    @RequestMapping("/save")
    public ReturnMessages addBrand(@RequestParam(name = "brandTitle",required = true)String brandTitle,
                                   @RequestParam(name = "pics",required = true)String pics,
                                   @RequestParam(name = "classIds",required = false)String classIds,
                                   @RequestParam(name = "storeId",required = false)String storeId,
                                   HttpServletRequest request
    ){

        ReturnMessages rm = null;
        Brand brand = new Brand();

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
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        Store userStore = storeService.findByMember(member);
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,可以添加公用品牌 && 默认店铺的品牌
            if(StringUtils.isNotEmpty(storeId)){ //店铺不为空，添加默认店铺品牌
                Store store = storeService.findByStoreId(storeId);
                Store defaultStore = storeService.findStoreByTheDefaultTrue();
                if(store != null && defaultStore != null && store.getStoreName().equals(defaultStore.getStoreName())){ //店铺不为空，且为默认店铺
                    brand.setStore(store);
                }else if(store == null){    //店铺为空
                    return new ReturnMessages(RequestState.ERROR,"店铺不存在！",null);
                }else{  //店铺不为空，且不为默认店铺
                    return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
                }
            }else{ //店铺为空，添加公用品牌（公用品牌不设置店铺）

            }

        }else if(roleCodeStr.contains("ROLE:SELLER")){  //是商家, 只能添加自己的品牌
            if(userStore == null){
                return new ReturnMessages(RequestState.ERROR,"您还没有店铺！",null);
            }else{
                if(userStore.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())){//店铺处于审核通过状态才能添加品牌
                    brand.setStore(userStore);  //默认添加自己的品牌
                }else{
                    return new ReturnMessages(RequestState.ERROR,"您的店铺未通过审核！",null);
                }
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }

        if(StringUtils.isNotEmpty(brandTitle)){
            Brand brandSearch = brandService.findByBrandTitle(brandTitle);
            if(brandSearch != null){
                return new ReturnMessages(RequestState.ERROR,"该品牌名称已存在！",null);//品牌名称相同不能填加
            }
            brand.setBrandTitle(brandTitle);
        }else{
            return new ReturnMessages(RequestState.ERROR,"品牌名称不能为空！",null);
        }

        if(StringUtils.isNotEmpty(pics)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {}.getType();
            try{
                List<Image> imageList = gson.fromJson(pics,type);
                brand.setBrandPic(imageList);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"图片请求参数有误！",null);
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"品牌图片不能为空！",null);
        }

        //品牌关联分类
        if(StringUtils.isNotEmpty(classIds)){
            List<GoodsClass> gcList = new ArrayList<GoodsClass>();
            if(classIds.contains(",")){
                String[] brandIdArray = classIds.split(",");
                for(String idStr : brandIdArray){
                    GoodsClass gc = gcService.findById(idStr);
                    if(gc != null){
                        gcList.add(gc);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"分类不存在!",null);
                    }
                }
            }else{
                GoodsClass gc = gcService.findById(classIds);
                if(gc != null){
                    gcList.add(gc);
                }else{
                    return new ReturnMessages(RequestState.ERROR,"分类不存在!",null);
                }

            }
            if(gcList.size() > 0){
                brand.setGoodsClassList(gcList);
            }
        }


        Brand brandRes = brandService.save(brand);
        if(brandRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"品牌保存成功！",brandRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"品牌保存失败！",null);
        }
        return rm;
    }

    /**
     * 编辑品牌
     * @param brandId       品牌id
     * @param brandTitle    品牌标题[可空]
     * @param pics          品牌图片集合[可空]
     * @param state         品牌审核状态[可空]["BRAND_STATE_CHECK_ON","BRAND_STATE_CHECK_OFF"]
     * @param classIds      品牌关联的分类id拼接的字符串[可空]
     * @return
     */
    @RequestMapping("/update")
    public ReturnMessages updateBrand(@RequestParam(name = "brandId",required = true)String brandId,
                                       @RequestParam(name = "brandTitle",required = false)String brandTitle,
                                       @RequestParam(name = "pics",required = false)String pics,
                                      @RequestParam(name = "state",required = false)String state,
                                      @RequestParam(name = "classIds",required = false)String classIds,
                                      HttpServletRequest request
    ){

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

        Brand brandSearch = null;
        ReturnMessages rm = null;
        Brand brand = new Brand();
        if(StringUtils.isNotEmpty(brandId)){
            brandSearch = brandService.findById(brandId);
            if(brandSearch == null){
                return new ReturnMessages(RequestState.ERROR,"品牌不存在！",null);
            }else{
                //设置id
                BeanUtils.copyProperties(brandSearch,brand);
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"品牌参数有误！",null);
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
        Store brandStore = brandSearch.getStore();
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,能进行审核 或者 编辑平台品牌及默认店铺的品牌
            //审核品牌
            if(StringUtils.isNotEmpty(state)){
                if(state.equals(StateConstant.BRAND_STATE_CHECK_ON.toString()) || state.equals(StateConstant.BRAND_STATE_CHECK_OFF.toString())){
                    brand.setState(state);
                    Brand brandRes = brandService.update(brand);
                    if(brandRes != null){
                        rm = new ReturnMessages(RequestState.SUCCESS,"品牌审核成功！",brandRes);

                    }else{
                        rm = new ReturnMessages(RequestState.ERROR,"品牌审核失败！",null);
                    }
                    return rm;

                }else{
                    return new ReturnMessages(RequestState.ERROR,"状态参数有误！",null);
                }

            }else{  // 编辑品牌
                Store defaultStore = storeService.findStoreByTheDefaultTrue();
                if(brandStore == null || (brandStore != null && defaultStore != null && brandStore.getStoreName().equals(defaultStore.getStoreName()))){ //是公共品牌或默认店铺品牌
                    //可以编辑
                }else{
                    return new ReturnMessages(RequestState.ERROR,"没有权限编辑商家的品牌！",null);
                }

            }

        }else if(roleCodeStr.contains("ROLE:SELLER")){  //是商家,只能编辑自己店铺的品牌
            if(userStore == null){
                return new ReturnMessages(RequestState.ERROR,"您还没有店铺！",null);
            }else{
                String brandStoreId = "";
                String userStoreId = userStore.getStoreId();
                if( brandStore != null){
                    brandStoreId = brandStore.getStoreId();
                    if(!brandStoreId.equals(userStoreId)){
                        return new ReturnMessages(RequestState.ERROR,"商家只能编辑自己店铺的品牌！",null);
                    }
                }
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }


        //设置标题
        if(StringUtils.isNotEmpty(brandTitle)){
            Brand titleSearchBrand = brandService.findByBrandTitle(brandTitle);
            if(titleSearchBrand != null && !(titleSearchBrand.getBrandId().equals(brandId))){
                return new ReturnMessages(RequestState.ERROR,"该品牌名称已存在！",null);//品牌名称相同不能添加
            }
            brand.setBrandTitle(brandTitle);
        }

        //设置图片
        if(StringUtils.isNotEmpty(pics)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {}.getType();
            try{
                List<Image> imageList = gson.fromJson(pics,type);
                brand.setBrandPic(imageList);
                brand.setPics(pics);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"图片请求参数有误！",null);
            }
        }

        //品牌关联分类
        if(StringUtils.isNotEmpty(classIds)){
            List<GoodsClass> gcList = new ArrayList<GoodsClass>();
            if(classIds.contains(",")){
                String[] brandIdArray = classIds.split(",");
                for(String idStr : brandIdArray){
                    GoodsClass gc = gcService.findById(idStr);
                    if(gc != null){
                        gcList.add(gc);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"分类不存在!",null);
                    }
                }
            }else{
                GoodsClass gc = gcService.findById(classIds);
                if(gc != null){
                    gcList.add(gc);
                }else{
                    return new ReturnMessages(RequestState.ERROR,"分类不存在!",null);
                }

            }
            if(gcList.size() > 0){
                brand.setGoodsClassList(gcList);
            }
        }
        //品牌状态为审核未通过或已关闭，修改后，状态改为待审核
        String brandState = brand.getState();
        if(StringUtils.isNotEmpty(brandState) && (brandState.equals(StateConstant.BRAND_STATE_CHECK_OFF.toString()) || brandState.equals(StateConstant.BRAND_STATE_ON_CLOSE.toString()))){
            brand.setState(StateConstant.BRAND_STATE_ON_CHECKING.toString());

        }
        Brand brandRes = brandService.update(brand);
        if(brandRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"品牌更新成功！",brandRes);

        }else{
            rm = new ReturnMessages(RequestState.ERROR,"品牌更新失败！",null);
        }
        return rm;
    }

    /**
     * 物理删除品牌
     * @param brandId   品牌id
     * @return
     */
    @RequestMapping("/physicalDelete")
    public ReturnMessages physicalDelete(@RequestParam(name = "brandId",required = true)String brandId,
                                         HttpServletRequest request
    ){

        Brand brand = null;
        if(StringUtils.isNotEmpty(brandId)){
            brand = brandService.findById(brandId);
        }else{
            return new ReturnMessages(RequestState.ERROR,"参数有误！",null);
        }

        if(brand == null){
            return new ReturnMessages(RequestState.ERROR,"品牌不存在!",null);

        }else{
            Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
            if(defaultBrand != null && defaultBrand.getBrandId().equals(brandId)){  //是默认品牌
                return new ReturnMessages(RequestState.ERROR,"默认品牌暂时不能删除，请更改默认品牌后再删除！",null);
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
                Store brandStore = brand.getStore();
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员,只能删除公用品牌 和 默认店铺的品牌
                    if (brandStore == null) { //是公用品牌
                        //没有商品关联的时候才能删除
                        List<Goods> goodsList = goodsService.findByBrandAndDelFalse(brand);
                        if (goodsList != null && goodsList.size() > 0) {
                            return new ReturnMessages(RequestState.ERROR, "品牌下关联有商品，请删除商品后再删除！", goodsList);
                        }
                    } else {
                        Store defaultStore = storeService.findStoreByTheDefaultTrue();
                        if (defaultStore != null && brandStore.getStoreName().equals(defaultStore.getStoreName())) {  //是默认店铺品牌
                            //可以删除

                        } else {
                            return new ReturnMessages(RequestState.ERROR, "没有权限删除商家的品牌！", null);
                        }
                    }

                } else if (roleCodeStr.contains("ROLE:SELLER")) {  //是商家,只能删除自己店铺的品牌
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺,不能删除其他商户的品牌！！", null);
                    } else {
                        String brandStoreId = "";
                        String userStoreId = userStore.getStoreId();
                        if (brandStore != null) {
                            brandStoreId = brandStore.getStoreId();
                            if (!brandStoreId.equals(userStoreId)) {
                                return new ReturnMessages(RequestState.ERROR, "商家只能删除自己店铺的品牌！", null);
                            } else {    //可以删除自己的品牌

                            }
                        }
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }
                List<Goods> goodsList = goodsService.findByBrandAndDelFalse(brand);
                if(goodsList != null && goodsList.size() > 0){
                    return new ReturnMessages(RequestState.ERROR, "品牌下有商品，请删除商品后再删除品牌！", goodsList);
                }

                if (brandService.physicalDelete(brandId)) {
                    return new ReturnMessages(RequestState.SUCCESS, "删除成功!", null);
                }
                return new ReturnMessages(RequestState.ERROR, "删除失败!", null);
            }
        }
    }

    /**
     * 逻辑删除品牌
     * @param brandId   品牌id
     * @return
     */
    @RequestMapping("/delete")
    public ReturnMessages delete(@RequestParam(name = "brandId",required = true)String brandId,
                                 HttpServletRequest request
    ){

        Brand brand = null;
        if(StringUtils.isNotEmpty(brandId)){
            brand = brandService.findById(brandId);
        }else{
            return new ReturnMessages(RequestState.ERROR,"参数有误！",null);
        }

        if(brand == null){
            return new ReturnMessages(RequestState.ERROR,"品牌不存在!",null);

        }else{
            Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
            if(defaultBrand != null && defaultBrand.getBrandId().equals(brandId)){  //是默认品牌
                return new ReturnMessages(RequestState.ERROR,"默认品牌暂时不能删除，请更改默认品牌后再删除！",null);
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
                Store brandStore = brand.getStore();
                if (roleCodeStr.contains("ROLE:ADMIN")) { //是管理员,只能删除公用品牌 和 默认店铺的品牌
                    if (brandStore == null) { //是公用品牌
                        //没有商品关联的时候才能删除
                        List<Goods> goodsList = goodsService.findByBrandAndDelFalse(brand);
                        if (goodsList != null && goodsList.size() > 0) {
                            return new ReturnMessages(RequestState.ERROR, "品牌下关联有商品，请删除商品后再删除！", goodsList);
                        }
                    } else {
                        Store defaultStore = storeService.findStoreByTheDefaultTrue();
                        if (defaultStore != null && brandStore.getStoreName().equals(defaultStore.getStoreName())) {  //是默认店铺品牌
                            //可以删除
                        } else {
                            return new ReturnMessages(RequestState.ERROR, "没有权限删除商家的品牌！", null);
                        }
                    }

                } else if (roleCodeStr.contains("ROLE:SELLER")) {  //是商家,只能删除自己店铺的商品
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺,不能删除其他商户的品牌！", null);
                    } else {
                        String brandStoreId = "";
                        String userStoreId = userStore.getStoreId();
                        if (brandStore != null) {
                            brandStoreId = brandStore.getStoreId();
                            if (!brandStoreId.equals(userStoreId)) {
                                return new ReturnMessages(RequestState.ERROR, "商家只能删除自己店铺的品牌！", null);
                            } else {

                            }
                        }
                    }

                } else {
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }

                List<Goods> goodsList = goodsService.findByBrandAndDelFalse(brand);
                if(goodsList != null && goodsList.size() > 0){
                    return new ReturnMessages(RequestState.ERROR, "品牌下有商品，请删除商品后再删除品牌！", goodsList);
                }
                if (brandService.delete(brandId)) {
                    return new ReturnMessages(RequestState.SUCCESS, "删除成功!", null);
                }
                return new ReturnMessages(RequestState.ERROR, "删除失败!", null);
            }
        }
    }

    /**
     * 关闭品牌
     * @param brandId   品牌id
     * @return
     */
    @RequestMapping("/close")
    public ReturnMessages closeBrand(@RequestParam(name = "brandId",required = true)String brandId,
                                     HttpServletRequest request
    ){

        Brand brand = null;
        if(StringUtils.isNotEmpty(brandId)){
            brand = brandService.findById(brandId);
            if(brand == null){
                return new ReturnMessages(RequestState.ERROR,"品牌不存在！",null);
            }else{
                Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
                if(defaultBrand != null && defaultBrand.getBrandId().equals(brandId)){  //是默认品牌
                    return new ReturnMessages(RequestState.ERROR,"默认品牌暂时不能关闭，请更改默认品牌后再关闭！",null);
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
                Store brandStore = brand.getStore();
                if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,可以关闭品牌

                }else if(roleCodeStr.contains("ROLE:SELLER")){  //是商家，可以关闭自己的品牌
                    if(userStore == null ){
                        return new ReturnMessages(RequestState.ERROR,"您还没有店铺，不能关闭其他商户的品牌！",null);
                    }else{
                        String userStoreId = userStore.getStoreId();
                        if(brandStore != null){
                            String brandStoreId = brandStore.getStoreId();
                            if(!userStoreId.equals(brandStoreId)){
                                return new ReturnMessages(RequestState.ERROR,"商家只能删除关闭自己店铺的品牌！",null);
                            }
                        }else{  //品牌没有店铺,为公用品牌，商家没有权限关闭
                            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
                        }

                    }

                }else{
                    return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
                }
                List<Goods> goodsList = goodsService.findByBrandAndDelFalse(brand);
                if(goodsList != null && goodsList.size() > 0){
                    return new ReturnMessages(RequestState.ERROR, "品牌下有商品，请删除商品后再关闭品牌！", goodsList);
                }
                Boolean res = brandService.closeBrand(brandId);
                if(res){
                    return new ReturnMessages(RequestState.SUCCESS,"品牌关闭成功！",null);
                }else{
                    return new ReturnMessages(RequestState.ERROR,"品牌关闭失败！",null);
                }
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"品牌参数有误！",null);
        }
    }

    //**添加查询前端品牌
    /**
     * 查找品牌
     * @param brandId       品牌id[可空]
     * @param brandTitle    品牌标题[可空]
     * @param storeId       品牌所属店铺id[可空]
     * @param state         品牌审核状态[可空]["BRAND_STATE_ON_CHECKING","BRAND_STATE_CHECK_ON","BRAND_STATE_CHECK_OFF","BRAND_STATE_ON_CLOSE"]
     * @param del           品牌删除状态[可空]["true","false"]
     * @param startTime     查询起始时间[可空][搜索品牌创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索品牌创建时间所在区间]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findFrontBrands")
    public ReturnMessages findFrontBrands(@RequestParam(name = "brandId",required = false)String brandId,
                                     @RequestParam(name = "brandTitle",required = false)String brandTitle,
                                     @RequestParam(name = "storeId",required = false)String storeId,
                                     @RequestParam(name = "state",required = false)String state,
                                     @RequestParam(name = "del",required = false)String del,
                                     @RequestParam(name = "startTime",required = false)String startTime,
                                     @RequestParam(name = "endTime",required = false)String endTime,
                                     @RequestParam(name = "pageNum",required = false)String pageNum,
                                     @RequestParam(name = "pageSize",required = false)String pageSize,
                                     @RequestParam(name = "pageSort",required = false)String pageSort,
                                     @RequestParam(name = "sortDirection",required = false)String sortDirection,
                                     HttpServletRequest request
    ){
        ReturnMessages rm = new ReturnMessages();

        Brand brand = new Brand();
        //设置品牌id
        if(StringUtils.isNotEmpty(brandId)){
            Brand brandSearch = brandService.findById(brandId);
            if(brandSearch == null){
                return new ReturnMessages(RequestState.ERROR,"品牌不存在!",null);
            }else{
                brand.setBrandId(brandId);
            }

        }

        //设置品牌标题
        if(StringUtils.isNotEmpty(brandTitle)){
            brand.setBrandTitle(brandTitle);
        }

        //设置状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.BRAND_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.BRAND_STATE_CHECK_ON.toString()) || state.equals(StateConstant.BRAND_STATE_CHECK_OFF.toString()) || state.equals(StateConstant.BRAND_STATE_ON_CLOSE.toString())){
                brand.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"审核状态参数有误!",null);
            }
        }

        //设置删除状态
        if(StringUtils.isNotEmpty(del)){
            if(del.equals("true") || del.equals("false")){
                brand.setDel(Boolean.valueOf(del));

            }else{
                return new ReturnMessages(RequestState.ERROR,"删除状态参数有误!",null);
            }
        }

        //设置店铺
        if(StringUtils.isNotEmpty(storeId)){
            Store store = storeService.findByStoreId(storeId);
            if(store != null){
                if(store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())){
                    brand.setStore(store);
                }else{
                    return new ReturnMessages(RequestState.ERROR,"店铺不在正常运营状态!",null);
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
            }
        }

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //用户已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在获取角色
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
                }else if(roleCodeStr.contains("ROLE:SELLER")){   //是商家，商家登录前端也可以查看前端品牌
                    /*Store userStore = storeService.findByMember(member);
                    if (userStore == null) {
                        return new ReturnMessages(RequestState.ERROR, "您还没有店铺！", null);
                    } else {
                        //重新设置店铺
                        brand.setStore(userStore);
                    }*/
                    brand.setDel(false);
                    brand.setState(StateConstant.BRAND_STATE_CHECK_ON.toString());

                }else{  //买家<相当于前端请求，品牌查询默认未删除，审核通过>
                    brand.setDel(false);
                    brand.setState(StateConstant.BRAND_STATE_CHECK_ON.toString());
                }

            }else{  //用户不存在<等同于未登录，可以查看前端品牌>
                brand.setDel(false);
                brand.setState(StateConstant.BRAND_STATE_CHECK_ON.toString());
            }

        }else{  //用户未登录,可以查看前端品牌
            brand.setDel(false);
            brand.setState(StateConstant.BRAND_STATE_CHECK_ON.toString());
        }

        Map<String,Object> mapParam = new HashMap<String,Object>();
        //设置品牌
        mapParam.put("brand",brand);
        Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
        mapParam.put("initBrand",defaultBrand);

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

        Page<Brand> brandPage = brandService.findBrand(mapParam);
        if(brandPage != null && brandPage.getContent() != null && brandPage.getContent().size() > 0){
            rm.setMessages("查询成功!");
        }else{
            rm.setMessages("暂无数据!");
        }
        rm.setContent(brandPage);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }

    /**
     * 查找品牌
     * @param brandId       品牌id[可空]
     * @param brandTitle    品牌标题[可空]
     * @param storeId       品牌所属店铺id[可空]
     * @param state         品牌审核状态[可空]["BRAND_STATE_ON_CHECKING","BRAND_STATE_CHECK_ON","BRAND_STATE_CHECK_OFF","BRAND_STATE_ON_CLOSE"]
     * @param del           品牌删除状态[可空]["true","false"]
     * @param startTime     查询起始时间[可空][搜索品牌创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索品牌创建时间所在区间]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findBrands")
    public ReturnMessages findBrands(@RequestParam(name = "brandId",required = false)String brandId,
                                     @RequestParam(name = "brandTitle",required = false)String brandTitle,
                                     @RequestParam(name = "storeId",required = false)String storeId,
                                     @RequestParam(name = "state",required = false)String state,
                                     @RequestParam(name = "del",required = false)String del,
                                     @RequestParam(name = "startTime",required = false)String startTime,
                                     @RequestParam(name = "endTime",required = false)String endTime,
                                     @RequestParam(name = "pageNum",required = false)String pageNum,
                                     @RequestParam(name = "pageSize",required = false)String pageSize,
                                     @RequestParam(name = "pageSort",required = false)String pageSort,
                                     @RequestParam(name = "sortDirection",required = false)String sortDirection,
                                     HttpServletRequest request
                                     ){
        ReturnMessages rm = new ReturnMessages();

        Brand brand = new Brand();
        //设置品牌id
        if(StringUtils.isNotEmpty(brandId)){
            Brand brandSearch = brandService.findById(brandId);
            if(brandSearch == null){
                return new ReturnMessages(RequestState.ERROR,"品牌不存在!",null);
            }else{
                brand.setBrandId(brandId);
            }

        }

        //设置品牌标题
        if(StringUtils.isNotEmpty(brandTitle)){
            brand.setBrandTitle(brandTitle);
        }

        //设置状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.BRAND_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.BRAND_STATE_CHECK_ON.toString()) || state.equals(StateConstant.BRAND_STATE_CHECK_OFF.toString()) || state.equals(StateConstant.BRAND_STATE_ON_CLOSE.toString())){
                brand.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"审核状态参数有误!",null);
            }
        }

        //设置删除状态
        if(StringUtils.isNotEmpty(del)){
            if(del.equals("true") || del.equals("false")){
                brand.setDel(Boolean.valueOf(del));

            }else{
                return new ReturnMessages(RequestState.ERROR,"删除状态参数有误!",null);
            }
        }

        //设置店铺
        if(StringUtils.isNotEmpty(storeId)){
            Store store = storeService.findByStoreId(storeId);
            if(store != null){
                if(store.getState().equals(StateConstant.STORE_STATE_CHECK_ON.toString())){
                    brand.setStore(store);
                }else{
                    return new ReturnMessages(RequestState.ERROR,"店铺不在正常运营状态!",null);
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
            }
        }

        //获取当前用户的角色
        String userName = SecurityUtils.getUsername(request);
        if(StringUtils.isNotEmpty(userName)){   //用户已登录
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){ //用户存在获取角色
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
                        //重新设置店铺
                        brand.setStore(userStore);
                    }

                }else{  //买家不允许进入后台
                    return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
                }

            }else{  //用户不存在
                return new ReturnMessages(RequestState.ERROR, "获取用户信息有误！", null);
            }

        }else{  //用户未登录
            return new ReturnMessages(RequestState.ERROR, "您还未登录！", null);
        }

        Map<String,Object> mapParam = new HashMap<String,Object>();
        //设置品牌
        mapParam.put("brand",brand);
        Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
        mapParam.put("initBrand",defaultBrand);

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

        Page<Brand> brandPage = brandService.findBrand(mapParam);
        if(brandPage != null && brandPage.getContent() != null && brandPage.getContent().size() > 0){
            rm.setMessages("查询成功!");
        }else{
            rm.setMessages("暂无数据!");
        }
        rm.setContent(brandPage);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }

    /**
     * 查询分类及店铺下的品牌
     * @param classId   分类id
     * @param storeId   店铺id[可空]
     * @return
     */
    @RequestMapping("/findByGcAndStore")
    public ReturnMessages findBrandsByGcAndStore(@RequestParam(name = "classId",required = true)String classId,
                                     @RequestParam(name = "storeId",required = false,defaultValue = "")String storeId,
                                                 HttpServletRequest request
    ){

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
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }

        ReturnMessages rm = new ReturnMessages();
        if(StringUtils.isNotEmpty(classId)){
            GoodsClass gc = gcService.findById(classId);
            if(gc == null){
                return new ReturnMessages(RequestState.ERROR,"分类不存在!",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"分类参数有误!",null);
        }
        if(StringUtils.isNotEmpty(storeId)){
            Store store = storeService.findByStoreId(storeId);
            if(store == null){
                return new ReturnMessages(RequestState.ERROR,"店铺不存在!",null);
            }

        }else{
            if (roleCodeStr.contains("ROLE:ADMIN")) {   //是管理员，且店铺值为空，则店铺为默认店铺
                Store defaultStore = storeService.findStoreByTheDefaultTrue();
                if(defaultStore == null){
                    return new ReturnMessages(RequestState.ERROR, "默认店铺获取有误！", null);
                }else{
                    storeId = defaultStore.getStoreId();
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"店铺参数有误!",null);
            }

        }

        List<Brand> brandList = brandService.findByGcAndStore(classId,storeId);

        Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
        if(defaultBrand != null){
            if(brandList.contains(defaultBrand)){
                brandList.remove(defaultBrand);
            }
            brandList.add(0,defaultBrand);  //默认品牌放在首位
        }
        if(brandList != null && brandList.size() > 0){
            rm.setMessages("查询成功!");
        }else{
            rm.setMessages("暂无数据!");
        }
        rm.setContent(brandList);
        rm.setState(RequestState.SUCCESS);
        return rm;
    }

    /**
     * 设置为默认品牌
     * @param brandId 品牌ID
     * @return
     */
    @RequestMapping(value = "/setTheDefault")
    public ReturnMessages setTheDefault(
            @RequestParam(value = "brandId",required = true,defaultValue = "")String brandId
    ){
        Brand defaultBrand = brandService.findBrandByTheDefaultTrue();
        if(defaultBrand != null){
            List<Goods> goodsList = goodsService.findByBrandAndDelFalse(defaultBrand);  //查询品牌下的商品
            if(goodsList != null && goodsList.size() > 0){  //原默认品牌下有商品
                return new ReturnMessages(RequestState.ERROR,"原默认品牌下有商品，请移除商品后再修改默认品牌",goodsList);
            }
        }
        Brand brandSearch = brandService.findById(brandId);
        if(brandSearch == null){
            return new ReturnMessages(RequestState.ERROR,"品牌不存在！",null);
        }
        List<Goods> goodsList = goodsService.findByBrandAndDelFalse(brandSearch);  //查询该品牌下的商品
        if(goodsList != null && goodsList.size() > 0){  //该品牌下有商品
            return new ReturnMessages(RequestState.ERROR,"该品牌下有商品，请移除商品后再修改为默认品牌",goodsList);
        }
        Brand brand = brandService.setBrandDefault(brandId);
        if(brand != null){
            return new ReturnMessages(RequestState.SUCCESS,"设置成功。",brand);
        }else{
            return new ReturnMessages(RequestState.ERROR,"设置失败。",null);
        }
    }

}
