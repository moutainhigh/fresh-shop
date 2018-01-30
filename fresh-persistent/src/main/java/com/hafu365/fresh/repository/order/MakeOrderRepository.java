package com.hafu365.fresh.repository.order;


import com.hafu365.fresh.core.entity.order.MakeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 预约订单 执久层
 * Created by zhaihuilin on 2017/7/21  14:40.
 */
@Repository
public interface MakeOrderRepository extends JpaRepository<MakeOrder,Long>,JpaSpecificationExecutor<MakeOrder> {

    /**
     * 根据预约订单编号查询预约订单信息
     * @param makeOrderId
     * @return
     */
    public MakeOrder findMakeOrderByMakeOrderId(String makeOrderId);

    /**
     *  根据预约订单编号进行删除
     * @param makeOrderId
     */
    public void  deleteMakeOrderByMakeOrderId(String makeOrderId);

    /**
     * 根据用户查询预约订单
     * @param username
     * @return
     */
    public MakeOrder findMakeOrderByUsername(String username);



}
