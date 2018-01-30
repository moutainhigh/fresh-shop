package com.hafu365.fresh.service.bills;

import com.hafu365.fresh.core.entity.bills.PayInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 交易信息逻辑层
 * Created by SunHaiyang on 2017/10/12.
 */
public interface PayInfoService {
    /**
     * 添加交易信息
     * @param payInfo
     * @return
     */
    public PayInfo savePayInfo(PayInfo payInfo);

    /**
     * 通过交易单号查询交易信息
     * @param paySn
     * @return
     */
    public PayInfo findPayInfoByPaySn(String paySn);

    /**
     * 分页查询交易信息
     * @param pageable
     * @return
     */
    public Page<PayInfo> findPayInfoAll(Pageable pageable);

    /**
     * 查询是否使用交易单号
     * @param paySn
     * @return
     */
    public boolean existPayInfo(String paySn);
}
