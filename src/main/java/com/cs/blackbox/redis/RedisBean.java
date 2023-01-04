package com.cs.blackbox.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedisBean {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public static RedisTemplate<String,String> redis;

    @PostConstruct
    public void getRedisTemplate(){
        redis=this.redisTemplate;
        log.info("初始化-------redisTemplate----");
    }

}
