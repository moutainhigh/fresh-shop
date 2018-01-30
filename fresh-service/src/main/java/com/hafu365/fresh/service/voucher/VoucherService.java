package com.hafu365.fresh.service.voucher;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 代金券业务接口
 * Created by HuangWeizhen on 2017/8/29.
 */
public interface VoucherService {

    /**
     * 代金券保存 or 审核 与log的添加
     * @param voucher
     * @return
     */
    Voucher save(Voucher voucher, VoucherLog log);

    /**
     * 代金券更新
     * @param voucher
     * @return
     */
    Voucher update(Voucher voucher, VoucherLog log);

    /**
     * 删除代金券
     * @param voucherId
     */
    boolean delete(String voucherId);

    /**
     * 根据id查询代金券
     * @param voucherId
     * @return
     */
    Voucher findById(String voucherId);

    /**
     * 根据条件分页查询代金券
     * @param paramMap
     * @return
     */
    Page<Voucher> findByCondition(Map<String,Object> paramMap);

    /**
     * 查询审核通过且过期的代金券（定时任务用）
     * @return
     */
    List<Voucher> findOverDueVoucher(long time);

    /**
     * 查询用户代金券
     * @param member
     * @return
     */
    List<Voucher> findUserVouchers(Member member);

    /**
     * 使用代金券
     * @param voucherId
     * @return
     */
    boolean useVoucher(String voucherId, HttpServletRequest request);
}
