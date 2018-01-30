package com.hafu365.usercenter.config;

import com.hafu365.fresh.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.security.Key;
import java.util.concurrent.TimeUnit;

/**
 * Created by SunHaiyang on 2017/9/29.
 */
@Component
public class JedisUtils {
    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @Resource(name = "redisTemplate")
    ValueOperations<String,String> valueOperations;

    public String get(String key){
        String value = valueOperations.get(key);
        if(StringUtils.isNotEmpty(value)){
            return value;
        }
       return null;
    }

    public void set (String key,String value){
        valueOperations.set(key, value);
    }

    public void set(String key,String value,long outTime){
        valueOperations.set(key, value, outTime, TimeUnit.SECONDS);
    }
}
