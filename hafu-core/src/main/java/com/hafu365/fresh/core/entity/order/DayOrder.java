package com.hafu365.fresh.core.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 天订单实体类
 * Created by zhaihuilin on 2017/7/21  11:55.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fresh_order_day_order")
public class DayOrder implements Serializable {

    /**
     * 天订单编号
     */
    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "DO")
    })
    @GeneratedValue(generator = "sys-uid")
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
     * 商品列表
     */
    @Transient
    private List<GoodsVo> goodsVoList;

    /**
     * 商品存储字段
     */
    @Lob
    @JsonBackReference
    private  String goodsVoStr;


    @ManyToOne(cascade = {},fetch = FetchType.EAGER)
    @JoinColumn(name = "make_order_id",nullable = true)
    @JsonManagedReference
    private MakeOrder makeOrder;
    /**
     * 读取数据库时进行JSON from Entity的转换
     */
    @PostLoad //读取
    private  void  load(){
        if (this.goodsVoStr !=null){
            Type type= new TypeToken<ArrayList<GoodsVo>>(){}.getType();
            Gson gson=new Gson();
            goodsVoList=gson.fromJson(goodsVoStr,type);
            goodsVoStr=null;
        }
    }

    /**
     * 存储或更新的时候进行Entity from JSON 的转换
     */
    @PrePersist
    @PreUpdate
    private void save(){
        ExclusionStrategy es = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fa) {
                return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList") || fa.getName().equals("childMember") ||fa.getName().equals("roleList");
            }
            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };
        Gson gson = new GsonBuilder().setExclusionStrategies(es).create();
        if (goodsVoList !=null){
            goodsVoStr=gson.toJson(goodsVoList);
        }

    }

    /**
     * 新增构造方法
     * @param dayOrderState  状态
     * @param deliverTime  配送时间
     * @param goodsVoList  商品
     * @param makeOrder  预订单
     */
    public DayOrder(int dayOrderState,long deliverTime,List<GoodsVo> goodsVoList,MakeOrder makeOrder){
        this.dayOrderState=dayOrderState;
        this.deliverTime=deliverTime;
        this.goodsVoList=goodsVoList;
        this.makeOrder=makeOrder;
    }

}
