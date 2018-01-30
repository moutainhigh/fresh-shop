package com.hafu365.image.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 上传信息
 * Created by SunHaiyang on 2017/8/22.
 */
@Data
@ToString
public class UploadInfo {

    /**
     * 链接地址
     */
    private String url;

    /**
     * 后缀名
     */
    private String suffix;

    /**
     * 大小
     */
    private long size;

}
