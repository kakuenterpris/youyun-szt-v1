package com.ustack.global.common.http.clients;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


@Configuration
public class OkHttpConfiguration {

    private Integer connectTimeout=60;
    private Integer readTimeout=60;
    private Integer writeTimeout=60;
    private Integer maxIdleConnections=360;
    private Long keepAliveDuration=360L;
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                // 是否开启缓存
                .retryOnConnectionFailure(false)
                .connectionPool(pool())
                .connectTimeout(connectTimeout, TimeUnit.HOURS)
                .readTimeout(readTimeout, TimeUnit.HOURS)
                .writeTimeout(writeTimeout,TimeUnit.HOURS)
                .hostnameVerifier((hostname, session) -> true)
                // 设置代理
                // .proxy(new Proxy(Proxy.DesensitizeType.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                // 拦截器
                // .addInterceptor()
                .build();
    }
    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }
    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            // 信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Bean
    public ConnectionPool pool() {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
    }
}