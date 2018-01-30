package com.hafu365.fresh.service.voucher.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.voucher.VoucherLogRepositoy;
import com.hafu365.fresh.service.voucher.VoucherLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 代金券操作记录业务接口
 * Created by HuangWeizhen on 2017/8/29.
 */
@Transactional
@Service
public class VoucherLogServiceImpl implements VoucherLogService {

    @Autowired
    private VoucherLogRepositoy logRepositoy;

    @Override
    public VoucherLog save(VoucherLog voucherLog) {
        return logRepositoy.save(voucherLog);
    }

    @Override
    public boolean delete(long logId) {
        try{
            logRepositoy.delete(logId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public VoucherLog findById(long logId) {
        return logRepositoy.getOne(logId);
    }

    @Override
    public Page<VoucherLog> findByCondition(Map<String, Object> paramMap) {
        //获取参数
        final VoucherLog log = (VoucherLog) paramMap.get("log");
        final long startTime = (Long) paramMap.get("startTime");
        final long endTime = (Long) paramMap.get("endTime");
        final UtilPage page = (UtilPage)paramMap.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);

        return logRepositoy.findAll(new Specification<VoucherLog>() {
            @Override
            public Predicate toPredicate(Root<VoucherLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //设置logId
                if(log.getLogId() != 0){
                    predicates.add(cb.equal(root.<Long>get("logId"),log.getLogId()));
                }
                //设置voucher
                if(log.getVoucher() != null){
                    predicates.add(cb.equal(root.<String>get("voucher"),log.getVoucher()));

                }
                //设置operation
                if(StringUtils.isNotEmpty(log.getOperation())){
                    predicates.add(cb.equal(root.<String>get("operation"),log.getOperation()));
                }
                //设置order
                if(log.getOrders() != null){
                    predicates.add(cb.equal(root.<String>get("orders"),log.getOrders()));
                }
                //设置操作时间范围
                Predicate p_opera_time = null;
                if(startTime != 0 && endTime != 0 && (startTime < endTime)){
                    p_opera_time = cb.between(root.<Long>get("operationTime"),startTime,endTime);
                }else if(startTime != 0 && endTime == 0){
                    p_opera_time = cb.between(root.<Long>get("operationTime"),startTime,System.currentTimeMillis());

                }else if(startTime == 0 && endTime != 0){
                    p_opera_time = cb.between(root.<Long>get("operationTime"),0l,endTime);
                }else{
                    p_opera_time = null;
                }
                if(p_opera_time != null){
                    predicates.add(p_opera_time);
                }

                //设置用户
                if(log.getMember() != null){
                    predicates.add(cb.equal(root.<String>get("member"),log.getMember()));
                }
                //设置描述模糊查询
                if(StringUtils.isNotEmpty(log.getDescription())){
                    predicates.add(cb.like(root.<String>get("description"),"%" + log.getDescription() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }
}
