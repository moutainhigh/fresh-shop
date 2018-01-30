package com.hafu365.fresh.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by HuangXueheng on 2017/8/9.
 */
@EntityScan("com.hafu365.fresh.core.entity")
@SpringBootApplication
@ComponentScan({"com.hafu365.fresh.service","com.hafu365.fresh.member.controller","com.hafu365.fresh.member.config"})
@EnableJpaRepositories("com.hafu365.fresh.repository")
public class Application {
    public void main(String [] args){
        SpringApplication.run(Application.class,args);

    }
}
