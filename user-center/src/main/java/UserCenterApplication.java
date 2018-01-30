import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.PrePersist;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by SunHaiyang on 2017/8/3.
 */
@EntityScan({"com.hafu365.fresh.core.entity"})
@EnableJpaRepositories({"com.hafu365.fresh.repository"})
@ComponentScan({"com.hafu365.fresh.service","com.hafu365.usercenter.controller","com.hafu365.usercenter.config"})
@SpringBootApplication
@EnableCaching
@EnableWebSecurity
@EnableTransactionManagement// 启动注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@EnableScheduling//启动注解定时任务
@ImportResource(locations={"classpath:Kaptcher.xml"})
public class UserCenterApplication {
    public static void main(String[] args){
        SpringApplication.run(UserCenterApplication.class,args);
    }

}
