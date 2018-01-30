package com.hafu365.fresh.core.entity.goods;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.store.Store;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品实体类
 * Created by HuangWeizhen on 2017/7/21.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "fresh_goods")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Goods implements Serializable {
    /**
     * 商品id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @Parameter(name = "k", value = "G")
    })
    @GeneratedValue(generator = "sys-uid")
    private String goodsId;
    /**
     * 商品标题
     */
//    @NonNull
    private String goodsTitle;
    /**
     * 商品副标题
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String goodsSubTitle;
    /**
     *商品价格
     */
    @NonNull
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String,Double> price;
    @JsonBackReference
    @Lob
    private String priceStr;
    /**
     * 商家
     */
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    /**
     * 商品分类
     */
    @ManyToOne(cascade = CascadeType.REFRESH,fetch = FetchType.EAGER)
    @JoinColumn(name = "goodsClass_id")
    private GoodsClass goodsClass;
    /**
     * 商品品牌
     */
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    /**
     * 商品图片
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Image> goodsPic;
    @JsonBackReference
    @Lob
    private String pics;
    /**
     * 商品描述
     */
    @Lob
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StringBuffer goodsBody;
    /**
     * 关键字
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String keywords;

    /**
     * 佣金
     */
    private double commission;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 编辑时间
     */
    private long updateTime;
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
     * 商品状态
     */
    private String state;

    /**
     * 关联商品库存
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "goodsStock_id")
    private GoodsStock goodsStock;

    @PostLoad
    private void load(){
        if(pics != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {}.getType();
            goodsPic = gson.fromJson(pics,type);
            pics = null;
        }

        if(priceStr != null){
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String,Double>>() {}.getType();
            price = gson.fromJson(priceStr,type);
            priceStr = null;
        }
    }

    @PrePersist
    @PreUpdate
    private void save(){
        if(goodsPic != null){
            Gson gson = new Gson();
            pics = gson.toJson(goodsPic);
        }

        if(price != null){
            Gson gson = new Gson();
            priceStr = gson.toJson(price);
        }
    }

    public Goods(String goodsId,String goodsTitle,String priceStr,String pics,GoodsStock stock){
        Gson gson = new Gson();
        Type typeImage = new TypeToken<ArrayList<Image>>() {}.getType();
        Type typePrice = new TypeToken<HashMap<String,Double>>() {}.getType();
        this.goodsId = goodsId;
        this.goodsTitle = goodsTitle;
        this.price = gson.fromJson(priceStr,typePrice);
        this.goodsPic = gson.fromJson(pics,typeImage);
        this.goodsStock = stock;
    }



    /**
     * 简易的商品实体信息  ZHL
     * @param goodsId
     * @param goodsTitle
     * @param goodsSubTitle
     * @param price
     * @param priceStr
     * @param store
     * @param soldInTime
     * @param soldOutTime
     * @param state
     * @param del
     */
    public Goods(String goodsId,String goodsTitle, String goodsSubTitle, Map<String, Double> price, String priceStr, Store store, long soldInTime, long soldOutTime, String state,boolean del) {
        this.goodsId = goodsId;
        this.goodsTitle = goodsTitle;
        this.goodsSubTitle = goodsSubTitle;
        this.price = price;
        this.priceStr = priceStr;
        this.store = store;
        this.soldInTime = soldInTime;
        this.soldOutTime = soldOutTime;
        this.state = state;
        this.del=del;
    }
}
