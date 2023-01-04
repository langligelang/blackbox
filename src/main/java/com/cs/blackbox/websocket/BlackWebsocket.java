package com.cs.blackbox.websocket;

import com.cs.blackbox.redis.RedisBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value="/{jiraId}")
@Component
@Slf4j
public class BlackWebsocket {
    /**
     * 存放所有在线的客户端
     */

    static RedisTemplate redisTemplate = RedisBean.redis;


    @PostConstruct
    public void init() {
        System.out.println("websocket 加载");
    }
    private static Map<String, Session> clients = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(@PathParam("jiraId") String jiraId, Session session) {
        log.info("有新的客户端连接了: {}", session.getId());
        //将新用户存入在线的组
        clients.put(jiraId,session);
    }

    /**
     * 客户端关闭
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("有用户断开了, id为:{}", session.getId());
        //将掉线的用户移除在线的组里
        clients.remove(session.getId());
    }

    /**
     * 发生错误
     * @param throwable e
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    /**
     * 收到客户端发来消息
     * @param message  消息对象
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("服务端收到客户端发来的消息: {}", message);
        this.sendInfo(message,6667);
    }

    /**
     * 群发消息
     * @param message 消息内容
     */
    public static synchronized void sendInfo(String message,Integer port) throws IOException {
        for (Map.Entry<String, Session> sessionEntry : clients.entrySet()) {
            String userName = (String) redisTemplate.opsForValue().get("thread_"+port);
            if (sessionEntry.getKey().equals(userName)) {
                sessionEntry.getValue().getBasicRemote().sendText(message);
            }
        }
    }
}
