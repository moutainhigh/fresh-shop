package com.hafu365.fresh.service.bills.impl;

import com.hafu365.fresh.core.entity.bills.PayInfo;
import com.hafu365.fresh.repository.bills.PayInfoRepository;
import com.hafu365.fresh.service.bills.PayInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by SunHaiyang on 2017/10/12.
 */
@Service
public class PayInfoServiceImpl implements PayInfoService {
    @Autowired
    PayInfoRepository payInfoRepository;

    @Override
    public PayInfo savePayInfo(PayInfo payInfo) {
        return payInfoRepository.save(payInfo);
    }

    @Override
    public PayInfo findPayInfoByPaySn(String paySn) {
        return payInfoRepository.findByPaySn(paySn);
    }

    @Override
    public Page<PayInfo> findPayInfoAll(Pageable pageable) {
        return payInfoRepository.findAll(pageable);
    }

    @Override
    public boolean existPayInfo(String paySn) {
        return payInfoRepository.findByPaySn(paySn) != null;
    }
}
