package com.hafu365.fresh.core.entity.home;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.utils.FloorFactory;
import com.hafu365.fresh.core.utils.StringUtils;
import lombok.*;

import javax.persistence.*;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 楼层
 * Created by SunHaiyang on 2017/8/21.
 */
@Data
@ToString
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "fresh_home_floor")
@JsonIgnoreProperties({"bodyStr","titleImageStr","advStr"})
public class Floor implements Serializable {

    /**
     * 楼层ID
     */
    @Id
    @GeneratedValue
    private long floorId;

    /**
     * 楼层标题
     */
    @NonNull
    private String title;

    /**
     * 楼层样式
     */
    @NonNull
    private String style;

    /**
     * 楼层类别
     */
    @NonNull
    private String type;

    /**
     * 楼层内容
     */
    @Transient
    private List<FloorBody> body;

    /**
     * 内容图片
     */
    @Lob
    private String bodyStr;

    /**
     * 标题图片
     */
    private Image titleImage;

    /**
     * 图片内容
     */
    @Lob
    private String titleImageStr;

    /**
     * 广告图片
     */
    @Transient
    private List<Image> advImage;

    /**
     * 广告图片内容
     */
    @Lob
    private String advStr;

    /**
     * 楼层培训
     */
    private int sort;

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

    @PostLoad
    private void load(){
        Gson gson = new Gson();
        if (StringUtils.isNotEmpty(this.bodyStr)){
            this.body = FloorFactory.getFloorBody(this.bodyStr,this.type);
        }
        if(StringUtils.isNotEmpty(this.advStr)){
            Type type = new TypeToken<ArrayList<Image>>(){}.getType();
            this.advImage = gson.fromJson(this.advStr,type);
        }
        if(StringUtils.isNotEmpty(this.titleImageStr)){
            this.titleImage = gson.fromJson(this.titleImageStr,Image.class);
        }
    }

    @PrePersist
    @PreUpdate
    private void save(){
        Gson gson = new Gson();
        if(this.body != null){
            this.bodyStr = gson.toJson(this.body);
        }
        if(this.advImage != null){
            this.advStr = gson.toJson(this.advImage);
        }
        if(this.titleImage != null){
            this.titleImageStr = gson.toJson(this.titleImage);
        }
    }


}
