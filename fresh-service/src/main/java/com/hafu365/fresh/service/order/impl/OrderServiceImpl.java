package com.hafu365.fresh.service.order.impl;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.OrderDaddress;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.order.OrderRepository;
import com.hafu365.fresh.service.order.OrderService;
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
 * 订单 逻辑层
 * Created by zhaihuilin on 2017/7/21  14:48.
 */
@Log4j
@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;


    /**
     * 新增
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public Orders saveOrders(Orders orders) {
        return orderRepository.save(orders);
    }

    /**
     * 编辑
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public Orders updateOrders(Orders orders) {
        return orderRepository.save(orders);
    }

    /**
     * 根据 订单编号进行查询
     * @param ordersId
     * @return
     */
    @Override
    public Orders findOrdersByordersId(String ordersId) {
        return orderRepository.findOrdersByOrdersId(ordersId);
    }

    /**
     * 根据 订单编号进行查询  未删除状态
     * @param ordersId
     * @return
     */
    @Override
    public Orders findOrdersByOrdersIdAndDelFalse(String ordersId) {
        return orderRepository.findOrdersByOrdersIdAndDelFalse(ordersId);
    }

    /**
     *  根据 订单编号进行删除  逻辑删除
     * @param ordersId
     * @return
     */
    @Override
    @Transactional
    public boolean deleteOrderByordersId(String ordersId) {
        try {
            orderRepository.deleteOrdersByOrdersId(ordersId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 根据 订单编号进行删除  物理删除
     * @param ordersId
     * @return
     */
    @Override
    @Transactional
    public boolean physicallyDeleteByordersId(String ordersId) {
        orderRepository.delete(ordersId);
        return true;
    }

    /**
     * 订单信息 动态条件查询
     * @param username   用户
     * @param store    店铺
     * @param orderDaddress   配送地址
     * @param orders   订单
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @return
     */
    @Override
    public Page<Orders> findOrders(String username, Store store,OrderDaddress orderDaddress, Orders orders, Long startDate, Long endDate, Pageable pageable) {
        return orderRepository.findAll(OrdersWhere(username,store,orderDaddress,orders,startDate,endDate),pageable);
    }

    /**
     * 订单信息   根据店铺和订单的状态进行查询
     * @param store  店铺
     * @param orders   订单
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @param pageable
     * @return
     */
    @Override
    public Page<Orders> findOrdersByStore(Orders orders, Store store, Long startDate, Long endDate, Pageable pageable) {
        return orderRepository.findAll(OrdersWhereByStore(orders,store,startDate,endDate),pageable);
    }

    /**
     * 获取商家自己的店铺下的订单
     * @param orders 订单
     * @param store  店铺
     * @param username 用户
     * @param orderDaddress 配送地址
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @param pageable
     * @return
     */
    @Override
    public Page<Orders> findStoreMeOrdersByStore(String username, OrderDaddress orderDaddress, Orders orders, Store store, Long startDate, Long endDate, Pageable pageable) {
        return orderRepository.findAll(getMeOrdersWhereByStore(username,orderDaddress,orders,store,startDate,endDate),pageable);
    }

    /**
     * 获取用户自己的订单信息
     * @param username   用户
     * @param pageable    分页
     * @return
     */
    @Override
    public Page<Orders> getMeOrders(String username, Pageable pageable) {
        return orderRepository.findOrdersByUsernameAndDelFalse(username,pageable);
    }

    /**
     * 订单信息 动态条件查询
     * @param username   用户
     * @return
     */
    public static Specification<Orders> getMeWhere(
            final  String username
    ){
        return new Specification<Orders>() {
            @Override
            public Predicate toPredicate(Root<Orders> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(username)){
                    predicates.add(criteriaBuilder.equal(root.<Member>get("username"),username));
                }
                predicates.add(criteriaBuilder.equal(root.<String>get("del"),false));
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }


    /**
     * 订单信息 动态条件查询
     * @param username   用户
     * @param store   店铺
     * @param orderDaddress   配送地址
     * @param orders   订单
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @return
     */
    public static Specification<Orders> OrdersWhere(
         final  String username,
         final  Store store,
         final  OrderDaddress orderDaddress,
         final  Orders orders,
         final  Long startDate,
         final  Long endDate
    ){
        return new Specification<Orders>() {
            @Override
            public Predicate toPredicate(Root<Orders> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(username)){
                    predicates.add(criteriaBuilder.equal(root.<String>get("username"),username));
                }
                if(store != null && StringUtils.isNotEmpty(store.getStoreId())){
                    predicates.add(criteriaBuilder.equal(root.<String>get("store"),store));
                }
                if (orderDaddress !=null){
                    predicates.add(criteriaBuilder.equal(root.<String>get("orderDaddress"),orderDaddress));
                }
                if (orders.getOrderState()>0){
                    predicates.add(criteriaBuilder.equal(root.<String>get("orderState"),String.valueOf(orders.getOrderState())));
                }
                if (orders.getOrdersId()!=null && !orders.getOrdersId().equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("ordersId"),orders.getOrdersId()));
                }
                if (orders.getSellerState()>0){
                    predicates.add(criteriaBuilder.equal(root.<String>get("sellerState"),String.valueOf(orders.getSellerState())));
                }
                if (startDate != null && endDate != null && (startDate <=endDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),startDate,endDate));
                }else if(startDate !=null && endDate ==null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),startDate,new Date().getTime()));
                }else if (startDate == null && endDate != null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),0l,endDate));
                }
                predicates.add(criteriaBuilder.equal(root.<String>get("del"),false));
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }


    /**
     * 订单信息 动态条件查询      【预计配送的订单信息】
     * @param store   店铺
     * @return
     */
    public static Specification<Orders> OrdersWhereByStore(
            final Orders orders,
            final  Store store,
            final  Long startDate,
            final  Long endDate
    ){
        return new Specification<Orders>() {
            @Override
            public Predicate toPredicate(Root<Orders> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (orders.getOrdersId()!=null && !orders.getOrdersId().equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("ordersId"),orders.getOrdersId()));
                }
                if (orders.getSellerState()>0){
                    predicates.add(criteriaBuilder.equal(root.<String>get("sellerState"),String.valueOf(orders.getSellerState())));
                }
                if (startDate != null && endDate != null && (startDate <=endDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),startDate,endDate));
                }else if(startDate !=null && endDate ==null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),startDate,new Date().getTime()));
                }else if (startDate == null && endDate != null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),0l,endDate));
                }
                if(store != null && StringUtils.isNotEmpty(store.getStoreId())){
                    predicates.add(criteriaBuilder.equal(root.<String>get("store"),store));
                }
                predicates.add(criteriaBuilder.equal(root.<String>get("orderState"),"20"));
                predicates.add(criteriaBuilder.equal(root.<String>get("del"),false));
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }

    /**
     * 动态获取商家旗下的订单信息
     * @param username   用户
     * @param orderDaddress  地址
     * @param orders  订单
     * @param store   店铺
     * @param startDate   订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @return
     */
    public static Specification<Orders> getMeOrdersWhereByStore(
            final  String username,
            final  OrderDaddress orderDaddress,
            final  Orders orders,
            final  Store store,
            final  Long startDate,
            final  Long endDate
    ){
        return new Specification<Orders>() {
            @Override
            public Predicate toPredicate(Root<Orders> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(username)){
                    predicates.add(criteriaBuilder.equal(root.<String>get("username"),username));
                }
                if (orderDaddress !=null){
                    predicates.add(criteriaBuilder.equal(root.<String>get("orderDaddress"),orderDaddress));
                }
                if (orders.getOrdersId()!=null && !orders.getOrdersId().equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("ordersId"),orders.getOrdersId()));
                }
                if (orders.getSellerState()>0){
                    predicates.add(criteriaBuilder.equal(root.<String>get("sellerState"),String.valueOf(orders.getSellerState())));
                }
                if (orders.getOrderState()>0){
                    predicates.add(criteriaBuilder.equal(root.<String>get("orderState"),String.valueOf(orders.getOrderState())));
                }
                if (startDate != null && endDate != null && (startDate <=endDate)){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),startDate,endDate));
                }else if(startDate !=null && endDate ==null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),startDate,new Date().getTime()));
                }else if (startDate == null && endDate != null){
                    predicates.add(criteriaBuilder.between(root.<Long>get("createTime"),0l,endDate));
                }
                if(store != null && StringUtils.isNotEmpty(store.getStoreId())){
                    predicates.add(criteriaBuilder.equal(root.<String>get("store"),store));
                }
                predicates.add(criteriaBuilder.equal(root.<String>get("del"),false));
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }
}
