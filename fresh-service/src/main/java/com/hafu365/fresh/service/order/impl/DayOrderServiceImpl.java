package com.hafu365.fresh.service.order.impl;

import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.entity.order.SimpleDayOrder;
import com.hafu365.fresh.core.utils.Constants;
import com.hafu365.fresh.repository.order.DayOrderRepository;
import com.hafu365.fresh.service.order.DayOrderService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 天订单 逻辑层
 * Created by zhaihuilin on 2017/7/21  14:23.
 */
@Transactional
@Service
@Log4j
public class DayOrderServiceImpl implements DayOrderService, Constants {

    @Autowired
    private DayOrderRepository dayOrderRepository;


    /**
     * 添加天订单
     * @param dayOrder
     * @return
     */
    @Override
    public DayOrder saveDayOrders(DayOrder dayOrder) {
        log.info(dayOrder.toString());
        return dayOrderRepository.save(dayOrder);
    }

    /**
     * 根据天订单编号进行删除
     * @param DayOrderId
     * @return
     */
    @Override
    public boolean deleteDayOrdersByDayOrderId(String DayOrderId) {
        try {
            dayOrderRepository.deleteDayOrderByDayOrderId(DayOrderId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 编辑天订单信息
     * @param dayOrder
     * @return
     */
    @Override
    public DayOrder updateDayOrders(DayOrder dayOrder) {
        log.info(dayOrder.getGoodsVoList().toString());
        Gson gson = new Gson();
        int row = dayOrderRepository.updateDayOrder(dayOrder.getDayOrderId(), gson.toJson(dayOrder.getGoodsVoList()),dayOrder.getDayOrderState());
        if(row == 1){
            log.info(true);
        }
        return dayOrder;
    }

    /**
     * 编辑天订单信息
     * @param dayOrder
     * @return
     */
    @Override
    public DayOrder update(DayOrder dayOrder) {
        return dayOrderRepository.save(dayOrder);
    }

    /**
     * 根据 天订单编号进行查询
     * @param dayOrderId
     * @return
     */
    @Override
    public DayOrder findDayOrderByDayOrderId(String dayOrderId) {
        return dayOrderRepository.findDayOrderByDayOrderId(dayOrderId);
    }
    /**
     * 根据预约时间查询天订单信息列表
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<DayOrder> findDayOrderBydeliverTime(Long startDate, Long endDate) {
        return dayOrderRepository.findAll(DayOrderWhereTime(startDate,endDate));
    }

    /**
     * 根据预订单来获取天订单信息
     * @param makeOrder
     * @return
     */
    @Override
    public List<SimpleDayOrder> findSimpleDayOrderByMakeOrder(MakeOrder makeOrder) {
        return dayOrderRepository.findSimpleDayOrOrderByMakeOrder(makeOrder);
    }

    /**
     * 新增根据预订单来获取天订单集合信息
     * @param makeOrder
     * @return
     */
    @Override
    public List<DayOrder> findDayOrderByMakeOrder(MakeOrder makeOrder) {
        return dayOrderRepository.findDayOrderByMakeOrder(makeOrder);
    }

    @Override
    public List<DayOrder> findDayOrderByDeliverTime(long time) {
        return dayOrderRepository.findDayOrdersByDeliverTime(time);
    }

    /**
     * 天订单条件查询
     * @param startDate        预定开始时间
     * @param endDate          预定结束时间
     * @return
     */
    public static Specification<DayOrder>DayOrderWhere(
            final DayOrder dayOrder,
            final Long startDate,
            final Long endDate
    ){
        return new Specification<DayOrder>() {
            @Override
            public Predicate toPredicate(Root<DayOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (dayOrder.getDayOrderId() !=null && !dayOrder.getDayOrderId().equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("dayOrderId"),dayOrder.getDayOrderId()));
                }
                if (dayOrder.getDayOrderState()>0){
                    predicates.add(criteriaBuilder.equal(root.<String>get("dayOrderState"),String.valueOf(dayOrder.getDayOrderState())));
                }
                if (startDate != null && endDate !=null && (startDate <= endDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("deliverTime"),startDate,endDate));
                }else if(startDate !=null && endDate ==null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("deliverTime"),startDate,new Date().getTime()));
                }else if (startDate ==null&& endDate != null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("deliverTime"),0l,endDate));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }

    /**
     * 时间   -----》天订单条件查询
     * @param startDate        预定开始时间
     * @param endDate          预定结束时间
     * @return
     */
    public static Specification<DayOrder>DayOrderWhereTime(
            final Long startDate,
            final Long endDate
    ){
        return new Specification<DayOrder>() {
            @Override
            public Predicate toPredicate(Root<DayOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (startDate != 0 && endDate != 0 && (startDate <= endDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("deliverTime"),startDate,endDate));
                }else if(startDate != 0 && endDate ==0){
                    predicates.add(criteriaBuilder.between(root.<Long>get("deliverTime"),startDate,new Date().getTime()));
                }else if (startDate == 0 && endDate != 0){
                    predicates.add(criteriaBuilder.between(root.<Long>get("deliverTime"),0l,endDate));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }


}
