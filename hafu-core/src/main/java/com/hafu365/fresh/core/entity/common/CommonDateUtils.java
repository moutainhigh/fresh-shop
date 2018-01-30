package com.hafu365.fresh.core.entity.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间转换
 * Created by SunHaiyang on 2017/8/31.
 */
public class CommonDateUtils {

    /**
     * 转换时间戳
     *
     * @param dateFormat 时间格式 例如(yyyyMMdd)
     * @param dateStr    时间值 例如(20170931)
     * @return 指定日期的时间戳
     */
    public static long toUnix(
            final String dateFormat, final String dateStr
    ) {
        long unix = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            Date date = simpleDateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            unix = calendar.getTimeInMillis() / 1000 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unix;
    }

    /**
     * 计算日期时间戳
     *
     * @param dateFormat 时间格式 例如(yyyyMMdd)
     * @param dateStr    时间值 例如(20170931)
     * @param day        日期
     * @return 指定日期的时间戳
     */
    public static long countDateToUnix(
            final String dateFormat, final String dateStr, final int day
    ) {
        long unix = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            Date date = simpleDateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + day);
            unix = calendar.getTimeInMillis() / 1000 * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unix;
    }

    /**
     * 获取指定日的时间戳
     *
     * @param day 日
     * @return
     */
    public static long getDayUnix(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONDAY), day, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    /**
     * 获取月的时间错
     * @param monday
     * @return
     */
    public static long getMondayUnix(int monday) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), monday, 1, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    /**
        获取当月月份
     */
    public static int getMonday(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONDAY);
    }
    /**
     获取当日日期
     */
    public static int getDay(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }
    /**
     获取当年年份
     */
    public static int getYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }
}
