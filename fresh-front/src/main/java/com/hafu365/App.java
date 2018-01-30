package com.hafu365;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EntityScan({"com.hafu365.fresh.core.entity"})
@EnableJpaRepositories({"com.hafu365.fresh.repository"})
@ComponentScan({"com.hafu365.fresh.service","com.hafu365"})
@EnableWebSecurity
@EnableCaching
@SpringBootApplication
public class App
{
    public static void main( String[] args )
    {
        SpringApplication.run(App.class,args);
    }

}
