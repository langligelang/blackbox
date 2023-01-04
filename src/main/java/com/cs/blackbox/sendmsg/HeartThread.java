package com.cs.blackbox.sendmsg;

import com.cs.blackbox.httpclient.MyHttpClient;
import com.cs.blackbox.redis.RedisBean;
import com.cs.blackbox.websocket.BlackWebsocket;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;

@Slf4j
public class HeartThread extends Thread {


    private String key;
    private Integer port;
    private String proxyIp;
    private Integer proxyPort;


    RedisTemplate redisTemplate = RedisBean.redis;


    MyHttpClient myHttpClient;


    public HeartThread(String key, Integer port, String proxyIp, Integer proxyPort) {
        this.key = key;
        this.port = port;
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
        myHttpClient = new MyHttpClient(proxyIp, proxyPort);
    }

    @Override
    public void run() {

        try {

            String uri = redisTemplate.opsForValue().get(key + "uri") == null ? "" : redisTemplate.opsForValue().get(key + "uri").toString();
            String resp = redisTemplate.opsForValue().get(key + "response") == null ? "" : redisTemplate.opsForValue().get(key + "response").toString();
            String content = redisTemplate.opsForValue().get(key + "httpContent") == null ? "" : redisTemplate.opsForValue().get(key + "httpContent").toString();
            String header_host = redisTemplate.opsForValue().get(key + "header_host") == null ? "" : redisTemplate.opsForValue().get(key + "header_host").toString();
            String method = redisTemplate.opsForValue().get(key + "method") == null ? "" : redisTemplate.opsForValue().get(key + "method").toString();

            if (!StringUtil.isNullOrEmpty(uri)) {
                redisTemplate.delete(key + "uri");
            }
            if (!StringUtil.isNullOrEmpty(uri)) {
                redisTemplate.delete(key + "method");
            }
            if (!StringUtil.isNullOrEmpty(header_host)) {
                redisTemplate.delete(key + "header_host");
            }
            if (!StringUtil.isNullOrEmpty(content)) {
                redisTemplate.delete(key + "httpContent");
            }
            if (!StringUtil.isNullOrEmpty(resp)) {
                redisTemplate.delete(key + "response");
            }
            //我们目前只考虑  application/x-www-form-urlencoded 这种情况的
            //我们拿到响应以后我们才发消息

            HashMap contentMap = new HashMap();
            if (!StringUtil.isNullOrEmpty(content)) {
                String[] contents = content.split("&");
                for (String c : contents) {
                    String[] text = c.split("=");
                    contentMap.put(text[0], text[1]);
                }
            }

            if (method.equals("POST")) {
                CloseableHttpResponse closeableHttpResponse = myHttpClient.doPost("https://" + header_host + uri, contentMap, null);
                HttpEntity resEntity = closeableHttpResponse.getEntity();
                String result = EntityUtils.toString(resEntity, "utf-8");
                closeableHttpResponse.close();
                BlackWebsocket.sendInfo("---------request uri-----------" + uri, port);
                BlackWebsocket.sendInfo("🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃", port);
                BlackWebsocket.sendInfo(UicodeBackslashU.unicodeToCn(resp), port);
                BlackWebsocket.sendInfo("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥", port);
                BlackWebsocket.sendInfo(UicodeBackslashU.unicodeToCn(result), port);
                BlackWebsocket.sendInfo("\n", port);
            } else if (method.equals("GET")) {
                CloseableHttpResponse closeableHttpResponse = myHttpClient.sendGet("https://" + header_host + uri, contentMap, null);
                HttpEntity resEntity = closeableHttpResponse.getEntity();
                String result = EntityUtils.toString(resEntity, "utf-8");
                closeableHttpResponse.close();
                BlackWebsocket.sendInfo("---------request uri-----------" + uri, port);
                BlackWebsocket.sendInfo("🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃🍃", port);
                BlackWebsocket.sendInfo(UicodeBackslashU.unicodeToCn(resp), port);
                BlackWebsocket.sendInfo("🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥", port);
                BlackWebsocket.sendInfo(UicodeBackslashU.unicodeToCn(result), port);
                BlackWebsocket.sendInfo("\n", port);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }
}
