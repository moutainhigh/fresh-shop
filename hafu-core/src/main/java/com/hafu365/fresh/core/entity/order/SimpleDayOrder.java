package com.hafu365.fresh.core.entity.order;

import lombok.*;

/**
 * Created by zhaihuilin on 2017/10/24  14:47.
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDayOrder {

    private  String  dayOrderId;

    /**
     * 订单状态
     */
    private int dayOrderState;

    /**
     * 配送时间
     */
    @NonNull
    private  long deliverTime;

    /**
     * 是否锁定
     */
    private boolean islook=Boolean.FALSE;

    /**
     * 商品结合的长度
     */
    private int   goodsListSize;



    public  SimpleDayOrder(DayOrder dayOrder){
         this.dayOrderId=dayOrder.getDayOrderId();
         this.dayOrderState=dayOrder.getDayOrderState();
         this.deliverTime=dayOrder.getDeliverTime();
         this.islook=dayOrder.isIslook();
         this.goodsListSize=dayOrder.getGoodsVoList().size();
    }
}
