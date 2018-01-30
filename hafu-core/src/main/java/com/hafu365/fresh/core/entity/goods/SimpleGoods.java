package com.hafu365.fresh.core.entity.goods;

import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.store.StoreVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaihuilin on 2017/10/20  10:44.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SimpleGoods implements Serializable {

    private String goodsId;

    private String goodsTitle;

    private Map<String,Double> price;

    private StoreVo store;

    private String state;

    /**
     * 是否显示
     */
    private boolean goodsShow;
    /**
     * 是否删除
     */
    private boolean del;
    /**
     * 上架时间
     */
    private long soldInTime;
    /**
     * 下架时间
     */
    private long soldOutTime;

    /**
     * 商品图片
     */
    private List<Image> goodsPic;
    private String pics;

    public SimpleGoods(Goods goods) {
        this.goodsId = goods.getGoodsId();
        this.goodsTitle = goods.getGoodsTitle();
        this.price = goods.getPrice();
        this.store = new StoreVo(goods.getStore());
        this.state = goods.getState();
        this.goodsShow = goods.isGoodsShow();
        this.del = goods.isDel();
        this.soldInTime = goods.getSoldInTime();
        this.soldOutTime = goods.getSoldOutTime();
        this.goodsPic=goods.getGoodsPic();
        this.pics=goods.getPics();
    }
}
