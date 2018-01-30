package com.hafu365.image.service;

import com.hafu365.image.entity.UploadInfo;
import org.springframework.web.multipart.MultipartFile;


/**
 * 上传逻辑类
 * Created by SunHaiyang on 2017/8/22.
 */
public interface ImageUploadService {

    /**
     * 上传单个
     * @return
     */
    public UploadInfo upload(MultipartFile multipartFile);



}
