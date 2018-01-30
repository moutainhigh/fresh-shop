package com.hafu365.fresh.core.entity.goods;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.converter.json.GsonBuilderUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * 修改属性商品实体类
 * Created by HuangWeizhen on 2017/8/9.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "fresh_modify_attr_goods")
public class ModifyAttrGoods implements Serializable {
    /**
     * 属性商品id
     */
    @Id
    @GeneratedValue
    private long modifyAttrId;

    /**
     * 已修改属性的商品
     */
    @Transient
    private Goods goods;
    @JsonBackReference
    @Lob
    private String goodsStr;

    /**
     * 商品id
     */
    private String goodsId;


    /**
     * 用户
     */
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 修改提交时间
     */
    private long createTime;

    /**
     * 状态
     */
    private String state;

    /**
     * 是否删除
     */
    private boolean del = Boolean.FALSE;

    /**
     * 修改原因
     */
    private String reason;

    @PreUpdate
    @PrePersist
    private void save(){
        ExclusionStrategy es = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fa) {
                return fa.getName().equals("memberList") || fa.getName().equals("goodsClassList") || fa.getName().equals("permissionList");
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        };
        Gson gson = new GsonBuilder().setExclusionStrategies(es).create();
        if(goods != null && goodsStr == null){
            goodsStr = gson.toJson(goods);
        }


    }

   @PostLoad
    private void load(){
        if(goodsStr != null && goods == null){
            Gson gson = new Gson();
            Type type = new TypeToken<Goods>() {}.getType();
            goods = gson.fromJson(goodsStr,type);
            goodsStr = null;
        }

    }


}
