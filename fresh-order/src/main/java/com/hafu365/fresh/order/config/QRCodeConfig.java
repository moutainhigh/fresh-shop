package com.hafu365.fresh.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 二维码生成配置文件
 * Created by zhaihuilin on 2017/9/7  13:37.
 */
@Configuration
public class QRCodeConfig {
        /**
         * 生成地址
         */
        @Value(value = "${upload.filepath}")
        public  String  QRCODEIMG_PATH;
        /**
         * 链接地址
         */
        @Value(value = "${upload.baseurl}")
        public  String   QRCODEBASE_URL;

        /**
         * 二维码内容链接
         */
        @Value(value = "${QRCode.Texturl}")
        public  String  QRCODEText_URL;
}
