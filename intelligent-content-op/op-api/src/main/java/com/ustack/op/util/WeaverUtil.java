package com.ustack.op.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.com.google.gson.reflect.TypeToken;
import com.ustack.global.common.cache.RedisUtil;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import com.ustack.login.dto.TfDepInfoDTO;
import com.ustack.login.dto.TfSubCompanyInfoDTO;
import com.ustack.login.dto.TfUserInfoResultDTO;
import com.ustack.op.properties.WeaverProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 泛微工具类
 *
 *  认证过程主要采用RSA非对称加密算法
 *
 * @author tzf 2020/6/9
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WeaverUtil {
    /**
     * 模拟缓存服务
     */
    private final RedisUtil redisUtil;
    private final WeaverProperties weaverProperties;
    private static final Long TOKEN_TIME_OUT = 1800L;
    /**
     * 客户端RSA私钥
     */
    private static final String LOCAL_PRIVATE_KEY = "LOCAL_PRIVATE_KEY";
    /**
     * 客户端RSA公钥
     */
    private static final String LOCAL_PUBLIC_KEY = "LOCAL_PUBLIC_KEY";
    /**
     * ECOLOGY返回的系统公钥
     */
    private static final String SERVER_PUBLIC_KEY = "SERVER_PUBLIC_KEY";
    /**
     * ECOLOGY返回的系统密钥
     */
    private static final String SERVER_SECRET = "SERVER_SECRET";
    /**
     * 泛微token
     */
    private static final String SERVER_TOKEN = "SERVER_TOKEN";

    /**
     * 第一步：
     *
     * 调用ecology注册接口,根据appid进行注册,将返回服务端公钥和Secret信息
     */
    public Map<String,Object> regist(String address){
        //获取当前系统RSA加密的公钥
        RSA rsa = new RSA();
        String publicKey = rsa.getPublicKeyBase64();
        String privateKey = rsa.getPrivateKeyBase64();
        // 客户端RSA私钥
        redisUtil.set(LOCAL_PRIVATE_KEY,privateKey,-1);
        // 客户端RSA公钥
        redisUtil.set(LOCAL_PUBLIC_KEY,publicKey,-1);
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address + weaverProperties.getRegisterUrl())
                .header("appid",weaverProperties.getAppId())
                .header("cpk","123")
                .timeout(2000)
                .execute().body();
        // 打印ECOLOGY响应信息
        log.info("regist：{}", data);
        Map<String,Object> datas = JSONUtil.parseObj(data);
        //ECOLOGY返回的系统公钥
        redisUtil.set(SERVER_PUBLIC_KEY, StrUtil.nullToEmpty((String)datas.get("spk")),-1);
        //ECOLOGY返回的系统密钥
        redisUtil.set(SERVER_SECRET,StrUtil.nullToEmpty((String)datas.get("secrit")),-1);
        return datas;
    }

    /**
     * 第二步：
     *
     * 通过第一步中注册系统返回信息进行获取token信息
     */
    public Map<String,Object> getoken(String address){
        // 从系统缓存或者数据库中获取ECOLOGY系统公钥和Secret信息
        String secret = (String) redisUtil.get(SERVER_SECRET);
        String spk = (String) redisUtil.get(SERVER_PUBLIC_KEY);
        // 如果为空,说明还未进行注册,调用注册接口进行注册认证与数据更新
        if (Objects.isNull(secret)||Objects.isNull(spk)){
            regist(address);
            // 重新获取最新ECOLOGY系统公钥和Secret信息
            secret = (String) redisUtil.get(SERVER_SECRET);
            spk = (String) redisUtil.get(SERVER_PUBLIC_KEY);
        }
        // 公钥加密,所以RSA对象私钥为null
        RSA rsa = new RSA(null,spk);
        //对秘钥进行加密传输，防止篡改数据
        String encryptSecret = rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address+ weaverProperties.getGetTokenUrl())
                .header("appid",weaverProperties.getAppId())
                .header("secret",encryptSecret)
                .header("time", String.valueOf(TOKEN_TIME_OUT))
                .execute().body();
        log.info("getoken()：{}", data);
        Map<String,Object> datas = JSONUtil.parseObj(data);
        //ECOLOGY返回的token
        // TODO 为Token缓存设置过期时间
        redisUtil.set(SERVER_TOKEN,StrUtil.nullToEmpty((String)datas.get("token")), TOKEN_TIME_OUT);
        return datas;
    }

    /**
     * 第三步：
     *
     * 调用ecology系统的rest接口，请求头部带上token和用户标识认证信息
     *
     * @param address ecology系统地址
     * @param api rest api 接口地址(该测试代码仅支持GET请求)
     * @param jsonParams 请求参数json串
     *
     * 注意：ECOLOGY系统所有POST接口调用请求头请设置 "Content-Type","application/x-www-form-urlencoded; charset=utf-8"
     */
    public String restful(String address, String api, String jsonParams){
        //ECOLOGY返回的token
        String token= (String) redisUtil.get(SERVER_TOKEN);
        if (StrUtil.isEmpty(token)){
            token = (String) getoken(address).get("token");
        }
        String spk = (String) redisUtil.get(SERVER_PUBLIC_KEY);
        //封装请求头参数
        RSA rsa = new RSA(null,spk);
        //对用户信息进行加密传输,暂仅支持传输OA用户ID
        String encryptUserid = rsa.encryptBase64(ContextUtil.getUserId(),CharsetUtil.CHARSET_UTF_8,KeyType.PublicKey);
        //调用ECOLOGY系统接口，注意此处的disableCookie，可翻阅hutool的文档查看
        String data = HttpRequest
                .get(address + api)
                .header("appid",weaverProperties.getAppId())
                .header("token",token)
                .header("userid",encryptUserid)
                .body(jsonParams)
                .execute().body();
        log.info("restful()：{}", data);
        log.info("encryptUserid(): {}" , encryptUserid);
        return data;
    }

    public String getEncryptUserid(String userid){
        String spk = (String) redisUtil.get(SERVER_PUBLIC_KEY);
        //封装请求头参数
        RSA rsa = new RSA(null,spk);
        //对用户信息进行加密传输,暂仅支持传输OA用户ID
        return rsa.encryptBase64(userid,CharsetUtil.CHARSET_UTF_8,KeyType.PublicKey);
    }

    /**
     * 获取用户信息
     */
    public TfUserInfoResultDTO getHrmUserInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"jobtitlecode\": \"\",\n" +
                "        \"jobtitlename\": \"\",\n" +
                "        \"pagesize\": " + pagesize + ",\n" +
                "        \"curpage\": " + curpage + "\n" +
                "    }\n" +
                "}";

        HttpResponse res = HttpRequest.post(weaverProperties.getBaseUrl() + weaverProperties.getUserInfoUrl())
                .header("appid", weaverProperties.getAppId())
                .header("token", token)
                .header("userid", encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)) {
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()) {
            return null;
        }
        Gson gson = new Gson();
        String userInfoStr = (String) gson.toJson(parse.getData());
        if (StringUtils.isEmpty(userInfoStr)) {
            return null;
        }
        return JsonUtil.fromJson(userInfoStr, TfUserInfoResultDTO.class);
    }

    /**
     * 获取岗位信息
     */
    public TfUserInfoResultDTO getJobtitleInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"jobtitlecode\": \"\",\n" +
                "        \"jobtitlename\": \"\",\n" +
                "        \"pagesize\": " + pagesize + ",\n" +
                "        \"curpage\": " + curpage + "\n" +
                "    }\n" +
                "}";
        //获取岗位列表
        HttpResponse res = HttpRequest.post(weaverProperties.getBaseUrl() + weaverProperties.getJobInfoUrl())
                .header("appid", weaverProperties.getAppId())
                .header("token", token)
                .header("userid", encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)) {
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()) {
            return null;
        }
        Gson gson = new Gson();
        String str = (String) gson.toJson(parse.getData());
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return JsonUtil.fromJson(str, TfUserInfoResultDTO.class);
    }

    /**
     * 获取部门信息
     */
    public List<TfDepInfoDTO> getDepInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"departmentname\": \"\",\n" +
                "        \"departmentcode\": \"\",\n" +
                "        \"pagesize\": " + pagesize + ",\n" +
                "        \"curpage\": " + curpage + "\n" +
                "    }\n" +
                "}";
        //获取部门列表
        HttpResponse res = HttpRequest.post(weaverProperties.getBaseUrl() + weaverProperties.getDepInfoUrl())
                .header("appid", weaverProperties.getAppId())
                .header("token", token)
                .header("userid", encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)) {
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()) {
            return null;
        }
        Map map = (Map) parse.getData();
        Gson gson = new Gson();
        Type type = new TypeToken<List<TfDepInfoDTO>>() {
        }.getType();

        return map.get("dataList") != null ? gson.fromJson(gson.toJson(map.get("dataList")), type) : null;
    }

    /**
     * 获取分部信息
     */
    public List<TfSubCompanyInfoDTO> getSubCompanyInfo(String token, String encryptUserid, int pagesize, int curpage) {
        String postJson = "{\n" +
                "    \"params\": {\n" +
                "        \"subcompanyname\": \"\",\n" +
                "        \"subcompanycode\": \"\",\n" +
                "        \"pagesize\": " + pagesize + ",\n" +
                "        \"curpage\": " + curpage + "\n" +
                "    }\n" +
                "}";
        //获取分部列表
        HttpResponse res = HttpRequest.post(weaverProperties.getBaseUrl() + weaverProperties.getSubCompanyInfoUrl())
                .header("appid", weaverProperties.getAppId())
                .header("token", token)
                .header("userid", encryptUserid)
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)) {
            return null;
        }
        RestResponse parse = JsonUtil.fromJson(body, RestResponse.class);
        if (null == parse.getData()) {
            return null;
        }
        Map map = (Map) parse.getData();
        Gson gson = new Gson();
        Type type = new TypeToken<List<TfSubCompanyInfoDTO>>() {
        }.getType();

        return map.get("dataList") != null ? gson.fromJson(gson.toJson(map.get("dataList")), type) : null;
    }

    /**
     * 调用业务接口前，均需查看 - 接口token鉴权
     */
    public Boolean processPermissions(String token, String userId, String workflowId) {
        String postJson = "{\n" +
                "    \"datas\": {\n" +
                "        \"userId\": " + userId + ",\n" +
                "        \"workflowId\": " + workflowId + "\n" +
                "    }\n" +
                "}";
        //获取分部列表
        HttpResponse res = HttpRequest.post(weaverProperties.getBaseUrl() + weaverProperties.getProcessPermissionsUrl())
                .header("appid", weaverProperties.getAppId())
                .header("token", token)
                .header("userid", getEncryptUserid(userId))
                .body(postJson)
                .execute();
        String body = res.body();
        if (StringUtils.isBlank(body)) {
            return false;
        }
        Map map = JsonUtil.fromJson(body, Map.class);
        return null == map.get("existPermission") ? false : (boolean) map.get("existPermission");
    }
}
