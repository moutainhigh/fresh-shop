package com.hafu365.fresh.repository.order;

import com.hafu365.fresh.core.entity.order.OrderDaddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配送地址 持久层
 * Created by zhaihuilin on 2017/8/7  17:36.
 */
@Repository
public interface OrderDaddressRepository extends JpaRepository<OrderDaddress,Long> ,JpaSpecificationExecutor<OrderDaddress>{

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
    public  void  deleteOrderDaddressByOrderDaddressId(long orderDaddressId);

    /**
     * 根据用户查询订单地址列表
     * @param username
     * @return
     */
    public List<OrderDaddress> findOrderDaddressByUsername(String username);

    /**
     * 获取用户的默认配送地址
     * @param username
     * @return
     */
    public OrderDaddress findByUsernameAndIsdefaultTrue(String username);



}
