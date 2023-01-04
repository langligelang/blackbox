package com.cs.blackbox.proxy;


import com.cs.blackbox.redis.RedisBean;
import com.cs.blackbox.sendmsg.HeartThread;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.intercept.common.FullResponseIntercept;
import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.proxy.ProxyType;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import com.github.monkeywie.proxyee.server.HttpProxyServerConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ProxyServer  {

    RedisTemplate redisTemplate = RedisBean.redis;

    public void startServer(Integer port,String proxyIP,Integer proxyPort){
        HttpProxyServerConfig config =  new HttpProxyServerConfig();
        config.setHandleSsl(true);
        new HttpProxyServer()
                .serverConfig(config)
                .proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
                    @Override
                    public void init(HttpProxyInterceptPipeline pipeline) {
                        pipeline.addLast(new FullResponseIntercept() {

                            @Override
                            public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                //在匹配到百度首页时插入js
                                return httpRequest.headers().get("HOST").contains("cs.com") && httpRequest.headers().get("Content-Type").contains("application/x-www-form-urlencoded");
                            }

                            @Override
                            public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, HttpProxyInterceptPipeline pipeline) {
                                ByteBuf buf = httpResponse.content().copy();
                                String requestUriKey = pipeline.getHttpRequest().uri()+"___"+port+"___"+"response";
                                if(redisTemplate.opsForValue().get(requestUriKey) == null ) {
                                    redisTemplate.opsForValue().set(pipeline.getHttpRequest().uri() + "___" + port + "___" + "response", buf.toString(io.netty.util.CharsetUtil.UTF_8),60, TimeUnit.SECONDS);
                                    buf.release();
                                    new HeartThread(pipeline.getHttpRequest().uri()+"___"+port+"___",port,proxyIP,proxyPort).run();
                                }
                            }
                            @Override
                            public void beforeRequest(Channel clientChannel, HttpContent httpContent, HttpProxyInterceptPipeline pipeline) throws Exception {
                                //在匹配到百度首页时插入js
                                log.info("-----------------headers------------------"+pipeline.getHttpRequest().headers().toString());
                                log.info("-----------------uri------------------"+pipeline.getHttpRequest().uri());
                                HttpContent httpContent1 = httpContent.copy();
                                //在这里存入redis
                                log.info("uri是:"+pipeline.getHttpRequest().uri());
                                log.info("headers是:"+pipeline.getHttpRequest().headers());
                                log.info("httpContent是:"+httpContent1);
                                redisTemplate.opsForValue().set(pipeline.getHttpRequest().uri()+"___"+port+"___"+"uri",pipeline.getHttpRequest().uri(),60, TimeUnit.SECONDS);
                                redisTemplate.opsForValue().set(pipeline.getHttpRequest().uri()+"___"+port+"___"+"method",pipeline.getHttpRequest().getMethod().name(),60, TimeUnit.SECONDS);
                                ByteBuf buf = httpContent1.content();
                                redisTemplate.opsForValue().set(pipeline.getHttpRequest().uri()+"___"+port+"___"+"httpContent",buf.toString(io.netty.util.CharsetUtil.UTF_8),60, TimeUnit.SECONDS);
                                buf.release();
                                redisTemplate.opsForValue().set(pipeline.getHttpRequest().uri()+"___"+port+"___"+"header_host",pipeline.getHttpRequest().headers().get("HOST"),60, TimeUnit.SECONDS);
                                pipeline.beforeRequest(clientChannel, httpContent);
                            }
                        });
                    }
                }).proxyConfig(new ProxyConfig(ProxyType.SOCKS5, proxyIP, proxyPort))
                .start(port);
    }
}



