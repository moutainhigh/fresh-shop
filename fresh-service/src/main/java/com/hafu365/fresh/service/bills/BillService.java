package com.hafu365.fresh.service.bills;

import com.hafu365.fresh.core.entity.bills.Bills;
import com.hafu365.fresh.core.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 账单逻辑层
 * Created by SunHaiyang on 2017/8/25.
 */
public interface BillService {

    /**
     * 通过ID 查询账单
     * @param id
     * @return
     */
    public Bills findBillsById(String id);




    /**
     * 保存账单
     * @param bills
     * @return
     */
    public Bills saveBills(Bills bills);

    /**
     * 查询所有账单
     * @param pageable
     * @return
     */
    public Page<Bills> findAll(Pageable pageable);

    /**
     * 更新账单
     * @param bills
     * @return
     */
    public Bills updateBills(Bills bills);

    /**
     * 条件查询账单
     * @param bills
     * @param pageable
     * @return
     */
    public Page<Bills> findByWhere(Bills bills,long startTime,long endTime,Pageable pageable);

    /**
     * 通过期号用户查询账单
     * @param member 用户
     * @param issue 期号
     * @return
     */
    public Bills findByMemberByIssue(Member member,String issue);

}
