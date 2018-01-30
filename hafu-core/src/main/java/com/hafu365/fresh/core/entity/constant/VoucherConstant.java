package com.hafu365.fresh.core.entity.constant;

/**
 * 代金券状态常量
 * Created by HuangWeizhen on 2017/8/29.
 */
public enum VoucherConstant {

    /**
     * 代金券状态
     */
    VOUCHER_STATE_ON_CHECKING,  //审核中
    VOUCHER_STATE_CHECK_ON,     //审核通过
    VOUCHER_STATE_CHECK_OFF,    //审核失败
    VOUCHER_STATE_OVERDUE,      //过期失效
    VOUCHER_STATE_USED,         //已使用

    /**
     * 代价券操作类型
     */
    VOUCHER_LOG_CREATE,         //创建
    VOUCHER_LOG_EDIT,           //编辑
    VOUCHER_LOG_CHECK,          //审核
    VOUCHER_LOG_USED,           //使用
    VOUCHER_LOG_CLOSE,          //关闭

}
