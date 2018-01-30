package com.hafu365.fresh.setting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaihuilin on 2017/9/25  16:59.
 */
@RestController
@EntityScan(value = "com.hafu365.fresh.core.entity")
@ComponentScan({"com.hafu365.fresh.service", "com.hafu365.fresh.setting.controller", "com.hafu365.fresh.setting.config"})
@SpringBootApplication
@EnableJpaRepositories("com.hafu365.fresh.repository")
public class SettingApplication {

    public static void main(String[] args) {

        SpringApplication.run(SettingApplication.class, args);
    }

//    @RequestMapping("/")
//    public  String saveCart(){
//        return  "进到set模块";
//    }
}
