package com.hafu365.fresh.core.entity.setting;

import org.springframework.stereotype.Component;

/**
 *基本设置中的key
 * Created by zhaihuilin on 2017/9/23  11:20.
 */
@Component
public  final class  BasicSetupKeyContant {

    /**
     * 自动下单时间
     */
    public final static String ORDER_AUTOMATIC_ORDER="0 42 11 * * ?";

    /**
     * 账单付款时间
     */
    public final static String BILLS_PAYMENT_TIME="0 42 11 * * ?";

    /**
     * 出账时间
     */
    public final static String BILLS_CLEARING_BILLS = "0 42 11 * * ?";

    /**
     * 账单结算时间
     */
    public final static String BILLS_PAYMENT_FINISH_TIME="0 42 11 * * ?";

    /**
     * 优惠券过期时间
     */
    public final static String VOUCHER_PAST_TIME="0 42 11 * * ?";

    /**
     * 价格修改商品自动生效时间
     */
    public final static String MODIFY_PRICE_GOODS_CHECKON_TIME = "0 42 11 * * ?";
}
