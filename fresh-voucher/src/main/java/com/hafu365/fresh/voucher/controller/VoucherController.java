package com.hafu365.fresh.voucher.controller;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.VoucherConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.OrderService;
import com.hafu365.fresh.service.role.RoleService;
import com.hafu365.fresh.service.voucher.VoucherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代金券
 * Created by HuangWeizhen on 2017/8/29.
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RoleService roleService;

    /**
     * 添加代金券
     * @param money         代金券金额
     * @param userName      代金券所属用户
     * @param description   代金券描述 [可空]
     * @param effectiveTime 代金券生效时间
     * @param indate        代金券使用期限
     * @return
     */
    @RequestMapping("/add")
    public ReturnMessages addVoucher(@RequestParam(name = "money",required = true)String money,
                                     @RequestParam(name = "userName",required = true)String userName,
                                     @RequestParam(name = "description",required = false)String description,
                                     @RequestParam(name = "effectiveTime",required = true)String effectiveTime,
                                     @RequestParam(name = "indate",required = true)String indate,
                                     HttpServletRequest request
                                     ){


        String userNameStr = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userNameStr)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userNameStr);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

        }
        List<Role> roleList = roleService.findRoleByMember(currentMember);
        String roleCodeStr = "";
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }
        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //只有管理员可以添加

        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }

        Voucher voucher = new Voucher();
        //设置金额
        if(StringUtils.isNotEmpty(money)){
            double moneyNum = 0d;
            try{
                moneyNum = Double.valueOf(money);

            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券金额参数有误！",null);
            }
            if(moneyNum < 0){
                return new ReturnMessages(RequestState.ERROR,"代金券金额不能为负数！",null);
            }else{
                voucher.setMoney(moneyNum);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券金额不能为空！",null);
        }
        //设置状态
        voucher.setState(VoucherConstant.VOUCHER_STATE_ON_CHECKING.toString());
        //设置所属用户
        if(StringUtils.isNotEmpty(userName)){
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){
                voucher.setMember(member);
            }else{
                return new ReturnMessages(RequestState.ERROR,"用户不存在！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券所属用户不能为空！",null);
        }
        //设置代金券描述
        if(StringUtils.isNotEmpty(description)){
            voucher.setDescription(description);
        }
        //设置生效时间
        if(StringUtils.isNotEmpty(effectiveTime)){
            try{
                voucher.setEffectiveTime(Long.valueOf(effectiveTime));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券生效时间参数有误！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券生效时间不能为空！",null);
        }
        //设置使用期限(失效时间)
        if(StringUtils.isNotEmpty(indate)){
            try{
                voucher.setIndate(Long.valueOf(indate));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券使用期限参数有误！",null);
            }

        }

        //添加log代码
        VoucherLog log = new VoucherLog();
        log.setOperation(VoucherConstant.VOUCHER_LOG_CREATE.toString());
        log.setOperationTime(System.currentTimeMillis());

        log.setMember(currentMember);
        log.setDescription("新增log");

        Voucher voucherRes = voucherService.save(voucher, log);

        if(voucherRes != null){
            return new ReturnMessages(RequestState.SUCCESS,"添加成功！",voucherRes);
        }else{
            return new ReturnMessages(RequestState.ERROR,"添加失败！",null);
        }
    }

    /**
     * 编辑代金券
     * @param voucherId     代金券id
     * @param money         代金券金额[可空]
     * @param memberId      代金券所属用户[可空]
     * @param description   代金券描述 [可空]
     * @param effectiveTime 代金券生效时间[可空]
     * @param indate        代金券使用期限[可空]
     * @return
     */
    @RequestMapping("/edit")
    public ReturnMessages editVoucher(@RequestParam(name = "voucherId",required = true)String voucherId,
                                      @RequestParam(name = "money",required = false)String money,
                                      @RequestParam(name = "memberId",required = false)String memberId,
                                      @RequestParam(name = "description",required = false)String description,
                                      @RequestParam(name = "effectiveTime",required = false)String effectiveTime,
                                      @RequestParam(name = "indate",required = false)String indate,
                                      HttpServletRequest request
    ){

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userName);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

        }
        //获取用户角色
        List<Role> roleList = roleService.findRoleByMember(currentMember);
        String roleCodeStr = "";
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }

        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //只有管理员有权限编辑

        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }
        Voucher voucher = new Voucher();
        Voucher voucherSearch = null;

        if(StringUtils.isNotEmpty(voucherId)){
            voucherSearch = voucherService.findById(voucherId);
            if(voucherSearch != null){
                String voucherState = voucherSearch.getState();
                if(voucherState.equals(VoucherConstant.VOUCHER_STATE_USED) || voucherState.equals(VoucherConstant.VOUCHER_STATE_OVERDUE)){

                    return new ReturnMessages(RequestState.ERROR,"已使用的和已过期的代金券不能编辑！",null);
                }else{
                    BeanUtils.copyProperties(voucherSearch, voucher);
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券不存在！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券参数有误！",null);
        }

        //设置金额
        if(StringUtils.isNotEmpty(money)){
            double moneyNum = 0d;
            try{
                moneyNum = Double.valueOf(money);

            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券金额参数有误！",null);
            }
            if(moneyNum < 0){
                return new ReturnMessages(RequestState.ERROR,"代金券金额不能为负数！",null);
            }else{
                voucher.setMoney(moneyNum);
            }

        }
        //设置所属用户
        if(StringUtils.isNotEmpty(memberId)){
            Member member = memberService.findMemberByMemberId(memberId);
            if(member != null){
                voucher.setMember(member);
            }else{
                return new ReturnMessages(RequestState.ERROR,"用户不存在！",null);
            }

        }
        //设置代金券描述
        if(StringUtils.isNotEmpty(description)){
            voucher.setDescription(description);
        }
        //设置生效时间
        if(StringUtils.isNotEmpty(effectiveTime)){
            try{
                voucher.setEffectiveTime(Long.valueOf(effectiveTime));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券生效时间参数有误！",null);
            }

        }
        //设置使用期限
        if(StringUtils.isNotEmpty(indate)){
            try{
                voucher.setIndate(Long.valueOf(indate));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券使用期限参数有误！",null);
            }

        }
        //设置代金券的状态为待审核状态
        voucher.setState(VoucherConstant.VOUCHER_STATE_ON_CHECKING.toString());
        //添加log代码
        VoucherLog log = new VoucherLog();
        log.setOperation(VoucherConstant.VOUCHER_LOG_EDIT.toString());
        log.setOperationTime(System.currentTimeMillis());
        log.setMember(currentMember);
        String beforeDesc = "";
        String afterDesc = "";
        beforeDesc = voucherSearch.getDescription();
        afterDesc = voucher.getDescription();
        String beforeEdit = "编辑前(金额：" + voucherSearch.getMoney() + ";用户：" + voucherSearch.getMember().getUsername()
                            + ";生效时间：" + voucherSearch.getEffectiveTime() + "；失效时间：" + voucherSearch.getIndate()
                            + ";代金券描述：" + beforeDesc + ")";
        String afterEdit = ";  编辑后(金额：" + voucher.getMoney() + ";用户：" + voucher.getMember().getUsername()
                            + ";生效时间：" + voucher.getEffectiveTime() + ";失效时间：" + voucher.getIndate()
                            + ";代金券描述：" + afterDesc + ")";
//        System.out.println(beforeEdit);
//        System.out.println(afterEdit);
        log.setDescription(beforeEdit + afterEdit);
//        log.setDescription("编辑代金券");
        Voucher voucherRes = voucherService.update(voucher, log);
        if(voucherRes != null){
            return new ReturnMessages(RequestState.SUCCESS,"编辑成功！",voucherRes);
        }else{
            return new ReturnMessages(RequestState.ERROR,"编辑失败！",null);
        }
    }


    /**
     * 审核代金券
     * @param voucherId     代金券id
     * @param result        代金券状态["success","failure"]
     * @return
     */
    @RequestMapping("/check")
    public ReturnMessages checkVoucher(@RequestParam(name = "voucherId",required = true)String voucherId,
                                      @RequestParam(name = "result",required = true)String result,
                                       HttpServletRequest request
    ){

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userName);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

        }
        List<Role> roleList = roleService.findRoleByMember(currentMember);
        String roleCodeStr = "";
        if(roleList != null && roleList.size() > 0){
            for(Role role : roleList){
                String roleCode = role.getRoleCode();
                roleCodeStr = roleCodeStr + roleCode + ",";
            }

        }
        if(roleCodeStr.contains("ROLE:ADMIN")){ //只有管理员有权限审核

        }else{
            return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
        }
        Voucher voucher = new Voucher();
        VoucherLog log = new VoucherLog();

        if(StringUtils.isNotEmpty(voucherId)){
            Voucher voucherSearch = voucherService.findById(voucherId);
            if(voucherSearch != null){
                String state = "";
                state = voucherSearch.getState();
                if(!state.equals(VoucherConstant.VOUCHER_STATE_ON_CHECKING.toString())){
                    return new ReturnMessages(RequestState.ERROR,"代金券不是待审核状态！",null);
                }else{
                    BeanUtils.copyProperties(voucherSearch, voucher);
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券不存在！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券参数有误！",null);
        }

        //设置状态
        if(StringUtils.isNotEmpty(result) && (result.equals("success") || result.equals("failure"))){
            if(result.equals("success")){
                voucher.setState(VoucherConstant.VOUCHER_STATE_CHECK_ON.toString());//审核通过
                log.setDescription("审核通过");
            }else{
                voucher.setState(VoucherConstant.VOUCHER_STATE_CHECK_OFF.toString());//审核不通过
                log.setDescription("审核不通过");
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券状态参数有误！",null);
        }

        //添加log代码

        log.setOperation(VoucherConstant.VOUCHER_LOG_CHECK.toString());
        log.setOperationTime(System.currentTimeMillis());
        log.setMember(currentMember);


        Voucher voucherRes = voucherService.save(voucher, log);

        if(voucherRes != null){
            return new ReturnMessages(RequestState.SUCCESS,"审核成功！",voucherRes);
        }else {
            return new ReturnMessages(RequestState.ERROR,"审核失败！",null);
        }
    }

    /**
     * 使用代金券
     * @param voucherId     代金券id
     * @param ordersId      订单id
     * @return
     */
    @RequestMapping("/use")
    public ReturnMessages useVoucher(@RequestParam(name = "voucherId",required = true)String voucherId,
                                     @RequestParam(name = "ordersId",required = true)String ordersId,
                                     HttpServletRequest request
    ){

        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userName);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);
        }

        Voucher voucher = new Voucher();
        VoucherLog log = new VoucherLog();
        if(StringUtils.isNotEmpty(voucherId)){
            Voucher voucherSearch = voucherService.findById(voucherId);
            if(voucherSearch != null){
                if(!voucherSearch.getState().equals(VoucherConstant.VOUCHER_STATE_CHECK_ON.toString())){

                    return new ReturnMessages(RequestState.ERROR,"代金券未通过审核！",null);
                }else{
                    long nowTime = System.currentTimeMillis();
                    long startTime = voucherSearch.getEffectiveTime();
                    long endTime = voucherSearch.getIndate();
                    if(nowTime > startTime && nowTime < endTime){

                        Member voucherMember = voucherSearch.getMember();
                        if(voucherMember != null){
                            if(voucherMember.getMemberId().equals(currentMember.getMemberId())){ //判断用户和代金券用户id是否一样

                            }else{
                                return new ReturnMessages(RequestState.ERROR,"只能使用自己的代金券！",null);
                            }

                        }else{
                            return new ReturnMessages(RequestState.ERROR,"该代金券没有用户！",null);
                        }
                        BeanUtils.copyProperties(voucherSearch, voucher);
                    }else{
                        return new ReturnMessages(RequestState.ERROR,"代金券不在使用期限内！",null);
                    }
                }

            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券不存在！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"代金券参数有误！",null);
        }

        Orders orders = null;
        if(StringUtils.isNotEmpty(ordersId)){
            orders = orderService.findOrdersByordersId(ordersId);
            if(orders != null){
                log.setDescription("使用代金券");
                voucher.setState(VoucherConstant.VOUCHER_STATE_USED.toString());

                log.setOperation(VoucherConstant.VOUCHER_LOG_USED.toString());
                log.setOrders(orders);
                log.setOperationTime(System.currentTimeMillis());

            }else{
                return new ReturnMessages(RequestState.ERROR,"订单不存在！",null);

            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"订单参数有误！",null);
        }

        log.setMember(currentMember);

        Voucher voucherRes = voucherService.save(voucher, log);

        if(voucherRes != null){

            return new ReturnMessages(RequestState.SUCCESS,"使用成功！",voucherRes);
        }else{
            return new ReturnMessages(RequestState.ERROR,"使用失败！",null);
        }
    }

    /**
     * 删除代金券
     * @param voucherId     代金券id
     * @return
     */
    @RequestMapping("/delete")
    public ReturnMessages deleteVoucher(@RequestParam(name = "voucherId",required = true)String voucherId,
                                        HttpServletRequest request
    ){
        //获取当前用户
        String userName = SecurityUtils.getUsername(request);
        if(!StringUtils.isNotEmpty(userName)){   //用户未登录
            return new ReturnMessages(RequestState.ERROR, "未登录，没有权限！", null);
        }
        Member currentMember = memberService.findMemberByUsername(userName);
        if(currentMember == null){ //用户不存在
            return new ReturnMessages(RequestState.ERROR, "用户不存在，没有权限！", null);

        }
        Voucher voucher = voucherService.findById(voucherId);
        if(voucher != null){
            Member member = voucher.getMember();
            if(member != null){
                if(member.getMemberId().equals(currentMember.getMemberId())){ //是自己的代金券,可以删除

                }else if(!(voucher.getState().equals(VoucherConstant.VOUCHER_STATE_CHECK_ON.toString()))){ //代金为非正常状态，管理员可以删除
                    //获取用户角色
                    List<Role> roleList = roleService.findRoleByMember(currentMember);
                    String roleCodeStr = "";
                    if(roleList != null && roleList.size() > 0){
                        for(Role role : roleList){
                            String roleCode = role.getRoleCode();
                            roleCodeStr = roleCodeStr + roleCode + ",";
                        }

                    }
                    if(roleCodeStr.contains("ROLE:ADMIN")){

                    }else{
                        return new ReturnMessages(RequestState.ERROR,"没有权限！",null);
                    }

                }else{
                    return new ReturnMessages(RequestState.SUCCESS,"没有权限！",null);
                }

            }else{
                return new ReturnMessages(RequestState.SUCCESS,"该代金券没有用户！",null);
            }
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"代金券不存在！",null);
        }

        if(voucherService.delete(voucherId)){
            return new ReturnMessages(RequestState.SUCCESS,"删除成功！",null);
        }else{
            return new ReturnMessages(RequestState.ERROR,"删除失败！",null);
        }
    }

    /**
     * 根据id查询代金券
     * @param voucherId     代金券id
     * @return
     */
    @RequestMapping("/findById")
    public ReturnMessages findVoucherById(@RequestParam(name = "voucherId",required = true)String voucherId
    ){

        if(StringUtils.isNotEmpty(voucherId)){
            Voucher voucher = voucherService.findById(voucherId);
            if(voucher != null){
                return new ReturnMessages(RequestState.SUCCESS,"查询成功！",voucher);
            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券不存在！",null);
            }


        }else{
            return new ReturnMessages(RequestState.ERROR,"参数有误！",null);
        }

    }

    /**
     * 查询用户自己的代金券
     * @return
     */
    @RequestMapping("/findMyVoucher")
    public ReturnMessages findMyVoucher(HttpServletRequest request
    ){
        String userName = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(userName);

        List<Voucher> voucherList = voucherService.findUserVouchers(member);

        if(voucherList != null && voucherList.size() > 0){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",voucherList);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",null);
        }

    }

    /**
     * 根据条件分页查询代金券
     * @param voucherId     代金券id[可空]
     * @param money         代金券金额[可空]
     * @param state         代金券状态[可空]["VOUCHER_STATE_ON_CHECKING","VOUCHER_STATE_CHECK_ON","VOUCHER_STATE_CHECK_OFF","VOUCHER_STATE_OVERDUE","VOUCHER_STATE_USED"]
     * @param userName      代金券用户名[可空]
     * @param description   代金券描述 [可空]
     * @param isIndate      是否在生效时间内[可空]["0","1","2","3"][0表示未到期查询，1表示在期限时间内查询，2表示过期查询,3表示不设期限条件查询,默认为3]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"effectiveTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findVouchers")
    public ReturnMessages findVouchers(@RequestParam(name = "voucherId",required = false)String voucherId,
                                       @RequestParam(name = "money",required = false)String money,
                                       @RequestParam(name = "state",required = false)String state,
                                       @RequestParam(name = "userName",required = false)String userName,
                                       @RequestParam(name = "description",required = false)String description,
                                       @RequestParam(name = "isIndate",required = false)String isIndate,
                                       @RequestParam(name = "pageNum",required = false)String pageNum,
                                       @RequestParam(name = "pageSize",required = false)String pageSize,
                                       @RequestParam(name = "pageSort",required = false)String pageSort,
                                       @RequestParam(name = "sortDirection",required = false)String sortDirection
    ){

        Map<String,Object> paramMap = new HashMap<String,Object>();
        Voucher voucher = new Voucher();

        if(StringUtils.isNotEmpty(voucherId)){
            Voucher voucherSearch = voucherService.findById(voucherId);
            if(voucherSearch != null){
                voucher.setVoucherId(voucherId);
            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券不存在！",null);
            }

        }

        //设置金额
        if(StringUtils.isNotEmpty(money)){
            try{
                voucher.setMoney(Long.valueOf(money));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"代金券金额参数有误！",null);
            }

        }
        //设置状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(VoucherConstant.VOUCHER_STATE_ON_CHECKING.toString()) || state.equals(VoucherConstant.VOUCHER_STATE_CHECK_ON.toString()) || state.equals(VoucherConstant.VOUCHER_STATE_CHECK_OFF.toString()) || state.equals(VoucherConstant.VOUCHER_STATE_OVERDUE.toString()) || state.equals(VoucherConstant.VOUCHER_STATE_USED.toString())){
                voucher.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券状态参数有误！",null);
            }
        }
        //设置所属用户
        if(StringUtils.isNotEmpty(userName)){
            Member member = memberService.findMemberByUsername(userName);
            if(member != null){
                voucher.setMember(member);
            }else{
                return new ReturnMessages(RequestState.ERROR,"用户不存在！",null);
            }

        }
        //设置代金券描述
        if(StringUtils.isNotEmpty(description)){
            voucher.setDescription(description);
        }
        paramMap.put("voucher",voucher);

        //设置是否在使用期限内的条件
        if(StringUtils.isNotEmpty(isIndate)){
            if(isIndate.equals("0") || isIndate.equals("1") || isIndate.equals("2")){

            }else{
                return new ReturnMessages(RequestState.ERROR,"期限条件参数有误!",null);
            }
        }else{
            isIndate = "3";
        }
        paramMap.put("isIndate",isIndate);


        //设置查询分页
        UtilPage page = new UtilPage(0,5,"effectiveTime", Sort.Direction.DESC);
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

        paramMap.put("page",page);

        Page<Voucher> voucherPage = voucherService.findByCondition(paramMap);
        if(voucherPage != null && voucherPage.getContent() != null && voucherPage.getContent().size() > 0){

            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",voucherPage);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",voucherPage);
        }
    }
}
