package com.ustack.chat.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Token认证测试
 *
 *  认证过程主要采用RSA非对称加密算法
 *
 * @author tzf 2020/6/9
 */
public class MainTest {
    /**
     * 模拟缓存服务
     */
    private static final Map<String,String> SYSTEM_CACHE = new HashMap<>();
    /**
     * ecology系统发放的授权许可证(appid)
     */
    private static final String APPID = "your_appid";
    private static final String USERID = "your_userid";

    public static void main(String[] args) {
        testRestful("https://oa.example.com","/api/system/appmanage/route","");
        //获取token
//        testGetoken("https://oa.example.com/");
    }
    /**
     * 第一步：
     *
     * 调用ecology注册接口,根据appid进行注册,将返回服务端公钥和Secret信息
     */
    public static Map<String,Object> testRegist(String address){
        //获取当前系统RSA加密的公钥
        RSA rsa = new RSA();
        String publicKey = rsa.getPublicKeyBase64();
        String privateKey = rsa.getPrivateKeyBase64();
        // 客户端RSA私钥
        SYSTEM_CACHE.put("LOCAL_PRIVATE_KEY",privateKey);
        // 客户端RSA公钥
        SYSTEM_CACHE.put("LOCAL_PUBLIC_KEY",publicKey);
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address + "/api/ec/dev/auth/regist")
                .header("appid",APPID)
                .header("cpk","123")
                .timeout(2000)
                .execute().body();
        // 打印ECOLOGY响应信息
        System.out.println("testRegist()："+data);
        Map<String,Object> datas = JSONUtil.parseObj(data);
        //ECOLOGY返回的系统公钥
        SYSTEM_CACHE.put("SERVER_PUBLIC_KEY", StrUtil.nullToEmpty((String)datas.get("spk")));
        //ECOLOGY返回的系统密钥
        SYSTEM_CACHE.put("SERVER_SECRET",StrUtil.nullToEmpty((String)datas.get("secrit")));
        return datas;
    }

    /**
     * 第二步：
     *
     * 通过第一步中注册系统返回信息进行获取token信息
     */
    public static Map<String,Object> testGetoken(String address){
        // 从系统缓存或者数据库中获取ECOLOGY系统公钥和Secret信息
        String secret = SYSTEM_CACHE.get("SERVER_SECRET");
        String spk = SYSTEM_CACHE.get("SERVER_PUBLIC_KEY");
        // 如果为空,说明还未进行注册,调用注册接口进行注册认证与数据更新
        if (Objects.isNull(secret)||Objects.isNull(spk)){
            testRegist(address);
            // 重新获取最新ECOLOGY系统公钥和Secret信息
            secret = SYSTEM_CACHE.get("SERVER_SECRET");
            spk = SYSTEM_CACHE.get("SERVER_PUBLIC_KEY");
        }
        // 公钥加密,所以RSA对象私钥为null
        RSA rsa = new RSA(null,spk);
        //对秘钥进行加密传输，防止篡改数据
        String encryptSecret = rsa.encryptBase64(secret, CharsetUtil.CHARSET_UTF_8, KeyType.PublicKey);
        //调用ECOLOGY系统接口进行注册
        String data = HttpRequest.post(address+ "/api/ec/dev/auth/applytoken")
                .header("appid",APPID)
                .header("secret",encryptSecret)
                .header("time","3600")
                .execute().body();
        System.out.println("testGetoken()："+data);
        Map<String,Object> datas = JSONUtil.parseObj(data);
        //ECOLOGY返回的token
        // TODO 为Token缓存设置过期时间
        SYSTEM_CACHE.put("SERVER_TOKEN",StrUtil.nullToEmpty((String)datas.get("token")));
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
    public static String testRestful(String address,String api,String jsonParams){
        //ECOLOGY返回的token
        String token= SYSTEM_CACHE.get("SERVER_TOKEN");
        if (StrUtil.isEmpty(token)){
            token = (String) testGetoken(address).get("token");
        }
        String spk = SYSTEM_CACHE.get("SERVER_PUBLIC_KEY");
        //封装请求头参数
        RSA rsa = new RSA(null,spk);
        //对用户信息进行加密传输,暂仅支持传输OA用户ID
        String encryptUserid = rsa.encryptBase64(USERID,CharsetUtil.CHARSET_UTF_8,KeyType.PublicKey);
        //调用ECOLOGY系统接口，注意此处的disableCookie，可翻阅hutool的文档查看
        String data = HttpRequest
                .get(address + api)
                .header("appid",APPID)
                .header("token",token)
                .header("userid",encryptUserid)
                .body(jsonParams)
                .execute().body();
        System.out.println("testRestful()："+data);
        System.out.println("encryptUserid():" + encryptUserid);
        return data;
    }

    public static String getEncryptUserid(String userid){
        String spk = SYSTEM_CACHE.get("SERVER_PUBLIC_KEY");
        //封装请求头参数
        RSA rsa = new RSA(null,spk);
        //对用户信息进行加密传输,暂仅支持传输OA用户ID
        return rsa.encryptBase64(userid,CharsetUtil.CHARSET_UTF_8,KeyType.PublicKey);
    }

    public static String getAppid(){
        return APPID;
    }

    public static String getUserid(){
        return USERID;
    }
}
