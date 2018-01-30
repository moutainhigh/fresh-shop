package com.hafu365.fresh.service.order.impl;

import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.order.MakeOrderRepository;
import com.hafu365.fresh.service.order.MakeOrderService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 预约订单 逻辑层
 * Created by zhaihuilin on 2017/7/21  14:42.
 */
@Log4j
@Service
@Transactional
public class MakeOrderServiceImpl implements MakeOrderService {


    @Autowired
    private MakeOrderRepository makeOrderRepository;

    /**
     * 新增
     * @param makeOrder
     * @return
     */
    @Override
    public MakeOrder saveMakeOrder(MakeOrder makeOrder) {
        return makeOrderRepository.save(makeOrder);
    }

    /**
     * 编辑
     * @param makeOrder
     * @return
     */
    @Override
    public MakeOrder updateMakeOrder(MakeOrder makeOrder) {
        return makeOrderRepository.save(makeOrder);
    }

    /**
     * 根据预约订单编号查询
     * @param MakeOrderId
     * @return
     */
    @Override
    public MakeOrder findMakeOrderByMakeOrderId(String MakeOrderId) {
        return makeOrderRepository.findMakeOrderByMakeOrderId(MakeOrderId);
    }

    /**
     * 根据预约订单编号进行删除
     * @param makeOrderId
     * @return
     */
    @Override
    public boolean deleteMakeOrderByMakeOrderId(String makeOrderId) {
        try {
            makeOrderRepository.deleteMakeOrderByMakeOrderId(makeOrderId);
            return  true;
        }catch (Exception e){
            log.info("删除失败的原因:"+e.getMessage());
            return false;
        }
    }

    /**
     * 条件动态查询
     * @param makeOrder    预约订单
     * @param username       所属用户
     * @param CstartDate   创建开始时间
     * @param CendDate     创建结束时间
     * @param UstartDate   编辑开始时间
     * @param UendDate     编辑结束时间
     * @param pageable
     * @return
     */
    @Override
    public Page<MakeOrder> findAllMakeOrder(MakeOrder makeOrder, String username, Long CstartDate, Long CendDate, Long UstartDate, Long UendDate, Pageable pageable) {
        return makeOrderRepository.findAll(MakeOrderWhere(makeOrder,username,CstartDate,CendDate,UstartDate,UendDate),pageable);
    }

    /**
     * 查询用户的预订单信息
     * @param username
     * @return
     */
    @Override
    public MakeOrder findMakeOrderByMember(String username) {
        return makeOrderRepository.findMakeOrderByUsername(username);
    }

    /**
     * 根据用户
     * @param username
     * @param pageable
     * @return
     */
    @Override
    public Page<MakeOrder> findAllMakeOrderByMember(String username, Pageable pageable) {
        return makeOrderRepository.findAll(MakeOrderWhereByMember(username),pageable);
    }

    /**
     * 获取所有的预订单列表信息
     * @return
     */
    @Override
    public List<MakeOrder> getMakeOrderList() {
        List<MakeOrder> makeOrderList=makeOrderRepository.findAll();
        return makeOrderList;
    }

    /**
     * 条件动态查询
     * @param makeOrder   预约订单
     * @param username    所属用户
     * @param CstartDate   创建开始时间
     * @param CendDate     创建结束时间
     * @param UstartDate   编辑开始时间
     * @param UendDate     编辑结束时间
     * @return
     */
    public static Specification<MakeOrder> MakeOrderWhere(
            final MakeOrder makeOrder,
            final String username,
            final Long CstartDate,
            final Long CendDate,
            final Long UstartDate,
            final Long UendDate
    ){
        return  new Specification<MakeOrder>() {
            @Override
            public Predicate toPredicate(Root<MakeOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                /**
                 * 预约订单本身
                 */
                if (makeOrder.getMakeOrderId()!=null && ! makeOrder.getMakeOrderId().equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("makeOrderId"),makeOrder.getMakeOrderId()));
                }
                /**
                 * 所属用户
                 */
                if (username !=null && StringUtils.isNotEmpty(username)){
                    predicates.add(criteriaBuilder.equal(root.<String>get("username"),username));
                }
                /**
                 * 创建时间
                 */
                if (CstartDate != null && CendDate !=null && (CstartDate <= CendDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),CstartDate,CendDate));
                }else if(CstartDate !=null && CendDate ==null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),CstartDate,new Date().getTime()));
                }else if (CstartDate ==null&& CendDate != null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),0l,CendDate));
                }
                /**
                 * 编辑时间
                 */
                if (UstartDate != null && UendDate !=null && (UstartDate <= UendDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("updateTime"),UstartDate,UendDate));
                }else if(UstartDate !=null && UendDate ==null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("updateTime"),UstartDate,new Date().getTime()));
                }else if (UstartDate ==null&& UendDate != null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("updateTime"),0l,UendDate));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }


    /**
     * 条件动态查询
     * @param username    所属用户
     * @return
     */
    public static Specification<MakeOrder> MakeOrderWhereByMember(
            final String username
    ){
        return  new Specification<MakeOrder>() {
            @Override
            public Predicate toPredicate(Root<MakeOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                /**
                 * 所属用户
                 */
                if (username !=null && StringUtils.isNotEmpty(username)){
                    predicates.add(criteriaBuilder.equal(root.<String>get("username"),username));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }





}
