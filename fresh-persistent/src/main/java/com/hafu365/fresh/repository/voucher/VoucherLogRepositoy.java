package com.hafu365.fresh.repository.voucher;

import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 代金券操作记录持久层
 * Created by HuangWeizhen on 2017/8/29.
 */
@Repository
public interface VoucherLogRepositoy extends JpaRepository<VoucherLog,Long>, JpaSpecificationExecutor<VoucherLog>{

    /**
     * 分页查询代金券操作记录
     * @param voucherLogSpecification
     * @param pageable
     * @return
     */
    Page<VoucherLog> findAll(Specification<VoucherLog> voucherLogSpecification, Pageable pageable);

    /**
     * 根据代金券删除代金券操作记录
     * @param voucher
     */
    void deleteByVoucher(Voucher voucher);
}
