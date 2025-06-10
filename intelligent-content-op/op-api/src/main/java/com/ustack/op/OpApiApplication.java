package com.ustack.op;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.ustack.op.mapper"})
@EnableAsync // 开启异步
@EnableScheduling // 开启定时任务
@EnableFeignClients
@EnableDiscoveryClient
public class OpApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpApiApplication.class, args);
    }

}
