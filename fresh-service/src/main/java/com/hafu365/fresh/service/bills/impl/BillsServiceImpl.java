package com.hafu365.fresh.service.bills.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hafu365.fresh.core.entity.bills.Bills;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.bills.BillsRepository;
import com.hafu365.fresh.service.bills.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SunHaiyang on 2017/8/25.
 */
@Service
public class BillsServiceImpl implements BillService {

    @Autowired
    BillsRepository billsRepository;

    @Override
    public Bills findBillsById(String id) {
        return billsRepository.findOne(id);
    }

    @Override
    public Bills saveBills(Bills bills) {
        return billsRepository.save(bills);
    }

    @Override
    public Page<Bills> findAll(Pageable pageable) {
        return billsRepository.findAll(pageable);
    }

    @Override
    public Bills updateBills(Bills bills) {
        return billsRepository.save(bills);
    }

    @Override
    public Page<Bills> findByWhere(Bills bills,long startTime,long endTime, Pageable pageable) {
        return billsRepository.findAll(where(bills,startTime,endTime),pageable);
    }

    @Override
    public Bills findByMemberByIssue(Member member, String issue) {
        return billsRepository.findByMemberAndIssue(member,issue);
    }

    /**
     * 条件删选
     * @param bills
     * @return
     */
    public Specification<Bills> where(
            final Bills bills,
            final long startTime,
            final long endTime
    ){
        return new Specification<Bills>() {
            @Override
            public Predicate toPredicate(Root<Bills> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(bills.getId())){
                    predicates.add(cb.equal(root.<String>get("id"),bills.getId()));
                }
                if(bills.getMember() != null){
                    predicates.add(cb.equal(root.<Member>get("member"),bills.getMember()));
                }
                if(bills.getState() >= 0){
                    predicates.add(cb.equal(root.<Integer>get("state"),bills.getState()));
                }
                if (startTime > 0){
                    predicates.add(cb.greaterThanOrEqualTo(root.<Long>get("generatedBillsTime"),startTime));
                }
                if (endTime > 0){
                    predicates.add(cb.lessThanOrEqualTo(root.<Long>get("generatedBillsTime"),endTime));
                }
                query.orderBy(cb.desc(root.<Long>get("generatedBillsTime")));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }
}
