package com.hafu365.fresh.repository.bills;

import com.hafu365.fresh.core.entity.bills.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 交易信息持久层
 * Created by SunHaiyang on 2017/10/12.
 */
public interface PayInfoRepository extends JpaRepository<PayInfo,Long> {

    /**
     * 通过交易单号查询
     * @param paySn 交易单号
     * @return
     */
    public PayInfo findByPaySn(String paySn);
}
