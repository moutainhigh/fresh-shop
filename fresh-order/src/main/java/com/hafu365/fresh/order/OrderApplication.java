package com.hafu365.fresh.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EntityScan(value = "com.hafu365.fresh.core.entity")
@ComponentScan({"com.hafu365.fresh.service","com.hafu365.fresh.order.controller","com.hafu365.fresh.order.config"})
@SpringBootApplication
@EnableJpaRepositories("com.hafu365.fresh.repository")
@EnableScheduling     //开启对计划任务的支持
public class OrderApplication {
	public static void main(String[] args) {

		SpringApplication.run(OrderApplication.class, args);
	}
//
//	@RequestMapping("/")
//	public  String saveCart(){
//		return  "进到order模块";
//	}
}
