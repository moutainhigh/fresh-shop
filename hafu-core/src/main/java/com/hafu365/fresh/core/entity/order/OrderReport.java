package com.hafu365.fresh.core.entity.order;


import com.hafu365.fresh.core.utils.ExcelResources;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhaihuilin on 2017/8/29  17:21.
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderReport implements Serializable{

    /**
     * 订单编号
     */
    private  String orderId;
    /**
     * 订单时间
     */

    private Date createTime;
    /**
     * 用户
     */
    private  String username;
    /**
     * 店铺名称
     */
    private  String storeName;
    /**
     * 商品名称
     */
    @NonNull
    private String goodsName;
    /**
     * 购买数量
     */
    private  int shopNum;
    /**
     * 商品单价
     */
    private  double goodsPrice;
    /**
     * 商品总价
     */
    private  double goodsTotalPrice;



    /***/
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @ExcelResources(title = "商品名称",order = 1)
    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    @ExcelResources(title = "数量",order = 2)
    public int getShopNum() {
        return shopNum;
    }

    public void setShopNum(int shopNum) {
        this.shopNum = shopNum;
    }
    @ExcelResources(title = "单价",order = 3)
    public double getGoodsPrice() {
        return goodsPrice;
    }


    public void setGoodsPrice(double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }
    @ExcelResources(title = "商品总价",order = 4)
    public double getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    public void setGoodsTotalPrice(double goodsTotalPrice) {
        this.goodsTotalPrice = goodsTotalPrice;
    }
}
