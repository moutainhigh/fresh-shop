package com.hafu365.fresh.core.entity.order;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.store.Store;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 * Created by zhaihuilin on 2017/7/21  11:29.
 */
@Data
@Entity
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "fresh_order")
@JsonIgnoreProperties({"member"})
public class Orders implements Serializable {

    /**
     * 订单编号
     */

    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "O")
    })
    @GeneratedValue(generator = "sys-uid")
    private  String ordersId;

    /**
     * 订单价格
     */
    @NonNull
    private  double price;

    /**
     * 订单状态
     */
    @NonNull
    private  int  orderState;

    /**
     * 退货审核状态   1:待审核 2:同意 3:不同意
     */
    private int sellerState;

    /**
     * 商家审核退货  所填写的原因
     */
    private  String sellerMessage;
    /**
     * 是否锁定
     */
    @JsonBackReference
    private  boolean  islook=Boolean.FALSE;
    /**
     * 创建时间
     */
    private  long createTime=new Date().getTime() ;
    /**
     * 更新时间
     */
    private  long  updateTime=new Date().getTime();

    /**
     * 是否删除
     */
    private boolean del = Boolean.FALSE;


    /**
     * 所属用户
     */
      @NonNull
      private  String username;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @ManyToOne(cascade = {},fetch =FetchType.EAGER)
//    @JoinColumn(name = "member_id")
//    private Member member;

    /**
     * 配送地址
     */
    @Transient
    @NonNull
    private OrderDaddress orderDaddress;

    @Lob
    @JsonBackReference
    private String  orderDaddressStr;

    /**
     * 所属店铺
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ManyToOne(cascade = {},fetch =FetchType.EAGER)
    @JoinColumn(name = "store_id")
    private Store store;

    /**
     * 商品列表
     */
    @Transient //数据库不存在这个字段的 时候  用 @Transient
    @NonNull
    private List<GoodsVo> goodsList;

    /**
     * 商品列表存储
     */
    @Lob
    @JsonBackReference
    private  String goodsStr;

    /**
     * 二维码图片
     */
    @JsonBackReference
    @Lob
    private  String  QRCodeImg;

    /**
     * 有参数构造方法
     * @param price      价格
     * @param orderState  订单状态
     * @param username   用户
     * @param orderDaddress  配送地址
     * @param store   店铺
     * @param goodsList   商品集合
     * @param QRCodeImg   二维码
     */
    public  Orders(double price,int  orderState,String username,OrderDaddress orderDaddress,Store store,List<GoodsVo> goodsList,String  QRCodeImg){
        this.price=price;
        this.orderState=orderState;
        this.username=username;
        this.orderDaddress=orderDaddress;
        this.store=store;
        this.goodsList=goodsList;
        this.QRCodeImg=QRCodeImg;
    }
    /**
     * 读取数据库时进行JSON from Entity的转换
     */
    @PostLoad //读取
    private void load(){
         if (goodsStr !=null){
             Type type= new TypeToken<ArrayList<GoodsVo>>(){}.getType();
             Gson gson=new Gson();
             goodsList=gson.fromJson(goodsStr,type);
             goodsStr=null;
         }

         if (orderDaddressStr !=null){
             Type type= new TypeToken<OrderDaddress>(){}.getType();
             Gson gson=new Gson();
             orderDaddress=gson.fromJson(orderDaddressStr,type);
             orderDaddressStr=null;
         }
    }
    /**
     * 存储或更新的时候进行Entity from JSON 的转换
     */
    @PrePersist
    @PreUpdate
    private void  save(){
        Gson gson=new Gson();
        if (this.goodsList !=null){
            this.goodsStr=gson.toJson(this.goodsList);
        }

        if (this.orderDaddress !=null){
            this.orderDaddressStr=gson.toJson(this.orderDaddress);
        }
        System.out.println(this.toString());
    }






}


