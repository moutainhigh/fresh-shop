package com.hafu365.fresh.core.entity.constant;

/**
 * 订单状态常量
 * Created by zhaihuilin on 2017/8/1  16:52.
 */
public final class OrderStateConstant {

    /**
     * 待发货
     */
    public final static int ORDER_STATE_UNFILLE=20;
    /**
     * 待收货
     */
    public final static int ORDER_STATE_NOT_RECEIVING=30;
    /**
     * 已签收 已完成
     */
    public final static int ORDER_STATE_FINISH=40;
    /**
     * 未生成
     */
    public final static int DAY_ORDER_STATE_NO=1;
    /**
     * 生成
     */
    public final static int DAY_ORDER_STATE_YES=2;

    /**
     * 订单退货审核状态  待审核
     */
    public final static int RETURN_STATE_CHECK_NO = 1;

    /**
     * 订单退货审核状态  已审核  同意   agree
     */
    public final static int RETURN_STATE_CHECK_YES_AGREE = 2;

    /**
     * 订单退货审核状态  已审核  不同意   disagree
     */
    public final static int RETURN_STATE_CHECK_YES_DISAGREE =3;


    public final static  String  ORDER_GOODS_STATE="商品失效";


}
