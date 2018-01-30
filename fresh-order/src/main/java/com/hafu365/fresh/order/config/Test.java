package com.hafu365.fresh.order.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaihuilin on 2017/9/7  10:27.
 */
@RestController
public class Test {

    @Autowired
    ExcleConfig excleConfig;

    @Autowired
    QRCodeConfig qrCodeConfig;


//    @PostMapping(value = "/test")
//    public String test(){
//        String orderId="123456";
//        System.out.println("0---------:"+qrCodeConfig.QRCODEText_URL+"?orderId=" + orderId);
//        return  qrCodeConfig.QRCODEText_URL+"?orderId=" + orderId;
//    }
}
