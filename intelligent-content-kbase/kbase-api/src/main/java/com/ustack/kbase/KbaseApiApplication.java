package com.ustack.kbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableDiscoveryClient
public class KbaseApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(KbaseApiApplication.class, args);
	}

}
