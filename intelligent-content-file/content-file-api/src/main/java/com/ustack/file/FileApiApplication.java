package com.ustack.file;

import jakarta.servlet.MultipartConfigElement;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.unit.DataSize;

import java.io.File;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.ustack.file.mapper"})
@EnableAsync
@EnableDiscoveryClient
public class FileApiApplication {

    @Value("${file.temp.path}")
    private String tempPath;

    public static void main(String[] args) {
        SpringApplication.run(FileApiApplication.class, args);
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        File tempDir = new File(tempPath);
        if (!tempDir.exists()){
            tempDir.mkdirs();
        }
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // spring boot 上传需要一个临时目录
        factory.setLocation(tempPath);
        // 每次可上传的最大文件 300mb
        factory.setMaxFileSize(DataSize.ofMegabytes(300));
        // 每次可接受的请求体大小 300mb
        factory.setMaxRequestSize(DataSize.ofMegabytes(300));
        return factory.createMultipartConfig();
    }

}
