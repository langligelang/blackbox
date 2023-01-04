package com.cs.blackbox.contorller;


import com.alibaba.fastjson.JSONObject;
import com.cs.blackbox.beans.BlackScan;
import com.cs.blackbox.constant.Constant;
import com.cs.blackbox.dao.BlackScanDao;
import com.cs.blackbox.proxy.ProxyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Controller
public class BlackContorller extends BaseController {

    @Autowired
    BlackScanDao blackScanDao;

    @Autowired
    StringRedisTemplate redisTemplate;



    @RequestMapping(value = "/testbill", method = {RequestMethod.POST})
    @ResponseBody
    public String testBill(BlackScan blackScan) {
        blackScan.setId(UUID.randomUUID().toString().replaceAll("-",""));
        blackScan.setRegTime(new Timestamp(new Date().getTime()));
        blackScanDao.save(blackScan);

        String wsAddress = "ws://ws.cs.com:8886/"+blackScan.getUsername();
        Map map = new HashMap();
        map.put("status", 0);
        map.put("msg", "成功");
        Map map1 = new HashMap();
        map.put("data",map1);
        map1.put("wsaddress",wsAddress);
        //这里分配端口和ws地址  从9000开始分配  分配到9010
        int flag = 0;
        for(int i=9000;i<=9015;i++){
            Object value = redisTemplate.opsForValue().get("thread_"+i);
            if(null != value){
                continue;
            }else{
                flag=1;
                redisTemplate.opsForValue().set("thread_"+i,blackScan.getUsername(),30,TimeUnit.MINUTES);
                map1.put("wsaddress",wsAddress+"  代理端口是："+i);
                //开启服务
                    int finalI = i;
                    Thread thread = new Thread() {
                        public void run() {
                            new ProxyServer().startServer(finalI, blackScan.getProxyIp(), blackScan.getPort());
                        }
                    };
                    Constant.threadMap.put("thread_" + i, thread);
                    Constant.threadMap.get("thread_" + i).start();
                    break;
            }
        }
        if(flag==0){
            map1.put("wsaddress","暂时没有端口，请等待释放");
        }
        String jsonStr = JSONObject.toJSONString(map);
        log.info(jsonStr);
        return jsonStr;
    }



}