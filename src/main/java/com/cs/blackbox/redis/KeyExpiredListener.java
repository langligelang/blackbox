package com.cs.blackbox.redis;

import com.cs.blackbox.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Autowired
    public RedisTemplate<String,String> redisTemplate;

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        //获取失效key名称
        String expireKey = new String(message.getBody(), StandardCharsets.UTF_8);
        //获取key原本的value 获取不到 是null
        if(expireKey.startsWith("thread_")){
            Constant.threadMap.get(expireKey).interrupt();
            log.info("线程终止了"+expireKey);
            Constant.threadMap.remove(expireKey);
        }
        //String expireKeyValue = redisTemplate.opsForValue().get("myKey");
        log.info("expireKey---"+expireKey);



    }
}
