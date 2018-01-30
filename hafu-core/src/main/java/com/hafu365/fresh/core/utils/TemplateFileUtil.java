package com.hafu365.fresh.core.utils;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 获取模板文件的工具类 【Excle】
 * Created by zhaihuilin on 2017/8/29  9:55.
 */
public class TemplateFileUtil {

    /**
     * 获取 excle 模板文件
     * @param tempName
     * @return
     * @throws FileNotFoundException
     */
     public static FileInputStream getTemplates(String tempName) throws FileNotFoundException{

         return  new FileInputStream(ResourceUtils.getFile("classpath:excel-templates/"+tempName));
     }

    /**
     * 获取生成二维码所需要的logo图
     * @param tempName
     * @return
     * @throws FileNotFoundException
     */
     public static File getQrecodeLogo(String tempName) throws FileNotFoundException{
          return  ResourceUtils.getFile("classpath:/qrcode-logo/"+tempName);
     }

}
