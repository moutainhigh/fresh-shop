package com.hafu365.fresh.voucher.task;

import com.hafu365.fresh.core.entity.setting.BasicSetup;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.basicSetup.BasicSetupRepository;
import lombok.extern.log4j.Log4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时查库，并更新任务
 * Created by zhaihuilin on 2017/9/24  13:27.
 */
@Service
@Transactional
@Log4j
public class ScheduleRefreshDatabase {
    @Autowired
    private BasicSetupRepository basicSetupRepository;

    @Resource(name = "jobDetail")
    private JobDetail jobDetail;

    @Resource(name = "jobTrigger")
    private CronTrigger cronTrigger;

    @Resource(name = "scheduler")
    private Scheduler scheduler;

    @Scheduled(fixedRate=60000) // 每隔60s查库，并根据查询结果决定是否重新设置定时任务
    public void scheduleUpdateCronTrigger() throws SchedulerException {
//        log.info("我进来了");
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(cronTrigger.getKey());
        String currentCron = trigger.getCronExpression();// 当前Trigger使用的
        String searchCron ="";//声明一个String 类型
        List<BasicSetup> basicSetupList= basicSetupRepository.findAll();//获取 所有的list 集合
        if (basicSetupList.size()>0){
               for (BasicSetup basicSetup:basicSetupList){
                   String KeyNames=basicSetup.getKeyNames();
                   if (KeyNames.equals("VOUCHER_PAST_TIME")){
                       searchCron=basicSetup.getKeyCode();
                   }
               }
        }else{
            searchCron="0 0 3 * * ?" ;
        }
        if (!StringUtils.isNotEmpty(searchCron)) {
            searchCron = "0 0 3 * * ?";
        }
        log.info(currentCron);
        log.info(searchCron);
        if (currentCron.equals(searchCron)) {
            // 如果当前使用的cron表达式和从数据库中查询出来的cron表达式一致，则不刷新任务
        } else {
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(searchCron);
            // 按新的cronExpression表达式重新构建trigger
            trigger = (CronTrigger) scheduler.getTrigger(cronTrigger.getKey());
            trigger = trigger.getTriggerBuilder().withIdentity(cronTrigger.getKey())
                    .withSchedule(scheduleBuilder).build();
            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(cronTrigger.getKey(), trigger);
            currentCron = searchCron;
        }
    }
}
