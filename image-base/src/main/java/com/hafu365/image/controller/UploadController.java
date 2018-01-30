package com.hafu365.image.controller;

import com.hafu365.image.entity.UploadInfo;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.image.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by SunHaiyang on 2017/8/22.
 */
@RestController
public class UploadController {

    @Autowired
    ImageUploadService uploadService;

    @PostMapping(value = "/upload")
    public ReturnMessages upload(MultipartFile file){
        UploadInfo uploadInfo = uploadService.upload(file);
        if(uploadInfo != null){
            return new ReturnMessages(RequestState.SUCCESS,"上传成功。",uploadInfo);
        }
        return new ReturnMessages(RequestState.SUCCESS,"上传失败。",null);
    }

//    @PostMapping(value = "/upload")
//    public ReturnMessages upload(MultipartFile[] files){
//        List<UploadInfo> uploadInfos = uploadService.upload(files);
//        if(uploadInfos.size() > 0){
//            return new ReturnMessages(RequestState.SUCCESS,"上传成功。",uploadInfos);
//        }
//        return new ReturnMessages(RequestState.SUCCESS,"上传失败。",null);
//    }
}
