package com.hafu365.image.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by SunHaiyang on 2017/8/22.
 */
@Component
public class UploadConfig {

    /**
     * 上传地址
     */
    @Value(value = "${upload.filepath}")
    public String FILE_PATH;

    /**
     * 链接地址
     */
    @Value(value = "${upload.baseurl}")
    public String BASE_URL;

}