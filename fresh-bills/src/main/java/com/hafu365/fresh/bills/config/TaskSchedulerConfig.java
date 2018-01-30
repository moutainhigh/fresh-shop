package com.hafu365.fresh.bills.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by SunHaiyang on 2017/9/7.
 */
@Configuration
@ComponentScan({"com.hafu365.fresh.bills.task"})
@EnableScheduling
public class TaskSchedulerConfig {
}
