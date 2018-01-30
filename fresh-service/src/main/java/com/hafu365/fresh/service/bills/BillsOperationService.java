package com.hafu365.fresh.service.bills;

import com.hafu365.fresh.core.entity.bills.Bills;
import com.hafu365.fresh.core.entity.bills.BillsInfo;
import com.hafu365.fresh.core.entity.common.CommonDateUtils;
import com.hafu365.fresh.core.entity.constant.BillsConstant;
import com.hafu365.fresh.core.entity.constant.BillsInfoConstant;
import com.hafu365.fresh.core.entity.constant.CommonConstant;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.member.MemberInfoService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.member.MemberSettingService;
import com.hafu365.fresh.service.voucher.VoucherService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 账单工具类
 * Created by SunHaiyang on 2017/9/4.
 */
@Service
@Log4j
public class BillsOperationService {

    @Autowired
    MemberService memberService;

    @Autowired
    BillService billService;

    @Autowired
    BillsInfoService billsInfoService;

    @Autowired
    MemberSettingService memberSettingService;

    @Autowired
    VoucherService voucherService;

    @Autowired
    MemberInfoService memberInfoService;

    /**
     * 添加用户账单信息
     *
     * @param username    用户名
     * @param money       金额
     * @param type        账单类型(入账|出账)
     * @param order       关联订单(可空)
     * @param description 信息描述
     * @return 返回值
     */
    @Async
    public Bills addBillsInfo(String username, double money, BillsInfoConstant type, Orders order, String description) {
        Member member = null;
        if (StringUtils.isNotEmpty(username)) {
            member = memberService.findMemberByUsername(username);
        }
        if (member == null) {
            return null;
        }
        MemberSetting memberSetting = memberSettingService.findMemberSettingByMember(member);
        if (memberSetting == null) {
            return null;
        }
        Date date = new Date();
        long settlementUnix = CommonDateUtils.getDayUnix(memberSetting.getSettlementDate());
        long nowUnix = date.getTime();
        int monday = CommonDateUtils.getMonday();
        if (!(nowUnix < settlementUnix)) {
            monday++;
        }
        String issue = getIssue(monday);
        Bills bills = billService.findByMemberByIssue(member, issue);
        if (bills == null) {
            bills = new Bills(member, BillsConstant.BILLS_NOT_GENERATED_BILLS.getState());
            bills.setIssue(issue);
            bills = billService.saveBills(bills);
        }
        //创建订单信息
        BillsInfo billsInfo = new BillsInfo();
        billsInfo.setBills(bills);
        billsInfo.setOrder(order);
        billsInfo.setDescription(description);
        billsInfo.setMoney(money);
        billsInfo.setType(type);
        //End
        billsInfo = billsInfoService.save(billsInfo);
        log.info(username + " 账单添加成功.");
        return billsInfo.getBills();
    }

    /**
     * 手动添加账单信息
     *
     * @param billsId     账单Id
     * @param request     Request (主要用来记录添加本次记录的账单信息)
     * @param money       金额
     * @param type        类型(入账|冲账)
     * @param order       关联订单号
     * @param description 描述(手动添加信息,描述不可为空)
     * @return 添加的信息
     */
    @Async
    public BillsInfo manualOperationBillsInfo(String billsId, HttpServletRequest request, double money, BillsInfoConstant type, Orders order, String description) {
        Bills bills = billService.findBillsById(billsId);
        if (bills == null) {
            return null;
        }
        String username = SecurityUtils.getUsername(request);
        if (StringUtils.isNotEmpty(username)) {
            return null;
        }
        if (!StringUtils.isNotEmpty(description)) {
            return null;
        }
        //创建订单信息
        BillsInfo billsInfo = new BillsInfo();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        description += " 用户名: " + username + " 进行本次操作。 时间: " + simpleDateFormat.format(new Date());
        billsInfo.setBills(bills);
        billsInfo.setOrder(order);
        billsInfo.setDescription(description);
        billsInfo.setMoney(money);
        billsInfo.setType(type);
        //End
        billsInfo = billsInfoService.save(billsInfo);
        return billsInfo;
    }


    /**
     * 通过用户名结算账单
     *
     * @param username 结算账单
     * @return
     */
    @Async
    public Bills clearingBillsByUsername(String username) {
        Member member = memberService.findMemberByUsername(username);
        if (member == null) {
            return null;
        }
        int day = CommonDateUtils.getDay();
        MemberSetting memberSetting = memberSettingService.findMemberSettingByMemberAndDay(member, day);
        if (memberSetting == null) {
            return null;
        }
        int monday = CommonDateUtils.getMonday();
        if (day == 1) { //判断今天是否是一号
            monday -= 1; // 如果是一号,那么结账期号应该是上个月.
        }
        String issue = getIssue(monday);
        Bills bills = billService.findByMemberByIssue(member, issue);
        if (bills == null) {
            return null;
        }
        if (bills.getState() != BillsConstant.BILLS_NOT_GENERATED_BILLS.getState()) {
            return null;
        }
        double money = 0.0;
        for (BillsInfo billsInfo : bills.getBillsInfos()) {
            if (billsInfo.getType() == BillsInfoConstant.CHARGE_OFF.getState()) {
                money -= billsInfo.getMoney();
            } else if (billsInfo.getType() == BillsInfoConstant.ENTRY_ACCOUNT.getState()) {
                money += billsInfo.getMoney();
            }
        }
        bills.setMoney(money);
        bills.setGeneratedBillsTime(new Date().getTime());
        bills.setState(BillsConstant.BILLS_NOT_PAYING);
        bills = billService.updateBills(bills);
        log.info(username + (bills == null ? " 账单生成失败。" : " 账单生成成功。￥ " + bills.getMoney()) );
        return bills;
    }

    /**
     * 生成付款签名()
     * @param billsId 账单号
     * @param request Request
     * @param voucherSns 优惠券编码[]
     * @return
     */
    public Bills useVoucher(String billsId, HttpServletRequest request, String[] voucherSns) {
        Bills bills = billService.findBillsById(billsId);
        if(voucherSns == null || voucherSns.length <= 0){
            return bills;
        }
        String username = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(username);
        double voucherMoney = 0.0;
        if (bills == null) {
            return null;
        }
        if (!StringUtils.isNotEmpty(username)) {
            return null;
        }
        for (String voucherSn : voucherSns){
            Voucher voucher = voucherService.findById(voucherSn);
            if(voucherService.useVoucher(voucherSn,request)){
                addBillsInfo(username,voucher.getMoney(),BillsInfoConstant.CHARGE_OFF,null,"使用优惠券："+voucherSn);
                voucherMoney += voucher.getMoney();
            }
        }
        bills = billService.findBillsById(billsId);
        double money = 0.0;
        for (BillsInfo billsInfo : bills.getBillsInfos()) {
            if (billsInfo.getType() == BillsInfoConstant.CHARGE_OFF.getState()) {
                money -= billsInfo.getMoney();
            } else if (billsInfo.getType() == BillsInfoConstant.ENTRY_ACCOUNT.getState()) {
                money += billsInfo.getMoney();
            }
        }
        if(money < 0){
            money = 0.0;
        }
        bills.setMoney(money);
        bills.setCoupon(voucherMoney);
        bills = billService.saveBills(bills);
        if(bills != null){
            return bills;
        }
        return null;
    }


    /**
     * 付款后修改账单状态
     *
     * @param billsId
     * @param money
     * @return
     */
    @Async
    public Bills paymentBills(String billsId, Double money) {
        Bills bills = billService.findBillsById(billsId);
        Member member = bills.getMember();
        if (bills == null) {
            return null;
        }
        bills.setPaymentMoney(money);
        bills.setPaymentTime(new Date().getTime());
        double spareMoney = bills.getMoney() - bills.getPaymentMoney();
        if (spareMoney > 0) {
            String description = "账单编号: " + bills.getId() + " 未结算的余款。";
            addBillsInfo(member.getUsername(), spareMoney, BillsInfoConstant.ENTRY_ACCOUNT, null, description);
        }
        bills = billService.updateBills(bills);
        return bills;
    }

    public MemberInfo VerifyBillsTask(String username){
        MemberInfo memberInfo = memberInfoService.findMemberInfoByUsername(username);
        if(memberInfo == null){
            return null;
        }
        Member member = memberInfo.getMember();
        Bills bills = new Bills();
        bills.setState(BillsConstant.BILLS_NOT_PAYING);
        bills.setMember(member);
        PageRequest pageRequest = new PageRequest(0,10);
        Page<Bills> billsPage = billService.findByWhere(bills,-1,-1,pageRequest);
        if(billsPage.getContent() != null && billsPage.getContent().size() > 0){
            memberInfo.setState(StateConstant.USER_STATE_LOCK_ING);
        }
        return null;
    }

    /**
     * 获取当前日期的期号
     *
     * @return
     */
    @Async
    public String getIssue(int monday) {
        Date date = new Date(CommonDateUtils.getMondayUnix(monday));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        return simpleDateFormat.format(date);
    }

}
