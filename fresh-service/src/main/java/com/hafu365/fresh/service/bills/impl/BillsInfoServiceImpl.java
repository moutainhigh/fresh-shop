package com.hafu365.fresh.service.bills.impl;

import com.hafu365.fresh.core.entity.bills.BillsInfo;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import com.hafu365.fresh.repository.bills.BillsInfoRepository;
import com.hafu365.fresh.repository.member.MemberRepository;
import com.hafu365.fresh.repository.member.MemberSettingRepository;
import com.hafu365.fresh.service.bills.BillsInfoService;
import com.hafu365.fresh.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账单信息
 * Created by SunHaiyang on 2017/8/24.
 */
@Service
public class BillsInfoServiceImpl implements BillsInfoService {

    @Autowired
    BillsInfoRepository billsInfoRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberSettingRepository memberSettingRepository;

    @Override
    public BillsInfo save(BillsInfo billsInfo) {
        return billsInfoRepository.save(billsInfo);
    }

    @Override
    public BillsInfo update(BillsInfo billsInfo) {
        return billsInfoRepository.save(billsInfo);
    }

    @Override
    public boolean delete(String id) {
        boolean flag = false;
        try {
            billsInfoRepository.delete(id);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public BillsInfo findBillsInfoById(String id) {
        return billsInfoRepository.findOne(id);
    }

    @Override
    public Page<BillsInfo> findAll(Pageable pageable) {
        return billsInfoRepository.findAll(pageable);
    }

    @Override
    public List<BillsInfo> findAllByBetween(String memberId) {
        long day = 1000*60*60*24;
        Member member = memberRepository.findMemberByMemberIdAndDelFalse(memberId);
        MemberSetting memberSetting = memberSettingRepository.findByMember(member);
        if(memberSetting != null && memberSetting.getSettlementDate() > 0 && memberSetting.getSettlementInterval() > 0){
           List<BillsInfo> billsInfos = billsInfoRepository.findAllByCreateTimeBetween(memberSetting.getSettlementDate(),memberSetting.getSettlementInterval() * day);
           return billsInfos;
        }
        return null;
    }
}
