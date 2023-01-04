package com.cs.blackbox.httpclient;

import com.cs.blackbox.constant.Constant;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Slf4j
public class MyHttpClient {

    private  CloseableHttpClient httpclient;
    public static final String CHARSET = "GBK";



    public MyHttpClient(String proxyIp, Integer port) {
            SSLConnectionSocketFactory sslConnectionSocketFactory = null;
            try {
                sslConnectionSocketFactory = new SSLConnectionSocketFactory(SSLContexts.custom().loadTrustMaterial(null, new
                        TrustSelfSignedStrategy()).build(), NoopHostnameVerifier.INSTANCE);
                RequestConfig config = RequestConfig.custom().setConnectTimeout(40000).setSocketTimeout(30000).setProxy(new HttpHost(proxyIp, port)).build();
                httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).setSSLSocketFactory(sslConnectionSocketFactory).build();
            }
        catch (Exception e) {
            log.info(e.getMessage());
        }
    }


    public CloseableHttpResponse doPost(String url, Map<String, Object> params,String cookie) throws IOException {
        List<NameValuePair> pairs = null;
        if (params != null && !params.isEmpty()) {
            pairs = new ArrayList<NameValuePair>(params.size());
            for (String key : params.keySet()) {
                String value = Constant.userTokenHashMap.get(key) == null ? "" : Constant.userTokenHashMap.get(key).toString();
                if(!StringUtil.isNullOrEmpty(value)){
                    pairs.add(new BasicNameValuePair(key, value));
                }else{
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
        }
        HttpPost httpPost = new HttpPost(url);
        if(null != cookie){
            httpPost.setHeader("Cookie",Constant.cookie);
        }
        Iterator iter = Constant.userHeaderToken.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            httpPost.setHeader(key.toString(), value.toString());
        }
        if (pairs != null && pairs.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, CHARSET));
        }
        CloseableHttpResponse response = httpclient.execute(httpPost);
        return response;
    }


    //发送普通get请求
    public CloseableHttpResponse sendGet(String url, Map<String, Object> params,String cookie) throws ParseException, IOException {
        if(params !=null && !params.isEmpty()){
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (String key :params.keySet()){
                String value = Constant.userTokenHashMap.get(key) == null ? "" : Constant.userTokenHashMap.get(key).toString();
                if(!StringUtil.isNullOrEmpty(value)){
                    pairs.add(new BasicNameValuePair(key, value));
                }else{
                    pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
                }
            }
            url +="?"+ EntityUtils.toString(new UrlEncodedFormEntity(pairs), CHARSET);
        }
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", Constant.cookie);
        Iterator iter = Constant.userHeaderToken.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            httpGet.setHeader(key.toString(), value.toString());
        }
        CloseableHttpResponse response = httpclient.execute(httpGet);
        return response;
    }





}
