package com.hafu365.fresh.core.entity.constant;

/**
 * 商品模块状态常量
 * Created by HuangWeizhen on 2017/7/27.
 */
public enum StateConstant {

    /**
     * 商品状态
     */
    GOODS_STATE_ON_CHECKING,  //审核中
    GOODS_STATE_CHECK_ON, //审核通过
    GOODS_STATE_CHECK_OFF, //审核不通过

    /**
     * 修改商品状态
     */
    MODIFY_GOODS_STATE_ON_CHECKING,  //修改审核中
    MODIFY_GOODS_STATE_CHECK_ON, //修改审核通过
    MODIFY_GOODS_STATE_CHECK_OFF, //修改审核不通过

    /**
     * 店铺状态
     */
    STORE_STATE_ON_CHECKING,  //店铺审核中
    STORE_STATE_CHECK_ON,    //店铺审核通过（正常运营）
    STORE_STATE_CHECK_OFF, //店铺审核不通过
    STORE_STATE_ON_CLOSE,    //店铺关闭

    /**
     * 品牌状态
     */
    BRAND_STATE_ON_CHECKING,  //品牌审核中
    BRAND_STATE_CHECK_ON,    //品牌审核通过（正常使用）
    BRAND_STATE_CHECK_OFF, //品牌审核不通过
    BRAND_STATE_ON_CLOSE,    //品牌停用

    /**
     * 商品评论状态(后台暂无评论审核功能，评论保存时直接审核通过)
     */
    COMMENT_STATE_ON_CHECKING,  //评论审核中
    COMMENT_STATE_CHECK_ON,    //评论审核通过（显示）
    COMMENT_STATE_CHECK_OFF, //评论审核不通过

    /**
     * 用户状态
     */
    USER_STATE_CHECK_ING,   //审核中
    USER_STATE_CHECK_NO,    //审核通过
    USER_STATE_CHECK_OFF,   //审核不通过
    USER_STATE_LOCK_ING     //用户锁定中
}
