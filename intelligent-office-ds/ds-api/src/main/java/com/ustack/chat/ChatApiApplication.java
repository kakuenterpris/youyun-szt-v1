package com.ustack.chat;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.ustack.chat.mapper"})
@EnableAsync // 开启异步
@EnableScheduling // 开启定时任务
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableRabbit
@EnableFeignClients(basePackages = "com.ustack.feign.client")
public class ChatApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(ChatApiApplication.class, args);
    }


}
