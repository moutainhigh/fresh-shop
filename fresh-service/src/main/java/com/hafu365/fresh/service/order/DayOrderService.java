package com.hafu365.fresh.service.order;

import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.MakeOrder;
import com.hafu365.fresh.core.entity.order.SimpleDayOrder;

import java.util.List;


/**
 * 天订单 服务层
 * Created by zhaihuilin on 2017/7/21  14:23.
 */
public interface DayOrderService {

    /**
     * 添加 天订单信息
     * @param dayOrder
     * @return
     */
    public DayOrder saveDayOrders(DayOrder dayOrder);

    /**
     *  编辑(保存) 天订单信息
     * @param dayOrder
     * @return
     */
    public DayOrder updateDayOrders(DayOrder dayOrder);

    /**
     *  编辑(保存) 天订单信息
     * @param dayOrder
     * @return
     */
    public DayOrder update(DayOrder dayOrder);

    /**
     * 根据天订单编号进行删除
     * @param DayOrderId
     * @return
     */
    public boolean deleteDayOrdersByDayOrderId(String DayOrderId);

    /**
     * 根据天订单编号进行查询
     * @param DayOrderId
     * @return
     */
    public DayOrder findDayOrderByDayOrderId(String DayOrderId);
    /**
     * 根据配送信息查询天订单信息列表
     * @param startDate
     * @param endDate
     * @return
     */
    public List<DayOrder> findDayOrderBydeliverTime(
            Long startDate,
            Long endDate
    );

    /**
     * 通过MakeOrder 获取 简单的天订单信息
     * @param makeOrder
     * @return
     */
    public List<SimpleDayOrder> findSimpleDayOrderByMakeOrder(MakeOrder makeOrder);

    public List<DayOrder> findDayOrderByMakeOrder(MakeOrder makeOrder);

    /**
     * 通过配送时间获取订单信息
     * @param time
     * @return
     */
    public List<DayOrder> findDayOrderByDeliverTime(long time);

}
