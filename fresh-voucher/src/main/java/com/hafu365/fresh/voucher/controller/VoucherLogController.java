package com.hafu365.fresh.voucher.controller;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.VoucherConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.OrderService;
import com.hafu365.fresh.service.voucher.VoucherLogService;
import com.hafu365.fresh.service.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 代金券操作记录
 * Created by HuangWeizhen on 2017/8/29.
 */
@RestController
@RequestMapping("/voucherLog")
public class VoucherLogController {

    @Autowired
    private VoucherLogService logService;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberService memberService;

    /**
     * 根据id查询代金券操作记录
     * @param logId     代金券id
     * @return
     */
    @RequestMapping("/findById")
    public ReturnMessages findVoucherLogById(@RequestParam(name = "logId",required = true)String logId
    ){

        ReturnMessages rm = new ReturnMessages();

        if(StringUtils.isNotEmpty(logId)){
            long voucherLogId = 0;
            try{
                voucherLogId = Long.valueOf(logId);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"参数格式有误！",null);
            }

            VoucherLog log = logService.findById(voucherLogId);
            if(log != null){

                return new ReturnMessages(RequestState.SUCCESS,"查询成功！",log);
            }else{
                return new ReturnMessages(RequestState.ERROR,"操作记录不存在！",null);
            }


        }else{
            return new ReturnMessages(RequestState.ERROR,"参数有误！",null);
        }

    }

    /**
     * 根据条件分页查询操作记录
     * @param logId         记录id[可空]
     * @param voucherId     代金券id[可空]
     * @param operation     操作类型[可空]["VOUCHER_LOG_CREATE","VOUCHER_LOG_EDIT","VOUCHER_LOG_CHECK","VOUCHER_LOG_USED","VOUCHER_LOG_CLOSE"]
     * @param ordersId      订单id[可空]
     * @param memberId      用户id[可空]
     * @param description   记录描述[可空]
     * @param startTime     起始时间[可空][操作时间区间查询]
     * @param endTime       结束时间[可空][操作时间区间查询]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"operationTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findByCondition")
    public ReturnMessages findByCondition(@RequestParam(name = "logId",required = false)String logId,
                                          @RequestParam(name = "voucherId",required = false)String voucherId,
                                          @RequestParam(name = "operation",required = false)String operation,
                                          @RequestParam(name = "ordersId",required = false)String ordersId,
                                          @RequestParam(name = "memberId",required = false)String memberId,
                                          @RequestParam(name = "description",required = false)String description,
                                          @RequestParam(name = "startTime",required = false)String startTime,
                                          @RequestParam(name = "endTime",required = false)String endTime,
                                          @RequestParam(name = "pageNum",required = false)String pageNum,
                                          @RequestParam(name = "pageSize",required = false)String pageSize,
                                          @RequestParam(name = "pageSort",required = false)String pageSort,
                                          @RequestParam(name = "sortDirection",required = false)String sortDirection
    ){

        Map<String,Object> paramMap = new HashMap<String,Object>();
        VoucherLog log = new VoucherLog();

        //设置记录id
        if(StringUtils.isNotEmpty(logId)){
            long voucherLogId = 0;
            try{
                voucherLogId = Long.valueOf(logId);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"参数格式有误！",null);
            }

            VoucherLog logRes = logService.findById(voucherLogId);
            if(log != null){
                log.setLogId(voucherLogId);
            }else{
                return new ReturnMessages(RequestState.ERROR,"操作记录不存在！",null);
            }
        }

        //设置代金券
        if(StringUtils.isNotEmpty(voucherId)){
            Voucher voucher = voucherService.findById(voucherId);
            if(voucher != null){
                log.setVoucher(voucher);

            }else{
                return new ReturnMessages(RequestState.ERROR,"代金券不存在！",null);
            }
        }
        //设置操作类型
        if(StringUtils.isNotEmpty(operation)){
            if(operation.equals(VoucherConstant.VOUCHER_LOG_CREATE.toString()) || operation.equals(VoucherConstant.VOUCHER_LOG_EDIT.toString()) || operation.equals(VoucherConstant.VOUCHER_LOG_CHECK.toString()) || operation.equals(VoucherConstant.VOUCHER_LOG_USED.toString()) || operation.equals(VoucherConstant.VOUCHER_LOG_CLOSE.toString())){
                log.setOperation(operation);
            }else{
                return new ReturnMessages(RequestState.ERROR,"操作记录不存在！操作记录状态参数有误！",null);
            }
        }
        //设置订单号
        if(StringUtils.isNotEmpty(ordersId)){
            Orders orders = orderService.findOrdersByordersId(ordersId);
            if(orders != null){
                log.setOrders(orders);

            }else{
                return new ReturnMessages(RequestState.ERROR,"订单不存在！",null);
            }
        }
        //设置用户
        if(StringUtils.isNotEmpty(memberId)){
            Member member = memberService.findMemberByMemberId(memberId);
            if(member != null){
                log.setMember(member);
            }else{
                return new ReturnMessages(RequestState.ERROR,"用户不存在！",null);
            }
        }
        //设置描述
        if(StringUtils.isNotEmpty(description)){
            log.setDescription(description);
        }
        //
        paramMap.put("log", log);

        //设置创建时间条件查询
        long sTime = 0l;
        if(StringUtils.isNotEmpty(startTime)){
            try{
                sTime = Long.valueOf(startTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"起始时间参数格式有误!",null);
            }
        }
        paramMap.put("startTime",sTime);
        long eTime = 0l;
        if(StringUtils.isNotEmpty(endTime)){
            try{
                eTime = Long.valueOf(endTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"结束时间参数格式有误!",null);
            }
        }
        paramMap.put("endTime",eTime);

        //设置查询分页
        UtilPage page = new UtilPage(0,5,"operationTime", Sort.Direction.DESC);
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

        Page<VoucherLog> logPage = logService.findByCondition(paramMap);
        if(logPage != null && logPage.getContent() != null && logPage.getContent().size() > 0){

            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",logPage);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",logPage);
        }
    }

}
