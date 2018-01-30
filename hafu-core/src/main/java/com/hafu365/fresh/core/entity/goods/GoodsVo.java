package com.hafu365.fresh.core.entity.goods;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.entity.store.StoreVo;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单商品映射实体类
 * Created by zhaihuilin on 2017/7/21  10:02.
 */
@Data
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class GoodsVo implements Serializable {

    /**
     * 商品ID
     */
    @NonNull
    private  String goodsId;

    /**
     * 商品
     */
    @NonNull
    private SimpleGoods goods;
    /**
     * 所属店铺
     */
    @NonNull
    private StoreVo store;

    /**
     * 购买商品的 数量
     */
    @NonNull
    private  int  number;

    /**
     * 商品总价 =  购买商品的数量  *  商品的单价
     */
    @NonNull
    private  double price;

    /**
     * 商品损耗
     */
    private int  loss;

    /**
     * 订单商品状态
     */
    private String  orderGoodstate;


    /**
     * 商品是否失效
     */
    private  boolean  islose=Boolean.FALSE;

    /**
     * 拒绝收货的商品数量
     */
    private  int   rejection;

    /**
     * 拒绝收货的原因
     */
    @Lob
    private  String rejectreason;
    /**
     * 创建的时间
     */
    private  long createTiem= new Date().getTime() ;

    /**
     * 签收的时间
     */
    private  long takeTime;

    /**
     * 商品价格
     */
    @NonNull
    @Transient
    private Map<String,Double> goodsprice =new HashMap<String, Double>();


    /**
     * 价格存储 实体
     */
    @Lob
    @JsonBackReference
    private String goodspriceStr;


    public void setGoods(Goods goods) {
        this.goods = new SimpleGoods(goods);
    }

    public void setStore(Store store) {
        this.store = new StoreVo(store);
    }

    /**
     * 读取数据库时进行JSON from Entity的转换
     */
    @PostLoad //读取
    public void  load(){
        if (goodspriceStr !=null){
            Type type= new TypeToken<Map<String, Double>>() {}.getType();
            Gson gson=new Gson();
            goodsprice=gson.fromJson(goodspriceStr,type);
            goodspriceStr=null;
        }
    }

    /**
     * 存储或更新的时候进行Entity from JSON 的转换
     */
    @PrePersist
    @PreUpdate
    private void  save(){
        if (goodsprice !=null){
            Gson gson=new Gson();
            goodspriceStr=gson.toJson(goodsprice);
        }
    }


}
