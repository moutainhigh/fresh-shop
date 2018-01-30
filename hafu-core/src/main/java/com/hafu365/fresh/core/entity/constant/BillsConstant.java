package com.hafu365.fresh.core.entity.constant;

import com.alibaba.druid.filter.AutoLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 账单状态
 * Created by SunHaiyang on 2017/8/25.
 */
@Getter
@AllArgsConstructor
public enum BillsConstant {
    /**
     * 未出账
     */
    BILLS_NOT_GENERATED_BILLS(0),
    /**
     * 未付款
     */
    BILLS_NOT_PAYING(100),
    /**
     * 已付款
     */
    BILLS_PAYMENT_HAS_BEEN(200),
    /**
     * 账单关闭
     */
    BILLS_CLOSE(300),
    /**
     * 账单完成
     */
    BILLS_FINISH(400);
    private int state;

    public static BillsConstant getBillsConstant(int state){
        BillsConstant[] billsConstants = BillsConstant.values();
        for (BillsConstant billsConstant : billsConstants){
            if(billsConstant.getState() == state){
                return billsConstant;
            }
        }
        return null;
    }

}
