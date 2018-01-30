package com.hafu365.fresh.core.utils;

/**
 * 创建表头信息   用于自动生成表头结构以及排序
 * Created by zhaihuilin on 2017/8/29  9:42.
 */
public class ExcelHeader implements Comparable<ExcelHeader> {
    /**
     * excel 的标题名称
     */
    private  String title;
    /**
     * 每一个标题的排序
     */
    private int orede;
    /**
     * 对应的方法名称
     */
    private  String methodName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrede() {
        return orede;
    }

    public void setOrede(int orede) {
        this.orede = orede;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public  ExcelHeader(String title,int orede,String methodName){
        super();
        this.title=title;
        this.orede=orede;
        this.methodName=methodName;
    }

    @Override
    public int compareTo(ExcelHeader o) {
        return orede>o.orede?1:(orede<o.orede?-1:0);
    }

























}
