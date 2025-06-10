package com.ustack.op.util;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class OKHttpUtils {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");


    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url 发送请求的 URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public Map doGet(String url, Map params) {
        log.info("调用ragflow方法get请求参数：{}", params);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Object entryObj : params.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryObj;
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }
        String finalUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(finalUrl)
                .get()
                .addHeader("Authorization", "Bearer "+url)
                .addHeader("Content-Type","application/json")
                .addHeader("Connection", "keep-alive ")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用ragflow方法get响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            return map;
        } catch (Exception e) {
            log.error("调用ragflow方法get，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param
     * @return 所代表远程资源的响应结果
     */
    public Map doPost(Request request){

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用ragflow方法post响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            return map;
        }catch (Exception e) {
            log.error("调用ragflow方法post失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }

    /**
     * 向指定 URL 发送PUT方法的请求
     *
     * @param url 发送请求的 URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public Map doPut(String url, Map params){
        Gson gson = new Gson();
        String json = gson.toJson(params);
        log.info("调用ragflow方法put请求参数：{}", json);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
               .url(url)
               .put(body)
                .addHeader("Content-Type", "application/json")
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用ragflow方法put响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            return map;
        }catch (Exception e) {
            log.error("调用ragflow方法put失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }

    /**
     * 向指定 URL 发送DELETE方法的请求
     *
     * @param url 发送请求的 URL
     * @param params 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public Map doDelete(String url, Map params){
        Gson gson = new Gson();
        String json = gson.toJson(params);
        log.info("调用ragflow方法delete请求参数：{}", json);
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, json);
        Request request = new Request.Builder()
                .url(url)
                .delete(body)
                .addHeader("Content-Type", "application/json")
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            byte[] responseBytes = response.body().bytes();
            String jsonString = new String(responseBytes);
            log.info("调用ragflow方法delete响应：{}", jsonString);
            if (StringUtils.isEmpty(jsonString)) {
                return null;
            }
            Map<String, Object> map = JSONUtil.toBean(jsonString, Map.class);
            return map;
        }catch (Exception e) {
            log.error("调用ragflow方法delete失败，失败原因：" + e.getMessage());
            e.getMessage();
            return null;
        } finally {
            response.close();
        }
    }



}
