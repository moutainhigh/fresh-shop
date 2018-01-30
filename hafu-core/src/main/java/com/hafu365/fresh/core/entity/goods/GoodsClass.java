package com.hafu365.fresh.core.entity.goods;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类实体类
 * Created by HuangWeizhen on 2017/7/21.
 */
@Entity
@Data
@ToString(exclude = "oldClass")
@NoArgsConstructor
@Table(name = "fresh_goods_class")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler","oldClass"})
public class GoodsClass implements Serializable {
    /**
     * 分类id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @Parameter(name = "k", value = "GC")
    })
    @GeneratedValue(generator = "sys-uid")
    private String classId;
    /**
     * 分类标题
     */
    private String classTitle;
    /**
     * 父类
     */

    @ManyToOne( cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
    @JoinColumn(name = "oldClass_id")
    @JsonBackReference
    private GoodsClass oldClass;
    /**
     * 子类
     */
    @JsonManagedReference
    @Transient
    private List<GoodsClass> childClass;

    /**
     * 分类图片
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Image> classPic;

    @JsonBackReference
    @Lob
    private String pics;
    /**
     * 关键字
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String keywords;
    /**
     * 添加时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long updateTime;
    /**
     * 是否删除
     */
    private boolean del = Boolean.FALSE;
    /**
     * 是否显示
     */
    private boolean gcShow;

    /**
     * 排序数字
     */
    private int orderNum;

    /**
     * 关联的品牌
     */
    @JsonManagedReference
    @ManyToMany(
            targetEntity = Brand.class,
            mappedBy = "goodsClassList")
    private List<Brand> brandList;

    @Transient
    private List<String> brandIdList;

    public GoodsClass(String classId,String classTitle, String pics) {
        this.classId = classId;
        this.classTitle = classTitle;
        this.pics = pics;
    }

    @PostLoad
    private void load() {
        if (pics != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Image>>() {
            }.getType();
            classPic = gson.fromJson(pics, type);
            pics = null;
        }
    }

    @PrePersist
    @PreUpdate
    private void save() {
        if (classPic != null) {
            Gson gson = new Gson();
            pics = gson.toJson(classPic);
        }
    }

}
