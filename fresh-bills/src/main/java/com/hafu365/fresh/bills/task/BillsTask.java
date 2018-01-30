package com.hafu365.fresh.bills.task;

import com.hafu365.fresh.core.entity.bills.Bills;
import com.hafu365.fresh.core.entity.common.CommonDateUtils;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import com.hafu365.fresh.service.bills.BillService;
import com.hafu365.fresh.service.bills.BillsOperationService;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.member.MemberSettingService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务
 * Created by SunHaiyang on 2017/9/7.
 */
@Component
@Log4j
public class BillsTask {

    @Autowired
    MemberService memberService;

    @Autowired
    BillsOperationService billsOperationService;

    @Autowired
    BillService billService;

    @Autowired
    MemberSettingService memberSettingService;

    /**
     * 账单定时任务
     */
    public void ClearingBillsTask(){
        List<MemberSetting> memberSettings = memberSettingService.findMemberSettingBysettlementDate(CommonDateUtils.getDay());
        for (MemberSetting memberSetting:memberSettings){
            String username = memberSetting.getMember().getUsername();
            Bills bills = billsOperationService.clearingBillsByUsername(username);
            log.info("ClearingBillsTask: "+username);
        }
        VerifyBillsTask();
    }

    /**
     * 校验付账状态
     */
    public void VerifyBillsTask(){
        List<MemberSetting> memberSettings = memberSettingService.findMemberSettingBySettlementInterval(CommonDateUtils.getDay());
        for (MemberSetting memberSetting : memberSettings){
            String username = memberSetting.getMember().getUsername();
            MemberInfo memberInfo = billsOperationService.VerifyBillsTask(username);
            if(memberInfo != null) {
                log.info("VerifyBillsTask:" + memberInfo.getUsername() + " - " + memberInfo.getState());
            }
        }
    }

}
