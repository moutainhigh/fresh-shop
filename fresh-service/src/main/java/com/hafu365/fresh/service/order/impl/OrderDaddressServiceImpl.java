package com.hafu365.fresh.service.order.impl;

import com.hafu365.fresh.core.entity.order.OrderDaddress;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.order.OrderDaddressRepository;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.order.OrderDaddressService;
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
import java.util.List;

/**
 * 配送地址逻辑层
 * Created by zhaihuilin on 2017/8/7  17:43.
 */
@Log4j
@Transactional
@Service
public class OrderDaddressServiceImpl implements OrderDaddressService {

    @Autowired
    private OrderDaddressRepository orderDaddressRepository;
    @Autowired
    private MemberService memberService;

    /**
     * 新增配送地址信息
     * @param orderDaddress
     * @return
     */
    @Override
    public OrderDaddress saveOrderDaddress(OrderDaddress orderDaddress) {
        return orderDaddressRepository.save(orderDaddress);
    }

    /**
     * 编辑配送地址信息
     * @param orderDaddress
     * @return
     */
    @Override
    public OrderDaddress updateOrderDaddress(OrderDaddress orderDaddress) {
        return orderDaddressRepository.save(orderDaddress);
    }

    /**
     * 根据用户查询订单地址列表
     * @param username
     * @return
     */
    @Override
    public List<OrderDaddress> findOrderDaddressByMember(String username) {
        return orderDaddressRepository.findOrderDaddressByUsername(username);
    }

    /**
     * 根据配送地址编号进行查询
     * @param orderDaddressId
     * @return
     */
    @Override
    public OrderDaddress findOrderDaddressByOrderDaddressId(long orderDaddressId) {
        return orderDaddressRepository.findOrderDaddressByOrderDaddressId(orderDaddressId);
    }

    /**
     * 根据配送地址编号进行删除
     * @param orderDaddressId
     * @return
     */
    @Override
    public boolean deleteOrderDaddressByOrderDaddressId(long orderDaddressId) {
     try {
          orderDaddressRepository.deleteOrderDaddressByOrderDaddressId(orderDaddressId);
          return true;
     }catch (Exception e){
          return false;
     }
    }

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderDaddress> findAll(Pageable pageable) {
        return orderDaddressRepository.findAll(pageable);
    }

    /**
     * 条件查询 分页
     * @param username  用户
     * @param orderDaddress  配送地址信息
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderDaddress> findAllOrderDaddress(String username, OrderDaddress orderDaddress, Pageable pageable) {
        return orderDaddressRepository.findAll(orderDaddressWhere(username,orderDaddress),pageable);
    }

    /**
     * 根据用户动态查询
     * @param username
     * @param pageable
     * @return
     */
    @Override
    public Page<OrderDaddress> findAllOrderDaddressByMember(String username, Pageable pageable) {
        return orderDaddressRepository.findAll(orderDaddressWhereByMember(username),pageable);
    }

    @Override
    public OrderDaddress findDefaultOrderDaddressByUsername(String username) {
//        Member member = memberService.findMemberByUsername(username);
        return orderDaddressRepository.findByUsernameAndIsdefaultTrue(username);
    }

    /**
     * 条件查询
     * @param username
     * @return
     */
    public static Specification<OrderDaddress> orderDaddressWhere(
            final String username,
            final OrderDaddress orderDaddress
    ){
        return new Specification<OrderDaddress>() {
            @Override
            public Predicate toPredicate(Root<OrderDaddress> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(username)){
                    predicates.add(cb.equal(root.<String>get("username"),username));
                }
                 if (orderDaddress.getAddress() !=null && orderDaddress.getAddress().equals("")){
                     predicates.add(cb.like(root.<String>get("address"),"%"+orderDaddress.getAddress()+"%"));
                 }
                 if (orderDaddress.getName() !=null && orderDaddress.getName().equals("")){
                     predicates.add(cb.like(root.<String>get("name"),"%"+orderDaddress.getName()+"%"));
                 }
                 if (orderDaddress.getPhone() !=null && orderDaddress.getPhone().equals("")){
                     predicates.add(cb.like(root.<String>get("phone"),"%"+orderDaddress.getPhone()+"%"));
                 }
                if (orderDaddress.getOrderDaddressId()>0){
                    predicates.add(cb.equal(root.<String>get("orderDaddressId"),orderDaddress.getOrderDaddressId()));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }

    /**
     * 条件查询
     * @param username
     * @return
     */
    public static Specification<OrderDaddress> orderDaddressWhereByMember(
            final String username
    ){
        return new Specification<OrderDaddress>() {
            @Override
            public Predicate toPredicate(Root<OrderDaddress> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (StringUtils.isNotEmpty(username)){
                    predicates.add(cb.equal(root.<String>get("username"),username));
                }
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }

}
