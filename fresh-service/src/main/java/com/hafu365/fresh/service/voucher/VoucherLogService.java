package com.hafu365.fresh.service.voucher;

import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * 代金券操作记录业务接口
 * Created by HuangWeizhen on 2017/8/29.
 */
public interface VoucherLogService {

    /**
     * 保存与更新代金券操作记录
     * @param voucherLog
     * @return
     */
    VoucherLog save(VoucherLog voucherLog);

    /**
     * 删除代金券操作记录
     * @param logId
     */
    boolean delete(long logId);

    /**
     * 根据id查询代金券记录
     * @param logId
     * @return
     */
    VoucherLog findById(long logId);

    /**
     * 根据条件分页查询代金券操作记录
     * @param paramMap
     * @return
     */
    Page<VoucherLog> findByCondition(Map<String,Object> paramMap);
}
