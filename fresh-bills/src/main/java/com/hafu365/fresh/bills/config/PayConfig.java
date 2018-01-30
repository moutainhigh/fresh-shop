package com.hafu365.fresh.bills.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by SunHaiyang on 2017/10/11.
 */
@Component
@Getter
public class PayConfig {

    /**
     * 付款链接
     */
    @Value("${payment.url}")
    private String url;

    /**
     * 回调链接
     */
    @Value("${payment.callBackUrl}")
    private String callBackUrl;

    /**
     * KEY
     */
    @Value("${payment.key}")
    private String key;
}
