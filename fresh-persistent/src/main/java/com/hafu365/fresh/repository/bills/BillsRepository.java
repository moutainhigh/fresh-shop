package com.hafu365.fresh.repository.bills;

import com.hafu365.fresh.core.entity.bills.Bills;
import com.hafu365.fresh.core.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.Mapping;

import java.util.List;

/**
 * 账单持久层
 * Created by SunHaiyang on 2017/8/25.
 */
@Repository
public interface BillsRepository extends JpaRepository<Bills,String>,JpaSpecificationExecutor<Bills> {

    /**
     * 查询用户所有账单
     * @param member
     * @return
     */
    public List<Bills> findAllByMember(Member member);

    /**
     * 通过Id查询订单
     * @param id
     * @return
     */
    public Bills findById(String id);

    /**
     * 通过用户和期号获取账单
     * @param member
     * @param issue
     * @return
     */
    public Bills findByMemberAndIssue(Member member,String issue);


}
