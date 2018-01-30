package com.hafu365.fresh.goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by HuangWeizhen on 2017/8/1.
 */

@EnableWebSecurity
@EnableCaching
@EntityScan(value = "com.hafu365.fresh.core.entity")//此注解实体类生成表用
@SpringBootApplication
@ComponentScan({"com.hafu365.fresh.service","com.hafu365.fresh.goods.controller","com.hafu365.fresh.goods.config"})
@EnableJpaRepositories("com.hafu365.fresh.repository")
@EnableScheduling
public class App {
    public void main(String[] args){
        SpringApplication.run(App.class,args);
    }
}
