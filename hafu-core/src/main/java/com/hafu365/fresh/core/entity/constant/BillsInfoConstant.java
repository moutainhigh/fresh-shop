package com.hafu365.fresh.core.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 账单信息
 * Created by SunHaiyang on 2017/8/24.
 */
@Getter
@AllArgsConstructor
public enum BillsInfoConstant {
    /**
     * 入账
     */
    ENTRY_ACCOUNT(500),
    /**
     * 冲账
     */
    CHARGE_OFF(300);
    private final int state;

    public int getState() {
        return state;
    }

    public static BillsInfoConstant getBillsInfoConstant(int state){
        BillsInfoConstant[] billsInfoConstants = BillsInfoConstant.values();
        for (BillsInfoConstant billsInfoConstant : billsInfoConstants){
            if (billsInfoConstant.getState() == state){
                return billsInfoConstant;
            }
        }
        return null;
    }
}
