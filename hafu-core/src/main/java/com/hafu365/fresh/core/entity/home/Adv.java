package com.hafu365.fresh.core.entity.home;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.utils.StringUtils;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 广告
 * Created by SunHaiyang on 2017/8/18.
 */
@Entity
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "fresh_home_adv")
@JsonIgnoreProperties("imageStr")
public class Adv implements Serializable {

    /**
     * ID
     */
    @GeneratedValue
    @Id
    private long advId;

    /**
     * 标题
     */
    @NonNull
    private String advTitle;

    /**
     * 图片
     */
    @Transient
    @NonNull
    private Image advImage;

    /**
     * 图片存储地址
     */
    @Lob
    private String imageStr;

    /**
     * 一直显示
     */
    private boolean onlyShow;


    /**
     * 开始显示时间
     */
    private long startTime;

    /**
     * 结束显示时间
     */
    private long endTime;

    /**
     * 排序
     */
    @NonNull
    private int sort;


    @PostLoad
    private void load(){
        if(StringUtils.isNotEmpty(this.imageStr)){
            Gson gson = new Gson();
            this.advImage = gson.fromJson(this.imageStr,Image.class);
        }

    }
    @PreUpdate
    @PrePersist
    private void save(){
        if (this.advImage != null){
            Gson gson = new Gson();
            this.imageStr = gson.toJson(this.advImage);
        }
    }
}
