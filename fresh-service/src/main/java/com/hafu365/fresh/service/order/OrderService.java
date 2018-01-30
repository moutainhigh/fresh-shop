package com.hafu365.fresh.service.order;

import com.hafu365.fresh.core.entity.order.OrderDaddress;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 订单 服务层
 * Created by zhaihuilin on 2017/7/21  14:48.
 */
public interface OrderService {

    /**
     *  保存 订单信息
     * @param orders
     * @return
     */
    public Orders saveOrders(Orders orders);

    /**
     * 编辑 订单信息
     * @param orders
     * @return
     */
    public  Orders updateOrders(Orders orders);

    /**
     * 根据 订单编号查询 订单信息
     * @param ordersId
     * @return
     */
    public  Orders findOrdersByordersId(String ordersId);

    /**
     * 根据 订单编号 查询 订单信息  未删除状态
     * @param ordersId
     * @return
     */
    public Orders findOrdersByOrdersIdAndDelFalse(String ordersId);

    /**
     *  根据 订单编号进行删除  逻辑删除
     * @param ordersId
     * @return
     */
    public  boolean deleteOrderByordersId(String ordersId);

    /**
     * 根据 订单编号进行删除  物理删除
     * @param ordersId
     * @return
     */
    public  boolean physicallyDeleteByordersId(String ordersId);


    /**
     * 条件查询 分页
     * @param username   所属用户
     * @param store  店铺
     * @param orderDaddress  配送地址
     * @param orders     订单
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @param pageable
     * @return
     */
    public Page<Orders> findOrders(
            String username,
            Store store,
            OrderDaddress orderDaddress,
            Orders orders,
            Long startDate,
            Long endDate,
            Pageable pageable
    );

    /**
     * 获取用户自己的订单信息
     * @param username   用户
     * @param pageable    分页
     * @return
     */
    public Page<Orders> getMeOrders(
            String username,
            Pageable pageable
    );


    /**
     * 店铺
     * @param orders 订单
     * @param store  店铺
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @param pageable
     * @return
     */
    public Page<Orders> findOrdersByStore(
        Orders orders,
        Store store,
        Long startDate,
        Long endDate,
        Pageable pageable
    );

    /**
     * 店铺
     * @param orders 订单
     * @param store  店铺
     * @param startDate  订单创建的开始时间
     * @param endDate    订单创建的结束时间
     * @param pageable
     * @param username  所属用户
     * @return
     */
    public Page<Orders> findStoreMeOrdersByStore(
            String username,
            OrderDaddress orderDaddress,
            Orders orders,
            Store store,
            Long startDate,
            Long endDate,
            Pageable pageable
    );
}
