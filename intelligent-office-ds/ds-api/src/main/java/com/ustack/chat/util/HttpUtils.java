package com.ustack.chat.util;


import com.alibaba.nacos.common.http.BaseHttpMethod;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author PingY
 * @Classname HttpUtils
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
@Slf4j
public class HttpUtils {
    private static final String UTF8 = "UTF-8";
    private static CloseableHttpClient httpClient;

    static {
        // 听见服务、流控组件连接池
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
        pool.setMaxTotal(600);//客户端总并行链接最大数
        pool.setDefaultMaxPerRoute(200);//每个主机的最大并行链接数
        httpClient = HttpClients.createMinimal(pool);
    }
    public static String doGet(String url, String token) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            httpGet.setHeader("Authorization", "Bearer " + token);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
                return resultString;
            }
            log.error("【GET】调用http请求没有权限状态码为：{},url地址为：【{}】,token为：【{}】", response.getStatusLine().getStatusCode(), url, token);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * 构造delete方法
     *
     * @param url
     * @param param
     * @param token
     * @return
     */
    public static String doDelete(String url, String urlParam, Map param, String token) {
        CloseableHttpResponse response = null;
        // 创建 HttpClient 实例
        CloseableHttpClient client = HttpClients.createDefault();
        // 地址栏参数
        url = url + "/" + URLEncoder.encode(urlParam, StandardCharsets.UTF_8);
        // 创建 HttpRequest 实例，指定 DELETE 方法并包含请求体
        BaseHttpMethod.HttpDeleteWithEntity request = new BaseHttpMethod.HttpDeleteWithEntity(url);
        request.setHeader("Authorization", "Bearer " + token);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        // 构造请求体
        String requestBody = "{\"user\": \"" + param.get("user") + "\"}";
        HttpEntity entity = new StringEntity(requestBody, "UTF-8");
        request.setEntity(entity);
        try {
            log.error("【DELETE】调用http请求,url地址为：【{}】,token为：【{}】,param为：【{}】", url, token, requestBody);
            response = client.execute(request);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            } else {
                log.error("DELETE请求失败：" + status.getReasonPhrase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DELETE请求失败");
            throw new RuntimeException();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return "";
    }


    /**
     * 构造delete方法
     *
     * @param url
     * @param token
     * @return
     */
    public static String doDelete(String url, String token) {
        CloseableHttpResponse response = null;
        // 创建 HttpClient 实例
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建 HttpRequest 实例，指定 DELETE 方法并包含请求体
        BaseHttpMethod.HttpDeleteWithEntity request = new BaseHttpMethod.HttpDeleteWithEntity(url);
        request.setHeader("Authorization", "Bearer " + token);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");

        try {
            log.error("【DELETE】调用http请求,url地址为：【{}】,token为：【{}】", url, token);
            response = client.execute(request);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                return EntityUtils.toString(responseEntity);
            } else {
                log.error("DELETE请求失败：" + status.getReasonPhrase());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DELETE请求失败");
            throw new RuntimeException();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                client.close();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return "";
    }

    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url
     * @return
     */
    public static String doPost(String url, String token) throws Exception {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.addHeader("Authorization", "Bearer " + token);


        String charSet = "UTF-8";
        StringEntity entity = new StringEntity("", charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;

        try {

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
//                System.out.println(jsonString);
                return jsonString;
            } else {
                System.err.println("请求返回:" + state + "(" + url + ")");
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * post请求(用于key-value格式的参数)
     *
     * @param url
     * @param params
     * @return
     */
    public static String doPost(String url, Map params, String token) {

        BufferedReader in = null;
        try {
            // 定义HttpClient
            HttpClient client = new DefaultHttpClient();
            // 实例化HTTP方法
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));
            request.setHeader("Accept", "*/*");
            request.addHeader("Content-type", "application/x-www-form-urlencoded");
            request.setHeader("Authorization", "Bearer " + token);

            //设置参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator iter = params.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String value = String.valueOf(params.get(name));
                nvps.add(new BasicNameValuePair(name, value));

                //System.out.println(name +"-"+value);
            }
            request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(), "utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                log.info("response:{}", sb.toString());
                return sb.toString();
            } else {
                log.info("状态码：{}", code);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String doPost(String url, Map param, String token, String type) throws IOException {
        HttpPost httpPost = null;
        String result = null;

        HttpClient client = new DefaultHttpClient();
        httpPost = new HttpPost(url);
        if (param != null) {
            Gson gson = new Gson();
            // 将Map转换为JSON字符串
            String jsonString = gson.toJson(param);
            StringEntity se = new StringEntity(jsonString, "utf-8");
            httpPost.setEntity(se); // post方法中，加入json数据
            httpPost.setHeader("Content-Type", "application/json");
        }
        if (!StringUtils.isEmpty(token)) {
            httpPost.setHeader("Authorization", "Bearer " + token);
        }
        HttpResponse response = client.execute(httpPost);
        if (response != null) {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                result = EntityUtils.toString(resEntity, "utf-8");
            }
        }
        return result;
    }

    public static String parseMapToPathParam(Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        try {
            Set<Map.Entry<String, Object>> entryset = param.entrySet();
            boolean isFirst = true;
            for (Map.Entry<String, Object> entry : entryset) {
                if (!isFirst) {
                    sb.append("&");
                } else {
                    isFirst = false;
                }
                sb.append(URLEncoder.encode(entry.getKey(), UTF8));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue().toString(), UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            log.error("HttpUtil parseMapToPathParam Exception!", e);
        }
        return sb.toString();
    }

    /**
     * 请求的upload接口, 发送音频创建转写订单
     *
     * @param url       请求地址
     * @param in        需要转写的音频流
     * @return 返回结果
     */
    public static String iflyrecUpload(String url,  InputStream in) {
        // 1、准备参数
        HttpPost httpPost = new HttpPost(url);
        // 设置超时时间, 防止504的时候会耗时30分钟
        RequestConfig requestConfig = RequestConfig.custom()
                //从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(5000)
                //与服务器连接超时时间, 指的是连接一个url的连接等待时间
                .setConnectTimeout(600000)
                // 读取超时, 指的是连接上一个url，获取response的返回等待时间
                .setSocketTimeout(600000).build();
        httpPost.setConfig(requestConfig);
        HttpEntity requestEntity = new InputStreamEntity(in, ContentType.APPLICATION_JSON);
        //System.out.println("---"+requestEntity);
        httpPost.setEntity(requestEntity);

        // 2、执行请求
        return doExecute(httpPost, null);
    }

    /**
     * 请求听见的获取结果接口
     *
     * @param url       请求路径
     * @return 返回结果
     */
    public static String iflyrecGet(String url) {
        // 1、准备参数
        HttpGet httpget = new HttpGet(url);
        // 2、执行请求
        return doExecute(httpget, UTF8);
    }
    /**
     * 执行网络请求
     *
     * @param requestBase http请求对象
     * @param charset     字符集
     * @return 返回结果
     */
    private static String doExecute(HttpRequestBase requestBase, String charset) {
        String result = null;
        try (CloseableHttpResponse response = httpClient.execute(requestBase)) {
            // 3、检查结果状态
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.error("网络异常");
                return null;
            }
            // 4、获取结果
            result = charset == null
                    ? EntityUtils.toString(response.getEntity())
                    : EntityUtils.toString(response.getEntity(), charset);
        } catch (Exception e) {
            log.error("网络异常", e);
        }
        return result;
    }

}
