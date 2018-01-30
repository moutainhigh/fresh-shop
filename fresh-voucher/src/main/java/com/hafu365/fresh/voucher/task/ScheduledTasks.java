package com.hafu365.fresh.voucher.task;

import com.hafu365.fresh.core.entity.constant.VoucherConstant;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import com.hafu365.fresh.service.voucher.VoucherLogService;
import com.hafu365.fresh.service.voucher.VoucherService;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代金券自动过期定时任务
 * Created by HuangWeizhen on 2017/8/30.
 */
@Configuration
@Component
@Slf4j
@EnableScheduling
@Transactional
public class ScheduledTasks {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherLogService logService;

    public void voucherTasks(){
//        log.info("****进入定时任务");
        List<Voucher> voucherList = voucherService.findOverDueVoucher(System.currentTimeMillis());
        if(voucherList != null && voucherList.size() > 0){
            for(Voucher v : voucherList){
                if(v != null){
                    v.setState(VoucherConstant.VOUCHER_STATE_OVERDUE.toString());//设置代价券状态为过期状态(未使用)
                    VoucherLog log = new VoucherLog();
                    log.setVoucher(v);
                    log.setOperation(VoucherConstant.VOUCHER_LOG_CLOSE.toString());
                    log.setOperationTime(System.currentTimeMillis());
                    log.setDescription("定时任务设置自动过期");
                    voucherService.save(v, log);
                }
            }
        }

    }
}
