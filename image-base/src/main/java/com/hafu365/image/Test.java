package com.hafu365.image;

import com.hafu365.image.config.UploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by SunHaiyang on 2017/8/22.
 */
@RestController
public class Test {

    @Autowired
    UploadConfig uploadConfig;

    @PostMapping(value = "/test")
    public String test(){
        return uploadConfig.BASE_URL;
    }
}
