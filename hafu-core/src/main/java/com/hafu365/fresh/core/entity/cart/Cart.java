package com.hafu365.fresh.core.entity.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
 * 购物车类
 * Created by zhaihuilin on 2017/7/21  10:43.
 */
@Data
@Entity
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "fresh_goods_cart")
@JsonIgnoreProperties({"member"})
public class Cart implements Serializable {

    /**
     * 购物编号
     */
    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "C")
    })
    @GeneratedValue(generator = "sys-uid")
    private String cartId;

    /**
     *  购物车 总价
     */
    private double price;

//    /**
//     * 所属用户
//     */
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @ManyToOne(cascade = {},fetch =FetchType.EAGER)
//    @JoinColumn(name = "member_id")
//    private Member member;

    /**
     * 所属用户名
     */
    @NonNull
    private  String username;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long  updateTime;

    /**
     * 商品映射实体
     */
    @Transient     //数据库不存在这个字段的 时候  用 @Transient
    @NonNull
    private List<GoodsVo> goodsVoList;

    /**
     * 商品存储实体
     */
    @JsonBackReference
    @Lob
    private String goodsVoStr;

    /**
     * 读取数据库时进行JSON from Entity的转换
     */
    @PostLoad //读取
    private void load(){
        if (goodsVoStr !=null){
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
     private  void save(){
        if (goodsVoList !=null){
            ExclusionStrategy es = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fa) {
                    return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList");
                }
                @Override
                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            };
            Gson gson = new GsonBuilder().setExclusionStrategies(es).create();
            goodsVoStr=gson.toJson(goodsVoList);
        }
     }

    /**
     * 构造 方法
     * @param price  价格
     * @param username    用户
     * @param createTime  创建时间
     * @param updateTime  编辑时间
     * @param goodsVoList  商品信息
     */
     public  Cart(double price,String username,long createTime,long  updateTime,List<GoodsVo> goodsVoList){
         this.price=price;
         this.username=username;
         this.createTime=createTime;
         this.updateTime=updateTime;
         this.goodsVoList=goodsVoList;
     }
}
