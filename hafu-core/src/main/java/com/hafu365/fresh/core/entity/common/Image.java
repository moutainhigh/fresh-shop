package com.hafu365.fresh.core.entity.common;

import lombok.*;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * 图片实体类
 * Created by HuangWeizhen on 2017/7/21.
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Image implements Serializable{
    /**
     * 标题
     */
    @NonNull
    private String title;
    /**
     * 地址
     */
    @NonNull
    private String path;
    /**
     * 链接
     */
    @NonNull
    private String url;

}
