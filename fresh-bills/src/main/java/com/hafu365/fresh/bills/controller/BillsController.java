package com.hafu365.fresh.bills.controller;

//import com.hafu365.fresh.bills.config.SecrteKeyConfig;
import com.hafu365.fresh.bills.config.PayConfig;
import com.hafu365.fresh.core.entity.bills.Bills;
import com.hafu365.fresh.core.entity.bills.BillsInfo;
import com.hafu365.fresh.core.entity.bills.PayInfo;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.BillsConstant;
import com.hafu365.fresh.core.entity.constant.BillsInfoConstant;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.utils.*;
import com.hafu365.fresh.service.bills.BillService;
import com.hafu365.fresh.service.bills.BillsOperationService;
import com.hafu365.fresh.service.bills.PayInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.member.MemberSettingService;
import com.hafu365.fresh.service.order.OrderService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 账单控制层
 * Created by SunHaiyang on 2017/8/25.
 */
@RestController
@Log4j
public class BillsController {

    @Autowired
    BillService billService;

    @Autowired
    MemberService memberService;

    @Autowired
    BillsOperationService billsOperation;

    @Autowired
    MemberSettingService settingService;

    @Autowired
    BillsOperationService billsOperationService;

    @Autowired
    OrderService orderService;

    @Autowired
    PayConfig payConfig;

    @Autowired
    PayInfoService payInfoService;

//    @Autowired
//    SecrteKeyConfig secrteKeyConfig;

    /**
     * 手动添加账单
     * @param billsId 账单号
     * @param money 金额
     * @param type 类型{500入账|300冲账}
     * @param orderId 订单号码[可空]
     * @param description 操作描述
     * @return 消息反馈
     */
    @PostMapping(value = "/bills/addInfo")
    public ReturnMessages addBillsInfo(
            @RequestParam(name = "billsId")String billsId,
            HttpServletRequest request,
            @RequestParam(name = "money")double money,
            @RequestParam(name = "type")int type,
            @RequestParam(name = "orderId",required = false)String orderId,
            @RequestParam(name = "description")String description

    ){
        Orders order = null;
        Bills bills = billService.findBillsById(billsId);
        if(bills == null){
            return new ReturnMessages(RequestState.ERROR,"未查找到该账单号。",null);
        }
        if(money > 0){
            return new ReturnMessages(RequestState.ERROR,"金额不可为负数。",null);
        }
        BillsInfoConstant billsInfoConstant = BillsInfoConstant.getBillsInfoConstant(type);
        if(billsInfoConstant == null){
            return new ReturnMessages(RequestState.ERROR,"类别码错误。",null);
        }
        if(StringUtils.isNotEmpty(orderId)){
            order = orderService.findOrdersByordersId(orderId);
        }
        BillsInfo billsInfo = billsOperationService.manualOperationBillsInfo(billsId,
                request,money,billsInfoConstant, order,description);
        if(billsInfo == null){
            return new ReturnMessages(RequestState.ERROR,"查询失败。",null);
        }
        return new ReturnMessages(RequestState.SUCCESS,"查询成功。",billsInfo);
    }

    /**
     * 查询账单
     *
     * @param billsId   账单Id[可空]
     * @param username  用户名[可空]
     * @param state     账单状态[可空] {0:"未出账",50:"已出账",100:"未付款",200:"已付款",300:"账单关闭",400:"账单完成"}
     * @param startTime 区间查询-开始时间[可空]
     * @param endTime   区间查询-结束时间[可空]
     * @param page      页码
     * @param pageSize  页面大小
     * @return 消息反馈
     */
    @PostMapping(value = "/bills/find")
    public ReturnMessages findBills(
            @RequestParam(name = "billsId", required = false) String billsId,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "state", required = false, defaultValue = "-1") int state,
            @RequestParam(name = "startTime", required = false, defaultValue = "-1") long startTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "-1") long endTime,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "20") int pageSize
    ) {
        ReturnMessages returnMessages = null;
        if (StringUtils.isNotEmpty(billsId)) {
            Bills bills = billService.findBillsById(billsId);
            if (bills != null) {
                return new ReturnMessages(RequestState.SUCCESS, "查询成功。", bills);
            } else {
                return new ReturnMessages(RequestState.ERROR, "查询失败。", null);
            }
        }
        Pageable pageable = new PageRequest(page, pageSize);
        Member member = null;
        if(StringUtils.isNotEmpty(username)){
            member = memberService.findMemberByUsername(username);
        }
        Bills bills = new Bills();
        Page<Bills> billsPage = null;
        if (member != null) {
            bills.setMember(member);
        }
        if (state != -1) {
            BillsConstant billsConstant = BillsConstant.getBillsConstant(state);
            if(billsConstant == null){
                return new ReturnMessages(RequestState.ERROR, "未知账单状态。", null);
            }
            bills.setState(billsConstant);
        }else{
            bills.setState(-1);
        }
        if (startTime != -1 && endTime != -1) {
            billsPage = billService.findByWhere(bills, startTime, endTime, pageable);
        } else if (startTime != -1) {
            billsPage = billService.findByWhere(bills, startTime, -1, pageable);
        } else if (endTime != -1) {
            billsPage = billService.findByWhere(bills, -1, endTime, pageable);
        } else {
            billsPage = billService.findByWhere(bills, -1, -1, pageable);
        }
        if (billsPage != null) {
            return new ReturnMessages(RequestState.SUCCESS, "查询成功。", billsPage);
        } else {
            return new ReturnMessages(RequestState.ERROR, "查询失败。", null);
        }

    }


    /**
     * 查询自己的账单
     *
     * @param billsId   账单ID
     * @param state     账单状态 {0:"未出账",50:"已出账",100:"未付款",200:"已付款",300:"账单关闭",400:"账单完成"}
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param page      页码
     * @param pageSize  页面大小
     * @param request   request
     * @return 消息反馈
     */
    @PostMapping("/bills/findMe")
    public ReturnMessages findByMe(
            @RequestParam(name = "billsId", required = false) String billsId,
            @RequestParam(name = "state", required = false, defaultValue = "-1") int state,
            @RequestParam(name = "startTime", required = false, defaultValue = "-1") long startTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "-1") long endTime,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "pageSize", required = false, defaultValue = "20") int pageSize,
            HttpServletRequest request
    ) {
        ReturnMessages returnMessages = null;
        Member member = memberService.findMemberByUsername(SecurityUtils.getUsername(request));
        if (member == null) {
            return new ReturnMessages(RequestState.ERROR, "登录失效，请登录后在使用。", null);
        }
        Bills bills = new Bills();
        bills.setMember(member);
        Page<Bills> billsPage = null;
        if (StringUtils.isNotEmpty(billsId)) {
            bills.setId(billsId);
        }
        if (state != -1) {
            BillsConstant billsConstant = BillsConstant.getBillsConstant(state);
            if(billsConstant == null){
                return new ReturnMessages(RequestState.ERROR, "未知账单状态。", null);
            }
            bills.setState(billsConstant);
        }else{
            bills.setState(state);
        }
        Pageable pageable = new PageRequest(page, pageSize);
        if (startTime != -1 && endTime != -1) {
            billsPage = billService.findByWhere(bills, startTime, endTime, pageable);
        } else if (startTime != -1) {
            billsPage = billService.findByWhere(bills, startTime, -1, pageable);
        } else if (endTime != -1) {
            billsPage = billService.findByWhere(bills, -1, endTime, pageable);
        } else {
            billsPage = billService.findByWhere(bills, -1, -1, pageable);
        }
        if (billsPage.getSize() > 0) {
            returnMessages = new ReturnMessages(RequestState.SUCCESS, "查询成功。", billsPage);
        } else {
            returnMessages = new ReturnMessages(RequestState.ERROR, "查询失败。", null);
        }
        return returnMessages;
    }

    /**
     * 手动收账
     * @param billsId 账单ID
     * @param money 金额
     * @param description 描述
     * @param finish 是否完成本次账单
     * @return
     */
    @PostMapping(value = "/bills/collect")
    public ReturnMessages collect(
            @RequestParam(value = "billsId")String billsId,
            @RequestParam(value = "money")double money,
            @RequestParam(value = "description")String description,
            @RequestParam(value = "finish",required = false,defaultValue = "false")boolean finish,
            HttpServletRequest request
    ){
        Bills bills = billService.findBillsById(billsId);
        String message = null;
        String username = SecurityUtils.getUsername(request);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        if(bills == null){
            return new ReturnMessages(RequestState.ERROR,"未查找到账单信息。",null);
        }
        BillsConstant billsConstant = BillsConstant.getBillsConstant(bills.getState());
        switch (billsConstant){
            case BILLS_CLOSE: message = "账单已关闭。"; break;
            case BILLS_NOT_GENERATED_BILLS: message = "账单未生成。"; break;
            case BILLS_FINISH: message = "账单已完成。"; break;
        }
        if(message != null){
            return new ReturnMessages(RequestState.ERROR,message,null);
        }
        double payMoney = bills.getPaymentMoney();
        payMoney += money;
        bills.setPaymentMoney(payMoney);
        if(payMoney >= bills.getMoney() || finish){
            bills.setState(BillsConstant.BILLS_PAYMENT_HAS_BEEN);
        }
        bills.setPaymentTime(date.getTime());
        String des = bills.getDescription();
        if (StringUtils.isNotEmpty(description)){
            des += description;
        }
        des += "<p>【 "+ username +" 】 " + simpleDateFormat.format(date) + " 收款：￥" + String.format("%.2f",money)+"</p>";
        bills.setDescription(des);
        bills = billService.updateBills(bills);
        if(bills != null){
            return new ReturnMessages(RequestState.SUCCESS,"收账成功。",bills);
        }else{
            return new ReturnMessages(RequestState.ERROR,"收账失败。",null);
        }
    }

    /**
     * 更新账单状态
     *
     * @param billsId 账单ID
     * @param state   账单状态 {0:"未出账",50:"已出账",100:"未付款",200:"已付款",300:"账单关闭",400:"账单完成"}
     * @return 消息反馈
     */
    @PostMapping(value = "/bills/updateState")
    public ReturnMessages updateBills(
            @RequestParam(name = "billsId") String billsId,
            @RequestParam(name = "state") int state
    ) {
        Bills bills = billService.findBillsById(billsId);
        BillsConstant[] billsConstants = BillsConstant.values();
        int stateCode = -1;
        for (BillsConstant billsConstant : billsConstants) {
            if (state == billsConstant.getState()) {
                stateCode = state;
            }
        }
        if (stateCode > -1) {
            bills.setState(stateCode);
            bills = billService.updateBills(bills);
            if (bills != null) {
                return new ReturnMessages(RequestState.SUCCESS, "修改成功。", bills);
            } else {
                return new ReturnMessages(RequestState.ERROR, "修改失败。", bills);
            }
        } else {
            return new ReturnMessages(RequestState.ERROR, "状态码不存在。", null);
        }
    }

    /**
     * 财务审核
     * @param billsId 订单ID
     * @param description 描述
     * @param finish 是否完成
     * @return
     */
    @PostMapping(value = "/bills/check")
    public ReturnMessages checkBills(
            @RequestParam("billsId")String billsId,
            @RequestParam(value = "description",required = false,defaultValue = "")String description,
            @RequestParam(value = "finish",defaultValue = "false")boolean finish,
            HttpServletRequest request
    ){
        Bills bills = billService.findBillsById(billsId);
        String username = SecurityUtils.getUsername(request);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        if(bills == null){
            return new ReturnMessages(RequestState.ERROR,"未查找到账单信息。",null);
        }
        String des = bills.getDescription();
        if (StringUtils.isNotEmpty(description)){
            des += description;
        }
        if(finish){
            des += "<p>【 "+ username +" 】 " + simpleDateFormat.format(date) + " 审核收款：已通过 </p>";
            bills.setState(BillsConstant.BILLS_FINISH);
        }else{
            des += "<p>【 "+ username +" 】 " + simpleDateFormat.format(date) + " 审核收款：未通过 </p>";
        }
        bills.setDescription(des);
        bills = billService.updateBills(bills);
        if (bills != null) {
            return new ReturnMessages(RequestState.SUCCESS, "审核成功。", bills);
        } else {
            return new ReturnMessages(RequestState.ERROR, "审核失败。", bills);
        }
    }

    /**
     * 提交账单生成支付链接
     * @param billsId 账单ID
     * @param voucherIds 优惠券数组
     * @return
     */
    @PostMapping(value = "/bills/submit")
    public ReturnMessages useVoucher(
            @RequestParam("billsId")String billsId,
            @RequestParam(value = "voucherIds" ,required = false,defaultValue = "")String[] voucherIds,
            HttpServletRequest request
    ){
        Bills bills = null;
        if(voucherIds == null || voucherIds.length <= 0){
            bills = billService.findBillsById(billsId);
        }else{
            bills= billsOperationService.useVoucher(billsId,request,voucherIds);
        }
        if(bills!=null){
            if(bills.getState() == BillsConstant.BILLS_NOT_PAYING.getState()){
                double money = bills.getMoney() - bills.getPaymentMoney();
                if(money < 0){
                    money = 0.0;
                }
                Map<String,String> map = new HashMap<String, String>();
                map.put("callBackUrl",payConfig.getCallBackUrl());
                map.put("sn",bills.getId());
                map.put("money",String.format("%.2f",money));
                String sign = MD5Encrypt.buildRequestMysign(map,payConfig.getKey(),"utf-8");
                String url = payConfig.getUrl() + "?callBackUrl="+payConfig.getCallBackUrl()+"&sn="+bills.getId()
                        +"&money="+String.format("%.2f",money)+"&sign="+sign;
                return new ReturnMessages(RequestState.SUCCESS,"付款链接生成成功。",url);
            }
            return new ReturnMessages(RequestState.ERROR,"付款链接生成失败。",null);
        }else{
            return new ReturnMessages(RequestState.ERROR,"付款链接生成失败。",null);
        }
    }


    /**
     * 付款回调
     * @param factPayOID
     * @param totalFee
     * @param paymentBranch
     * @param payStatus
     * @param sign
     * @param orderSn
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/bills/payMent")
    public ReturnMessages payCallBack(
            @RequestParam(value = "factPayOID")String factPayOID,
            @RequestParam(value = "totalFee")Double totalFee,
            @RequestParam(value = "paymentBranch")String paymentBranch,
            @RequestParam(value = "payStatus")String payStatus,
            @RequestParam(value = "sign")String sign,
            @RequestParam(value = "orderSn")String orderSn

    ) throws Exception {
        Map<String,String> map = new HashMap<String, String>();
        map.put("factPayOID",factPayOID);
        map.put("totalFee",String.valueOf(totalFee));
        map.put("paymentBranch",paymentBranch);
        map.put("payStatus",payStatus);
        map.put("orderSn",orderSn);
        String retSign = MD5Encrypt.buildRequestMysign(map,payConfig.getKey(),"utf-8");
        log.info("retSig： " + retSign);
        log.info("sign： " + sign);
        if(sign.equals(retSign)){
            if(payInfoService.existPayInfo(factPayOID)){
                return  new ReturnMessages(RequestState.ERROR,"该交易信息重复反馈。",null);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Bills bills = billService.findBillsById(orderSn);
            if(bills == null){
                return new ReturnMessages(RequestState.ERROR,"订单异常，未查询到订单信息。",null);
            }
            String des = bills.getDescription();
            String description = "";
            double money = bills.getMoney();
            double payMoney = bills.getPaymentMoney();
            if(totalFee != null || totalFee > 0){
                description = "<p>【"+paymentBranch+"】交易单号：("+factPayOID+") 付款 ￥"+String.format("%.2f",totalFee)+" 日期：" +
                        ""+simpleDateFormat.format(new Date())+"</p>";
                des = StringUtils.isNotEmpty(des)?des+=description:description;
                bills.setDescription(des);
                double paymentMoney = payMoney + totalFee;
                bills.setPaymentMoney(paymentMoney);
                if(paymentMoney >= money){
                    bills.setState(BillsConstant.BILLS_FINISH);
                }
                bills = billService.updateBills(bills);
                PayInfo payInfo = new PayInfo();
                payInfo.setBills(bills);
                payInfo.setPaySn(factPayOID);
                payInfoService.savePayInfo(payInfo);
                if(bills != null){
                    return new ReturnMessages(RequestState.SUCCESS,"付款成功。",bills);
                }
            }
        }
        return new ReturnMessages(RequestState.ERROR,"付款失败。",null);
    }


    @PostMapping(value = "/test")
    public Bills test(
            HttpServletRequest request
    ){
        String username = SecurityUtils.getUsername(request);
        Random random = new Random();
        int[] state = {300,500};
        int n = random.nextInt(2);
        Double money = Math.random()*1000;
        String str = String.format("%.2f",money);
        money = Double.valueOf(str);
        BillsInfoConstant billsInfoConstant= BillsInfoConstant.getBillsInfoConstant(state[n]);
        Bills bills = billsOperationService.addBillsInfo(username,money,billsInfoConstant,null,n > 0 ? "入账了呀。":"出账了呀。");
        return bills;
    }

}
