package com.hafu365.fresh.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;

/**
 * jasoer report 配置 class
 * Created by zhaihuilin on 2017/8/24  13:16.
 */
@Configuration
public class JasperConfig extends WebMvcConfigurerAdapter {

    private final static String REPORT_DATA_KEY = "datasource";
    private final static String PATH_KEY = "classpath:jaspertemplate/";
    private final static String TYPE_KEY = ".jrxml";
    private final static String VIEW_KEY = "Report";

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
        super.configureDefaultServletHandling(configurer);
    }

    @Bean
    public JasperReportsViewResolver getjasperReportsViewResolver(){
         JasperReportsViewResolver resolver=new JasperReportsViewResolver();
         resolver.setPrefix(PATH_KEY); // resource 文件夹下放模板的路劲
         resolver.setSuffix(TYPE_KEY);// 模板文件的类型，这里选用jreml 而不是编译之后的jasper
         //JasperReportsMultiFormatView定义了ReportDataKey，这里给定key为datasource，后面controller的时候会用到
         resolver.setReportDataKey(REPORT_DATA_KEY);
         resolver.setViewNames("*"+VIEW_KEY+"*"); // //视图名称，模板名称需要符合 *你定义的key* 如*Report*
         resolver.setViewClass(JasperReportsMultiFormatView.class);//视图类
         resolver.setOrder(0);//顺序为第一位
         return  resolver;
    }





















}
