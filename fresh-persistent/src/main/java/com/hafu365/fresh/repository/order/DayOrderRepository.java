package com.hafu365.fresh.repository.order;


import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.entity.order.SimpleDayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 天订单 执久层
 * Created by zhaihuilin on 2017/7/21  14:21.
 */
@Repository
public interface DayOrderRepository extends JpaRepository<DayOrder,Long>,JpaSpecificationExecutor<DayOrder> {

    /**
     * 根据 天订单编号 查询 天订单信息
     *
     * @param dayOrderId
     * @return
     */
    public DayOrder findDayOrderByDayOrderId(String dayOrderId);

    /**
     * 通过 天订单编号进行删除
     *
     * @param dayOrderId
     */
    public void deleteDayOrderByDayOrderId(String dayOrderId);

    @Modifying
    @Query(value = "update DayOrder d set d.goodsVoStr = ?2 , d.dayOrderState = ?3 where d.dayOrderId = ?1")
    public Integer updateDayOrder(String dayOrderId, String goodsVoStr, int dayOrderState);

    /**
     * 根据预订单获取简易的天订单集合信息
     *
     * @param makeOrder
     * @return
     */
    @Query("select new com.hafu365.fresh.core.entity.order.SimpleDayOrder(d) from DayOrder d where d.makeOrder = ?1")
    public List<SimpleDayOrder> findSimpleDayOrOrderByMakeOrder(MakeOrder makeOrder);

    /**
     * 根据预订单获取所有天订单集合信息
     *
     * @param makeOrder
     * @return
     */
    public List<DayOrder> findDayOrderByMakeOrder(MakeOrder makeOrder);

    /**
     * 通过配送时间查询
     *
     * @param time
     * @return
     */
    public List<DayOrder> findDayOrdersByDeliverTime(long time);

}
