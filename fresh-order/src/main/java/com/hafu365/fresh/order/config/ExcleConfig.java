package com.hafu365.fresh.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Eexle报表导出配置
 * Created by zhaihuilin on 2017/9/7  10:15.
 */
@Configuration
public class ExcleConfig {

    /**
     * 上传地址
     */
    @Value(value = "${filebase.filepath}")
    public String EXCLEFILE_PATH;

    /**
     * 链接地址
     */
    @Value(value = "${filebase.fileurl}")
    public String BASE_URL;


}
