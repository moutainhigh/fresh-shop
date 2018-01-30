package com.hafu365.fresh.service.voucher.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.VoucherConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.voucher.VoucherLogRepositoy;
import com.hafu365.fresh.repository.voucher.VoucherRepository;
import com.hafu365.fresh.service.voucher.VoucherService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 代金券业务接口
 * Created by HuangWeizhen on 2017/8/29.
 */
@Transactional
@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private VoucherLogRepositoy logRepositoy;

    @Override
    public Voucher save(Voucher voucher, VoucherLog log) {
        Voucher voucherRes = voucherRepository.save(voucher);
        log.setVoucher(voucherRes);
        logRepositoy.save(log);//添加保存记录
        return voucherRes;
    }


    @Override
    public Voucher update(Voucher voucher, VoucherLog log) {
        Voucher voucherRes = voucherRepository.save(voucher);
        log.setVoucher(voucherRes);
        logRepositoy.save(log);
        return voucherRes;
    }

    @Override
    public boolean delete(String voucherId) {
        try{
            Voucher voucher = voucherRepository.getOne(voucherId);
            logRepositoy.deleteByVoucher(voucher);//删除该代金券对应的操作记录
            voucherRepository.delete(voucherId);//删除代金券
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Voucher findById(String voucherId) {
        return voucherRepository.getOne(voucherId);
    }

    @Override
    public Page<Voucher> findByCondition(Map<String, Object> paramMap) {
        final Voucher voucher = (Voucher)paramMap.get("voucher");
        final String isIndate = (String)paramMap.get("isIndate");
        final UtilPage page = (UtilPage)paramMap.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);

        return voucherRepository.findAll(new Specification<Voucher>() {
            @Override
            public Predicate toPredicate(Root<Voucher> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //拼接voucherId
                if(StringUtils.isNotEmpty(voucher.getVoucherId())){
                    predicates.add(cb.equal(root.<String>get("voucherId"),voucher.getVoucherId()));
                }
                //拼接金额查询
                if(voucher.getMoney() != 0){
                    predicates.add(cb.equal(root.<Long>get("money"),voucher.getMoney()));

                }
                //拼接状态查询
                if(StringUtils.isNotEmpty(voucher.getState())){
                    predicates.add(cb.equal(root.get("state"),voucher.getState()));
                }
                //拼接所属用户查询
                if(voucher.getMember() != null){
                    predicates.add(cb.equal(root.<String>get("member"),voucher.getMember()));
                }
                //拼接描述模糊查询
                if(StringUtils.isNotEmpty(voucher.getDescription())){
                    predicates.add(cb.like(root.<String>get("description"),"%" + voucher.getDescription() + "%"));
                }

                //拼接使用期限内查询
                long nowTime = System.currentTimeMillis();
                if(isIndate.equals("0")){//未到期查询
                    predicates.add(cb.greaterThan(root.<Long>get("effectiveTime"),nowTime));

                }else if(isIndate.equals("1")){//期限内查询

                    predicates.add(cb.lessThanOrEqualTo(root.<Long>get("effectiveTime"),nowTime));
                    predicates.add(cb.greaterThanOrEqualTo(root.<Long>get("indate"),nowTime));
                }else if(isIndate.equals("2")){//过期查询
                    predicates.add(cb.lessThan(root.<Long>get("indate"),nowTime));

                }else{
                    //不设此条件查询
                }


                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    @Override
    public List<Voucher> findOverDueVoucher(long time) {
        return voucherRepository.findOverDueVoucher(time);
    }

    @Override
    public List<Voucher> findUserVouchers(Member member) {
        long time = System.currentTimeMillis();
        return voucherRepository.findByMemberAndStateAndEffectiveTimeLessThanEqualAndIndateGreaterThanEqual(member,VoucherConstant.VOUCHER_STATE_CHECK_ON.toString(),time,time);
    }

    @Override
    public boolean useVoucher(String voucherId, HttpServletRequest request) {
        Voucher voucher = voucherRepository.findByVoucherId(voucherId);
        String username = SecurityUtils.getUsername(request);
        Member member = voucher.getMember();
        if(member.getUsername().equals(username)){
            if(voucher.getState().equals(VoucherConstant.VOUCHER_STATE_CHECK_ON.toString())){
                voucher.setState(VoucherConstant.VOUCHER_STATE_USED.toString());
                voucherRepository.save(voucher);
                VoucherLog voucherLog = new VoucherLog();
                voucherLog.setVoucher(voucher);
                voucherLog.setMember(member);
                voucherLog.setDescription("使用代金券");
                voucherLog.setOperation(VoucherConstant.VOUCHER_LOG_USED.toString());
                voucherLog.setOperationTime(new Date().getTime());
                logRepositoy.save(voucherLog);
                return true;
            }
        }
        return false;
    }

}
