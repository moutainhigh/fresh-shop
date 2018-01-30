package com.hafu365.fresh.core.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表头信息的Annotation
 * 用来在对象的get方法上加入的annotation，通过该annotation说明某个属性所对应的标题
 * Created by zhaihuilin on 2017/8/29  9:49.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelResources {

    /**
     * 属性的标题名称
     * @return
     */
     String title();

    /**
     * 在 excle 中的排序
     * @return
     */
     int order() default  9999;


















}
