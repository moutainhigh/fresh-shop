package com.hafu365.fresh.core.entity.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by SunHaiyang on 2017/8/31.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum CommonConstant {

    DATE_DAY(24*60*60*1000);
    private long value;
}
