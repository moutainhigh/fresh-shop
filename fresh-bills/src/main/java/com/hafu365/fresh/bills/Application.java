package com.hafu365.fresh.bills;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by SunHaiyang on 2017/8/24.
 */
@EntityScan({"com.hafu365.fresh.core.entity"})
@EnableJpaRepositories({"com.hafu365.fresh.repository"})
@ComponentScan({"com.hafu365.fresh.service","com.hafu365","com.hafu365.fresh.bills.config"})
@EnableWebSecurity
@EnableCaching
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
