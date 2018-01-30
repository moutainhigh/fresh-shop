package com.hafu365.fresh.goods.controller.goods;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsClass;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.GoodsClassService;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.role.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类(只有管理员可以添加，编辑和删除分类)
 * Created by HuangWeizhen on 2017/8/1.
 */
@Slf4j
@RestController
@RequestMapping("/gc")
public class GoodsClassController {
    @Autowired
    private GoodsClassService gcService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RoleService roleService;


    /**
     * 添加分类(可以添加重名的分类，同个父级分类下不能添加重名的（及与父级同名的）；一级分类需要上传图片)
     * @param classTitle    分类标题
     * @param parentClassId 父级分类id[可空]
     * @param pics          分类图片集合[可空]
     * @param keywords      分类关键字[可空]
     * @param gcShow        分类显示状态[可空]["true","false"]
     * @param orderNum      分类排序[可空]默认0
     * @return
     */
    @RequestMapping("/save")
    public ReturnMessages saveGoodsClass(
                                             @RequestParam(name = "classTitle",required = true)String classTitle,
                                             @RequestParam(name = "parentClassId",required = false)String parentClassId,
                                             @RequestParam(name = "pics",required = false)String pics,
                                             @RequestParam(name = "keywords",required = false)String keywords,
                                             @RequestParam(name = "gcShow",required = false)String gcShow,
                                             @RequestParam(name = "orderNum",required = false,defaultValue = "0")int orderNum,
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
        if (!roleCodeStr.contains("ROLE:ADMIN")) { //不是管理员，没有权限添加商品分类
            return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
        }


        ReturnMessages rm = new ReturnMessages();
        GoodsClass gc = new GoodsClass();
        //分类标题
        if(StringUtils.isNotEmpty(classTitle)){
            //分类名称可以重复
            /*GoodsClass gc_search = gcService.findByName(classTitle);
            if(gc_search != null){//分类名称一样不能保存
                return new ReturnMessages(RequestState.ERROR,"分类名已存在，请修改名称！",null);
            }*/
            gc.setClassTitle(classTitle);
        }else{
            return new ReturnMessages(RequestState.ERROR,"分类名称不能为空！",null);
        }

        //父级分类
        if(StringUtils.isNotEmpty(parentClassId)){
            GoodsClass oldClass = gcService.findById(parentClassId);
            if(oldClass != null){
                gc.setOldClass(oldClass);
            }else{
                return new ReturnMessages(RequestState.ERROR,"父级分类不存在！",null);
            }

        }
        //添加（父级分类下不能添加相同名称的子分类且不能和父类名称相同；一级分类下不能添加相同名称的分类）
        if(gc.getOldClass() != null){   //是二级分类
            GoodsClass oldClass = gc.getOldClass();
            if(oldClass.getClassTitle().equals(gc.getClassTitle())){   //和父级分类名称相同！
                return new ReturnMessages(RequestState.ERROR,"子类名称不能和父类名称相同！",null);
            }
            if(parentHasThisChild(oldClass, gc.getClassTitle())){
                return new ReturnMessages(RequestState.ERROR,"该父级分类已存在该类名！",null);
            }
        }else{  //是一级分类
            if(hasThisGc(gc.getClassTitle())){
                return new ReturnMessages(RequestState.ERROR,"一级分类中已存在该类名！",null);
            }
        }

        //分类图片
        if(StringUtils.isNotEmpty(pics)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {}.getType();
            try{
                List<Image> imageList = gson.fromJson(pics,type);
                gc.setClassPic(imageList);

            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分类图片请求参数有误！",null);
            }

        }else{
            if(parentClassId == null){
                return new ReturnMessages(RequestState.ERROR,"添加一级分类需要上传图片！",null);
            }
        }

        //关键字
        if(StringUtils.isNotEmpty(keywords)){
            gc.setKeywords(keywords);
        }

        //分类创建时间
        gc.setCreateTime(System.currentTimeMillis());

        //分类显示状态
        if(StringUtils.isNotEmpty(gcShow)){
            gc.setGcShow(Boolean.valueOf(gcShow));

        }else{
            gc.setGcShow(true);//默认显示
        }
        //分类排序
        gc.setOrderNum(orderNum);

        GoodsClass gcRes = gcService.save(gc);
        if(gcRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"保存成功!",gcRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"保存失败!",null);
        }
        return rm;

    }

    /**
     * 父级分类下是否存在该类名的子类
     * @param oldClass
     * @param classTitle
     * @return
     */
    public boolean parentHasThisChild(GoodsClass oldClass, String classTitle){
        List<GoodsClass> children = gcService.findGcChildren(oldClass);
        if(children != null && children.size() > 0){
            for(GoodsClass child: children){
                if(child != null && child.getClassTitle().equals(classTitle)){  //父级已存在名称相同的子类
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 一级分类下是否存在该类名的子类
     * @param classTitle
     * @return
     */
    public boolean hasThisGc(String classTitle){
        List<GoodsClass> gcList = gcService.findGcList();
        if(gcList != null && gcList.size() >0){
            for(GoodsClass gc : gcList){
                if(gc != null && gc.getClassTitle().equals(classTitle)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 编辑分类
     * @param classId           分类id
     * @param classTitle        分类标题[可空]
     * @param parentClassId     父级分类id[可空]
     * @param pics              分类图片集合[可空]
     * @param keywords          分类关键字[可空]
     * @param gcShow            分类显示状态[可空]["true","false"]
     * @param orderNum          分类排序[可空]
     * @return
     */
    @RequestMapping("/update")
    public ReturnMessages updateGoodsClass(@RequestParam(name = "classId",required = true)String classId,
                                           @RequestParam(name = "classTitle",required = false)String classTitle,
                                           @RequestParam(name = "parentClassId",required = false)String parentClassId,
                                           @RequestParam(name = "pics",required = false)String pics,
                                           @RequestParam(name = "keywords",required = false)String keywords,
                                           @RequestParam(name = "gcShow",required = false)String gcShow,
                                           @RequestParam(name = "orderNum",required = false,defaultValue = "-1")int orderNum,
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
        if (!roleCodeStr.contains("ROLE:ADMIN")) { //不是管理员，没有权限编辑商品分类
            return new ReturnMessages(RequestState.ERROR, "没有权限！", null);
        }

        ReturnMessages rm = new ReturnMessages();
        GoodsClass gc = new GoodsClass();
        GoodsClass gcSearch = null;
        if(StringUtils.isNotEmpty(classId)){
            gcSearch = gcService.findById(classId);
            if(gcSearch == null){
                return new ReturnMessages(RequestState.ERROR,"分类不存在！",null);
            }else{
                BeanUtils.copyProperties(gcSearch,gc);
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"分类参数有误！",null);
        }


        //分类标题
        if(StringUtils.isNotEmpty(classTitle) && !(gcSearch.getClassTitle().equals(classTitle))){   //分类名称改变
            //分类名称可以重复
            /*GoodsClass gcSearch = gcService.findByName(classTitle);
            if(gcSearch != null && !(gcSearch.getClassId().equals(classId))){//分类名称一样不能保存
                return new ReturnMessages(RequestState.ERROR,"分类名已存在，请修改名称！",null);
            }*/
            gc.setClassTitle(classTitle);
        }

        //父级分类
        if(StringUtils.isNotEmpty(parentClassId)){
            GoodsClass oldClass = gcService.findById(parentClassId);
            if(oldClass != null ){
                if(gcSearch.getOldClass() == null){ //原来为空，一级分类(当分类下有子类时，不能添加父级分类)
                    List<GoodsClass> children = gcService.findGcChildren(gcSearch);
                    if(children != null && children.size() > 0){    //有子类
                        return new ReturnMessages(RequestState.ERROR,"有子类，不能编辑为二级分类！",null);
                    }
                    gc.setOldClass(oldClass);
                }else if(!gcSearch.getOldClass().getClassId().equals(parentClassId)){ //原来不为空，且不相等
                    gc.setOldClass(oldClass);
                }else{  //不为空，不改变
                    //do nothing
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"父级分类不存在！",null);
            }

        }

        //添加（同级分类下不能添加相同名称的分类）
        if(gc.getOldClass() != null){   //是二级分类
            GoodsClass oldClass = gc.getOldClass();
            if(oldClass.getClassTitle().equals(gc.getClassTitle())){   //和父级分类名称相同！
                return new ReturnMessages(RequestState.ERROR,"子类名称不能和父类名称相同！",null);
            }
            List<GoodsClass> children = gcService.findGcChildren(oldClass);
            if(children != null && children.size() > 0){
                for(GoodsClass child: children){
                    if(child != null && child.getClassTitle().equals(classTitle) && !(child.getClassId().equals(classId))){  //父级已存在名称相同的子类(去除自己)
                        return new ReturnMessages(RequestState.ERROR,"该父级分类已存在该类名！",null);
                    }
                }
            }
        }else{  //是一级分类
            List<GoodsClass> gcList = gcService.findGcList();
            if(gcList != null && gcList.size() >0){
                for(GoodsClass gc1 : gcList){
                    if(gc1 != null && gc1.getClassTitle().equals(classTitle) && !(gc1.getClassId().equals(classId))){    //一级分类中已存在该类名（去除自己）
                        return new ReturnMessages(RequestState.ERROR,"一级分类中已存在该类名！",null);
                    }
                }
            }
        }


        //分类图片
        if(StringUtils.isNotEmpty(pics)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {}.getType();
            try{
                List<Image> imageList = gson.fromJson(pics,type);
                gc.setClassPic(imageList);
                gc.setPics(pics);

            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分类图片请求参数有误！",null);
            }

        }

        //关键字
        if(StringUtils.isNotEmpty(keywords)){
            gc.setKeywords(keywords);
        }

        //分类更新时间
        gc.setUpdateTime(System.currentTimeMillis());

        //分类显示状态
        if(StringUtils.isNotEmpty(gcShow)){
            gc.setGcShow(Boolean.valueOf(gcShow));

        }
        //分类排序
        if(orderNum != -1){
            gc.setOrderNum(orderNum);
        }

        GoodsClass gcRes = gcService.update(gc);
        if(gcRes != null){
            rm = new ReturnMessages(RequestState.SUCCESS,"更新成功!",gcRes);
        }else{
            rm = new ReturnMessages(RequestState.ERROR,"更新失败!",null);
        }

        return rm;
    }

    /**
     * 逻辑删除分类
     * @param classId   分类id
     * @return
     */
    @RequestMapping("/delete")
    public ReturnMessages deleteGcByClassId(@RequestParam(name = "classId",required = false)String classId,
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
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,只有管理员可以删除分类

        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }
        GoodsClass gc = gcService.findById(classId);
        if(gc != null){
            List<GoodsClass> childList = gcService.findGcChildren(gc);
            if(childList != null && childList.size() > 0){  //有子分类先删除子分类
                return new ReturnMessages(RequestState.ERROR,"分类有子类，请删除子类后再删除!",childList);

            }else{  //无子分类
                List<Goods> goodsList = goodsService.findByGoodsClass(gc);
                if(goodsList != null && goodsList.size() > 0){  //有商品，先删除商品再删除分类
                    return new ReturnMessages(RequestState.ERROR,"分类有商品，请删除商品后再删除!",goodsList);
                }else{
                    if(gcService.deleteByClassId(classId)){
                        return new ReturnMessages(RequestState.SUCCESS,"删除成功！",null);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"删除失败!",null);
                    }

                }

            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"分类不存在！",null);
        }

    }

    /**
     * 物理删除分类
     * @param classId   分类id
     * @return
     */
    @RequestMapping("/physicalDelete")
    public ReturnMessages physicalDeleteGc(@RequestParam(name = "classId",required = false)String classId,
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
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //是管理员,可以删除分类

        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }
        GoodsClass gc = gcService.findById(classId);
        if(gc != null){
            List<GoodsClass> childList = gcService.findGcChildren(gc);
            if(childList != null && childList.size() > 0){  //有子分类先删除子分类
                return new ReturnMessages(RequestState.ERROR,"分类有子类，请删除子类后再删除!",childList);

            }else{  //无子分类
                List<Goods> goodsList = goodsService.findByGoodsClass(gc);
                if(goodsList != null && goodsList.size() > 0){  //有商品，先删除商品再删除分类
                    return new ReturnMessages(RequestState.ERROR,"分类有商品，请删除商品后再删除!",goodsList);
                }else{
                    if(gcService.physicalDelete(classId)){
                        return new ReturnMessages(RequestState.SUCCESS,"删除成功！",null);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"删除失败!",null);
                    }

                }

            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"分类不存在！",null);
        }
    }

    /**
     * 查询显示的分类列表
     * @return
     */
    @RequestMapping("/findShowGcList")
    public ReturnMessages findShowGcList(){
        List<GoodsClass> gc_return = gcService.findShowGcList();
        if(gc_return != null && gc_return.size() > 0){

            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",gc_return);
        }else{

            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",null);
        }
    }

    /**
     * 查询分类列表(包括显示与不显示)
     * @return
     */
    @RequestMapping("/findGcList")
    public ReturnMessages findGcList(){

        List<GoodsClass> gc_return = gcService.findGcList();
        if(gc_return != null && gc_return.size() > 0){

            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",gc_return);
        }else{

            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",null);
        }
    }


    /**
     * 分类详情（根据分类id查询分类）
     * @param gcId
     * @return
     */
    @RequestMapping("/findGc")
    public ReturnMessages findGc(@RequestParam (name = "classId") String gcId){

        GoodsClass gc_return = gcService.findById(gcId);
        if(gc_return != null){

            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",gc_return);
        }else{

            return new ReturnMessages(RequestState.SUCCESS,"分类不存在！",null);
        }

    }

}
