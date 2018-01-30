package com.hafu365.fresh.core.utils;

/**
 * Created by HuangWeizhen on 2017/7/26.
 */
public class StringUtils {
    public static boolean isNotEmpty(String str){
        if(str != null && str.trim().length() != 0){
            return true;
        }
        return false;
    }
}
