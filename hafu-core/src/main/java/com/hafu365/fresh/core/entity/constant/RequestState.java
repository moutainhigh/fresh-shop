package com.hafu365.fresh.core.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 访问状态
 * Created by SunHaiyang on 2017/7/21.
 */
@AllArgsConstructor
@Getter
public enum RequestState {

    /**
     * 访问出错
     */
    ERROR(400),
    /**
     * 访问成功
     */
    SUCCESS(200),
    /**
     * 权限异常
     */
    AUTHENTICATION_ERROR(403);

    /**
     * 状态码
     */
    private final int stateCode;


}
