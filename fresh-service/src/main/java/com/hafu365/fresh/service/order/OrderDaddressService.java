package com.hafu365.fresh.service.order;

import com.hafu365.fresh.core.entity.order.OrderDaddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 配送地址业务层
 * Created by zhaihuilin on 2017/8/7  17:40.
 */
public interface OrderDaddressService {


    /**
     * 新增配送地址信息
     * @param orderDaddress
     * @return
     */
    public OrderDaddress saveOrderDaddress(OrderDaddress orderDaddress);

    /**
     * 编辑配送地址信息
     * @param orderDaddress
     * @return
     */
    public OrderDaddress updateOrderDaddress(OrderDaddress orderDaddress);


    /**
     * 根据用户查询订单地址列表
     * @param username
     * @return
     */
    public List<OrderDaddress> findOrderDaddressByMember(String username);




    /**
     * 根据地址编号查询地址信息
     * @param orderDaddressId
     * @return
     */
    public OrderDaddress findOrderDaddressByOrderDaddressId(long orderDaddressId);

    /**
     * 根据地址编号进行删除
     * @param orderDaddressId
     */
    public  boolean  deleteOrderDaddressByOrderDaddressId(long orderDaddressId);

    /**
     * 分页查询动态列表
     * @param pageable
     * @return
     */
    public Page<OrderDaddress> findAll(Pageable pageable);

    /**
     * 条件动态查询
     * @param username  用户
     * @param orderDaddress  配送地址信息
     * @param pageable
     * @return
     */
    public Page<OrderDaddress> findAllOrderDaddress(
            String username,
            OrderDaddress orderDaddress,
            Pageable pageable
    );

    /**
     * 根据用户信息动态查询
     * @param username
     * @param pageable
     * @return
     */
    public Page<OrderDaddress> findAllOrderDaddressByMember(
            String username,
            Pageable pageable
    );

    /**
     * 查询指定用户默认配送地址
     * @param username
     * @return
     */
    public OrderDaddress findDefaultOrderDaddressByUsername(String username);
}
