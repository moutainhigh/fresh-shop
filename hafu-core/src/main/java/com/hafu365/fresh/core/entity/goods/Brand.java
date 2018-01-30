package com.hafu365.fresh.core.entity.goods;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.store.Store;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 品牌实体类
 * Created by HuangWeizhen on 2017/7/27.
 */
@Entity
@Data
@ToString(exclude = "goodsClassList")
@NoArgsConstructor
@Table(name = "fresh_goods_brand")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler","goodsClassList"})
public class Brand implements Serializable{

    /**
     * 品牌id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @Parameter(name = "k", value = "B")
    })
    @GeneratedValue(generator = "sys-uid")
    private String  brandId;

    /**
     * 品牌标题
     */
    @Column(unique = true)
    private String brandTitle;

    /**
     * 品牌图片
     */
    @Transient
    private List<Image> brandPic;
    @JsonBackReference
    @Lob
    private String pics;

    /**
     * 发布商家
     */
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    /**
     * 发布时间
     */
    private long createTime;

    /**
     * 是否删除
     */
    private boolean del;

    /**
     * 品牌状态
     */
    private String state;

    /**
     * 默认品牌
     */
    private boolean theDefault;

    /**
     * 关联的分类
     */
    @JsonBackReference
    @ManyToMany(targetEntity = GoodsClass.class)
    @JoinTable(name = "fresh_brand_goodsClass",
            joinColumns = {@JoinColumn(name = "brand_id")},
            inverseJoinColumns = {@JoinColumn(name = "goods_class_id")})
    private List<GoodsClass> goodsClassList;

    @PrePersist
    @PreUpdate
    private void save(){
        if(brandPic != null){
            Gson gson = new Gson();
            pics = gson.toJson(brandPic);
        }
    }

    @PostLoad
    private void load(){
        if(pics != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {}.getType();
            brandPic = gson.fromJson(pics,type);
            pics = null;
        }
    }

}
