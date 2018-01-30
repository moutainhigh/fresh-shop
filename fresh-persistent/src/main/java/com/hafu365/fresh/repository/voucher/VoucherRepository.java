package com.hafu365.fresh.repository.voucher;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代金券持久层
 * Created by HuangWeizhen on 2017/8/29.
 */
@Repository
public interface VoucherRepository extends JpaRepository<Voucher,String>, JpaSpecificationExecutor<Voucher>{

    /**
     * 分页查询代金券
     * @param voucherSpecification
     * @param pageable
     * @return
     */
    Page<Voucher> findAll(Specification<Voucher> voucherSpecification, Pageable pageable);

    /**
     * 查询审核通过且过期的代金券（定时任务用）
     * @return
     */
    @Query(value = "SELECT v.* from fresh_voucher v where v.state = 'VOUCHER_STATE_CHECK_ON' and v.indate < ?1",nativeQuery = true)
    List<Voucher> findOverDueVoucher(long time);

    /**
     * 查询用户代金券
     * @param member
     * @return
     */
    List<Voucher> findByMemberAndStateAndEffectiveTimeLessThanEqualAndIndateGreaterThanEqual(Member member,String state,long timeStart, long timeEnd);

    Voucher findByVoucherId(String id);
}
