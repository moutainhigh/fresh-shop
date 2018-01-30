package com.hafu365.fresh.repository.order;


import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单 执久层
 * Created by zhaihuilin on 2017/7/21  14:47.
 */
@Repository
public interface OrderRepository extends JpaRepository<Orders,String>,JpaSpecificationExecutor<Orders> {

    /**
     * 根据 订单编号 查询 订单信息
     *
     * @param ordersId
     * @return
     */
    public Orders findOrdersByOrdersId(String ordersId);

    /**
     * 根据 订单编号 查询 订单信息  未删除状态
     * @param ordersId
     * @return
     */
    public Orders findOrdersByOrdersIdAndDelFalse(String ordersId);

    /**
     * 逻辑删除
     *
     * @param ordersId
     */
    @Query(value = " update  Orders  o set o.del = true where o.ordersId = ?1")
    @Modifying
    public void deleteOrdersByOrdersId(String ordersId);

    /**
     * 查询店铺订单
     * @param store
     * @return
     */
    List<Orders> findByStoreAndDelFalse(Store store);

    /**
     * 查询用户未删除订单
     * @param username
     * @param pageable
     * @return
     */
    Page<Orders> findOrdersByUsernameAndDelFalse(String username , Pageable pageable);

}